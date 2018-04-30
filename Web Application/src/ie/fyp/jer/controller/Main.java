package ie.fyp.jer.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ie.fyp.jer.config.LogCookie;
import ie.fyp.jer.model.Database;
import ie.fyp.jer.model.Logged;
import ie.fyp.jer.model.MobileResponse;

/**
 * Servlet implementation class Main
 */
@WebServlet("/index.jsp")
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Main() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Cookie cookie = checkCookie(request.getCookies());
		if(cookie==null)
			cookie = (Cookie) request.getAttribute("cookie");
		if(request.getSession().getAttribute("logged")!=null) {
			request.setAttribute("website", "IoT Efficiency");
			request.setAttribute("main", "main");
			request.setAttribute("subtitle", "Dashboard");
			int log = ((Logged)request.getSession().getAttribute("logged")).getId();
			setAccount(log, request);
			setLastLog(log, request);
			setHouse(log, request);
			request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
		}
		else if(cookie!=null) {
			String ip = request.getRemoteAddr();
			String user = request.getHeader("User-Agent");
			Logged log = LoginCookies(cookie, ip, user, response);
			String type = request.getParameter("type");
			if(log!=null) {
				request.getSession().setAttribute("logged", log);
				if(type!=null&&type.equals("mobile")) {
					int code = request.getSession().getAttribute("logged")!=null ? 1 : 0;
					MobileResponse mResponse = new MobileResponse(log, code);
					response.getWriter().write(new Gson().toJson(mResponse));
				}
				else {
					String next = request.getParameter("path");
					if(next==null)
						next="";
					response.sendRedirect(next);
				}
			}
			else
				request.getRequestDispatcher("/WEB-INF/homepage.jsp").forward(request, response); 
		}
		else
			request.getRequestDispatcher("/WEB-INF/homepage.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private Cookie checkCookie(Cookie cookies[]) {
		if(cookies!=null)
			for(int i=0; i<cookies.length; i++)
				if(cookies[i].getName().equals("login"))
					return cookies[i];
		return null;
	}

	private Logged LoginCookies(Cookie cookie, String ip, String user, HttpServletResponse response) {
		String sql = "SELECT a.email, a.id, l.device " + 
				"FROM FYP.Account a " + 
				"JOIN FYP.Login l " + 
				"ON l.accountId = a.id " + 
				"WHERE l.cookie = ? " + 
				"AND l.expire > ?" +
				"AND l.type != ?;";
		Object values[] = {cookie.getValue(), System.currentTimeMillis(), "Login Attempt"};
		Database db = new Database(dataSource);
		Logged log = db.getLogged(sql, values);
		if(log!=null) {
			log.setBuildings(setHouses(log.getId()));
			setLogin(ip, log.getType(), log.getId(), user, response);
		}
		return log;
	}

	private void setLogin(String ip, String type, int id, String user, HttpServletResponse httpResponse) {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet("http://ip-api.com/json/" + ip);
			CloseableHttpResponse response = httpclient.execute(httpGet);
			String location = "Unknown";
			try {
				HttpEntity entity = response.getEntity();
				String gson = EntityUtils.toString(entity);
				JsonParser parse = new JsonParser();
				JsonObject object = parse.parse(gson).getAsJsonObject();
				location = (object.get("city").getAsString() + " " + object.get("countryCode").getAsString());
				EntityUtils.consume(entity);
			} catch (Exception e) {
				System.out.println("GSON error occured in session controller - IP is likely local.");
			} finally {
				response.close();
			}
			Long expire = System.currentTimeMillis() + ((long)1000 * 60 * 60 * 24 * 30);
			String device = type;
			String cookie = LogCookie.generate();
			Object values[] = {id, System.currentTimeMillis(), location, user, device, cookie, expire, "Session"};
			String sql = "INSERT INTO FYP.Login(accountid, datetime, location, osbrowser, device,"
					+ "cookie, expire, type)VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
			if(execute(sql, values)>0)
				httpResponse.addCookie(createCookie("login", cookie, 60*60*24*30));
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Cookie createCookie(String name, String details, int life) {
		Cookie temp = new Cookie(name, details);
		temp.setMaxAge(life);
		return temp;
	}

	private ArrayList<String> setHouses(int id) {
		String sql = "SELECT name FROM FYP.building WHERE accountId = ?";
		Object values[] = {id};
		Database db = new Database(dataSource);
		return db.getHouses(sql, values);
	}

	private void setAccount(int log, HttpServletRequest request) {
		String sql ="SELECT a.*, p.date, COUNT(b.name) " + 
				"FROM FYP.Account a " + 
				"LEFT JOIN FYP.Building b ON b.accountId = a.id " + 
				"JOIN FYP.Password p ON p.accountId = a.id " +
				"WHERE a.id = ? " + 
				"GROUP BY a.id, p.date " + 
				"ORDER BY p.date DESC " + 
				"LIMIT 1;";
		Object values[] = {log};
		Database db = new Database(dataSource);
		String details[] = db.getDetails(sql, values);
		if(details.length==10) {
			request.setAttribute("firstName", details[0]);
			request.setAttribute("lastName", details[1]);
			request.setAttribute("email", details[2]);
			request.setAttribute("phone", details[3]);
			request.setAttribute("street", details[4]);
			request.setAttribute("town", details[5]);
			request.setAttribute("county", details[6]);
			request.setAttribute("regDate", details[7]);
			request.setAttribute("lastPas",details[8]);
			request.setAttribute("houses", details[9]);
		}
	}

	private void setLastLog(int log, HttpServletRequest request) {
		String sql = "SELECT dateTime, location, osBrowser, device " + 
				"FROM FYP.Login " + 
				"WHERE dateTime IN(SELECT dateTime " + 
				"				FROM FYP.Login " + 
				"				WHERE accountId = ? " + 
				"				AND type = ?" +
				"				ORDER BY dateTime DESC" +
				"				LIMIT 1 OFFSET 1);";
		Object values[] = {log, "Login"};
		Database db = new Database(dataSource);
		String details[] = db.getLastLog(sql, values);
		if(details.length==4) {
			request.setAttribute("prev", details[0]);
			request.setAttribute("lastLog", details[1]);
			request.setAttribute("location", details[2]);
			request.setAttribute("system", details[3]);
		}
	}

	private void setHouse(int log, HttpServletRequest request) {
		String sql = "SELECT name, location, id " + 
				"FROM FYP.building " + 
				"WHERE accountId = ?";
		Object values[] = {log};
		Database db = new Database(dataSource);
		request.setAttribute("houseDash", db.setHouse(sql, values));
	}

	private int execute(String sql, Object...values) {
		Database db = new Database(dataSource);
		return db.execute(sql, values);
	}
}
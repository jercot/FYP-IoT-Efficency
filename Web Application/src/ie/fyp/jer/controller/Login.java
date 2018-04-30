package ie.fyp.jer.controller;

import java.io.IOException;

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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ie.fyp.jer.config.LogCookie;
import ie.fyp.jer.config.SecondFactor;
import ie.fyp.jer.model.Database;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 5L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getSession().getAttribute("cookieS")!=null&&request.getAttribute("safety")!=null) {
			request.getSession().setAttribute("attempt", 0);
			response.sendRedirect("authenticate");
		}
		else if (request.getSession().getAttribute("cookieS")!=null) {
			request.setAttribute("cookie", createCookie("login", (String)request.getSession().getAttribute("cookieS"), 60*60*24*30));
			request.getRequestDispatcher("/index.jsp").forward(request, response);
		}
		else if(request.getSession().getAttribute("logged")!=null)
			response.sendRedirect("");
		else
			request.getRequestDispatcher("/WEB-INF/homepage.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String type = request.getParameter("type");
		if(type==null||type.equals(""))
			type = "browser";
		request.getSession().setAttribute("cookieS", login(type, request, response));
		doGet(request, response);
	}

	private String login(String type, HttpServletRequest request, HttpServletResponse response) {
		String email = request.getParameter("email");
		String password = request.getParameter("pass");
		if(!checkAttempts(email))
			request.setAttribute("message", "Too many attempts, Please try again later.");
		else if(email!=null&&password!=null) {
			String ip = request.getRemoteAddr();
			String user = request.getHeader("User-Agent");
			String device = request.getParameter("device");
			String sql = "SELECT a.email, a.id, a.twoStep, p.password, p.date " + 
					"FROM FYP.Account a " + 
					"LEFT JOIN FYP.Password p ON a.id = p.accountId " + 
					"WHERE UPPER(a.email) = UPPER(?) " + 
					"ORDER BY date DESC " + 
					"LIMIT 1;";
			Object values[] = {email};
			Database db = new Database(dataSource);
			int id = db.getLogin(sql, password, device, values);
			if(id!=-1&&db.isSuccess()) {
				if(db.isSafety()&&!type.equals("mobile")) {
					request.setAttribute("safety", "on");
					request.getSession().setAttribute("code", setCode(id));
				}
				return setLogin(ip, type, id, user, "Login", response);
			}
			else {
				request.setAttribute("message", "Password or Email incorrect");
				return setLogin(ip, type, id, user, "Login Attempt", response);
			}
		}
		return null;
	}

	private int setCode(int log) {
		int code = SecondFactor.generate();
		String sql = "INSERT INTO FYP.Token(accountId, expire, code) VALUES (?, ?, ?)";
		Object values[] = {log, System.currentTimeMillis()+300000, code};
		execute(sql, values);
		return code;
	}

	private boolean checkAttempts(String email) {
		String sql = "SELECT COUNT(*) " + 
				"FROM FYP.Login " + 
				"WHERE accountid IN(SELECT id " + 
				"				FROM FYP.Account " + 
				"				WHERE UPPER(email) = UPPER(?)) " + 
				"				AND type = ? " + 
				"				AND datetime > ?;";
		Object values[] = {email, "Login Attempt", System.currentTimeMillis()-300000};
		Database db = new Database(dataSource);
		return db.getAttempts(sql, values)<5;
	}

	private String setLogin(String ip, String type, int id, String user, String login, HttpServletResponse httpResponse) {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet("http://ip-api.com/json/" + ip);
			CloseableHttpResponse response = httpclient.execute(httpGet);
			String location = "Unknown";

			HttpEntity entity = response.getEntity();
			String gson = EntityUtils.toString(entity);
			JsonParser parse = new JsonParser();
			JsonObject object = parse.parse(gson).getAsJsonObject();
			try {
				location = (object.get("city").getAsString() + " " + object.get("countryCode").getAsString());
			} catch (Exception e) {
				System.out.println("GSON error occured in login controller - IP is likely local.");
			}
			response.close();
			EntityUtils.consume(entity);
			Long expire = System.currentTimeMillis() + ((long)1000 * 60 * 60 * 24 * 30);
			String device = type;
			String cookie = "None";
			if(login.equals("Login"))
				cookie = LogCookie.generate();
			Object values[] = {id, System.currentTimeMillis(), location, user, device, cookie, expire, login};
			String sql = "INSERT INTO FYP.Login(accountid, datetime, location, osbrowser, device,"
					+ "cookie, expire, type)VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
			if(execute(sql, values)>0&&login.equals("Login"))
				return cookie;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Cookie createCookie(String name, String details, int life) {
		Cookie temp = new Cookie(name, details);
		temp.setMaxAge(life);
		return temp;
	}

	private int execute(String sql, Object...values) {
		Database db = new Database(dataSource);
		return db.execute(sql, values);
	}
}
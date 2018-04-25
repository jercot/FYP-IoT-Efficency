package ie.fyp.jer.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ie.fyp.jer.config.LogCookie;
import ie.fyp.jer.config.SecondFactor;

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
			request.getSession().setAttribute("code", SecondFactor.generate());
			request.getSession().setAttribute("attempt", 0);
			response.sendRedirect("authenticate");
		}
		else if (request.getSession().getAttribute("cookieS")!=null) {
			response.addCookie(createCookie("login", (String)request.getSession().getAttribute("cookieS"), 60*60*24*30));
			response.sendRedirect("");
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
		String email = request.getParameter("email");
		String password = request.getParameter("pass");
		String ip = request.getRemoteAddr();
		String user = request.getHeader("User-Agent");
		String type = request.getParameter("type");
		if(type==null||type.equals(""))
			type = "browser";
		request.getSession().setAttribute("cookieS", login(email, password, ip, user, type, request, response));
		doGet(request, response);
	}

	private String login(String email, String password, String ip, String user, String type, HttpServletRequest request, HttpServletResponse response) {
		if(!checkAttempts(email))
			request.setAttribute("message", "Too many attempts, Please try again later.");
		else if(email!=null&&password!=null) {
			String sql = "SELECT a.email, a.id, a.twoStep, p.password, p.date " + 
					"FROM FYP.Account a " + 
					"LEFT JOIN FYP.Password p ON a.id = p.accountId " + 
					"WHERE UPPER(a.email) = UPPER(?) " + 
					"ORDER BY date DESC " + 
					"LIMIT 1;";
			Object val[] = {email};
			try (Connection con = dataSource.getConnection();
					PreparedStatement ptst = prepare(con, sql, val);
					ResultSet rs = ptst.executeQuery()) {
				if(rs.next()) {
					if(BCrypt.checkpw(password, rs.getString(4))) {
						if(rs.getString(3)!=null)
							request.setAttribute("safety", "on");
						return setLogin(con, ip, type, rs.getInt(2), user, "Login", response);
					}
					else {
						request.setAttribute("message", "Password or Email incorrect");
						return setLogin(con, ip, type, rs.getInt(2), user, "Login Attempt", response);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private boolean checkAttempts(String email) {
		String sql = "SELECT COUNT(*) " + 
				"FROM FYP.Login " + 
				"WHERE accountid IN(SELECT id " + 
				"				FROM FYP.Account " + 
				"				WHERE UPPER(email) = UPPER(?)) " + 
				"				AND type = ? " + 
				"				AND datetime > ?;";
		Object val[] = {email, "Login Attempt", System.currentTimeMillis()-300000};
		try(Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, val);
				ResultSet rs = ptst.executeQuery()) {
			if(rs.next())
				if(rs.getInt(1)<5)
					return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private String setLogin(Connection con, String ip, String type, int id, String user, String login, HttpServletResponse httpResponse) throws ClientProtocolException, IOException, SQLException {
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
		Object val3[] = {id, System.currentTimeMillis(), location, user, device, cookie, expire, login};
		String sql = "INSERT INTO FYP.Login(accountid, datetime, location, osbrowser, device,"
				+ "cookie, expire, type)VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		try (PreparedStatement ptst = prepare(con, sql, val3)) {
			if(ptst.executeUpdate()==1&&login.equals("Login"))
				return cookie;
		}
		return null;
	}
	
	private Cookie createCookie(String name, String details, int life) {
		Cookie temp = new Cookie(name, details);
		temp.setMaxAge(life);
		return temp;
	}

	private PreparedStatement prepare(Connection con, String sql, Object values[]) throws SQLException {
		final PreparedStatement ptst = con.prepareStatement(sql);
		for (int i = 0; i < values.length; i++) {
			ptst.setObject(i+1, values[i]);
		}
		return ptst;
	}
}
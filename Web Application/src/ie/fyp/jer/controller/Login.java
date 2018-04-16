package ie.fyp.jer.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ie.fyp.jer.config.LogCookie;
import ie.fyp.jer.domain.Logged;
import ie.fyp.jer.domain.MobileResponse;

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
		if(request.getSession().getAttribute("logged")!=null)
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
		if(type==null)
			type = "browser";
		request.getSession().setAttribute("logged", login(email, password, ip, user, type, response));
		if(type.equals("mobile")) {
			int code = request.getSession().getAttribute("logged")!=null ? 1 : 0;
			MobileResponse mResponse = new MobileResponse((Logged)request.getSession().getAttribute("logged"), code);
			response.getWriter().write(new Gson().toJson(mResponse));
		}
		else {
			if(request.getSession().getAttribute("logged")==null)
				request.setAttribute("message", "Password or Email incorrect");
			doGet(request, response);
		}
	}

	private Logged login(String email, String password, String ip, String user, String type, HttpServletResponse response) {
		Logged log = null;
		if(email!=null&&password!=null) {
			String sql = "SELECT a.email, a.id, p.password, p.date " + 
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
					if(BCrypt.checkpw(password, rs.getString(3))) {
						log = new Logged(rs.getString(1), rs.getInt(2));
						log.setType(type);
						log.setBuildings(setHouses(con, log.getId()));
						setLogin(con, ip, type, log.getId(), user, response);
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
		return log;
	}
	
	private void setLogin(Connection con, String ip, String type, int id, String user, HttpServletResponse httpResponse) throws ClientProtocolException, IOException, SQLException {
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
			System.out.println("GSON error occured in login controller - IP is likely local.");
		} finally {
			response.close();
		}
		Long expire = System.currentTimeMillis() + ((long)1000 * 60 * 60 * 24 * 30);
		String device = type;
		String cookie = LogCookie.generate();
		Object val3[] = {id, System.currentTimeMillis(), location, user, device, cookie, expire};
		String sql = "INSERT INTO FYP.Login(accountid, datetime, location, osbrowser, device,"
				+ "cookie, expire)VALUES (?, ?, ?, ?, ?, ?, ?);";
		try (PreparedStatement ptst = prepare(con, sql, val3)) {
			if(ptst.executeUpdate()==1)
				httpResponse.addCookie(createCookie("login", cookie, 60*60*24*30));
		}
	}
	
	private Cookie createCookie(String name, String details, int life) {
		Cookie temp = new Cookie(name, details);
		temp.setMaxAge(life);
		return temp;
	}
	
	private ArrayList<String> setHouses(Connection con, int id) throws SQLException {
		ArrayList<String> houses = new ArrayList<>();
		String sql = "SELECT name FROM FYP.building WHERE accountId = ?";
		Object val2[] = {id};
		try (PreparedStatement ptst1 = prepare(con, sql, val2);
				ResultSet rs1 = ptst1.executeQuery()) {
			while(rs1.next())
				houses.add(rs1.getString(1));
		}
		return houses;	
	}

	private PreparedStatement prepare(Connection con, String sql, Object values[]) throws SQLException {
		final PreparedStatement ptst = con.prepareStatement(sql);
		for (int i = 0; i < values.length; i++) {
			ptst.setObject(i+1, values[i]);
		}
		return ptst;
	}
}
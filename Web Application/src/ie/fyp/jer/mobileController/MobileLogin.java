package ie.fyp.jer.mobileController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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

import ie.fyp.jer.domain.Logged;
import ie.fyp.jer.domain.MobileResponse;

/**
 * Servlet implementation class MobilerLogin
 */
@WebServlet("/mobileLogin")
public class MobileLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MobileLogin() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
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
		request.getSession().setAttribute("type", type);
		request.getSession().setAttribute("logged", login(email, password, ip, user));
		int code = 0;
		if(request.getSession().getAttribute("logged")!=null)
			code = 1;
		MobileResponse mResponse = new MobileResponse((Logged)request.getSession().getAttribute("logged"), code);
		response.getWriter().write(new Gson().toJson(mResponse));
	}
	
	private Logged login(String email, String password, String ip, String user) {
		Logged log = null;
		if(email!=null&&password!=null) {
			String sql = "SELECT a.fName, a.id, p.password, p.date " + 
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
						sql = "SELECT name FROM FYP.building WHERE accountId = ?";
						String location = "Unknown";
						Object val2[] = {log.getId()};
						try (PreparedStatement ptst1 = prepare(con, sql, val2);
								ResultSet rs1 = ptst1.executeQuery()) {
							while(rs1.next())
								log.addBuilding(rs1.getString(1));
						}
						CloseableHttpClient httpclient = HttpClients.createDefault();
						HttpGet httpGet = new HttpGet("http://ip-api.com/json/" + ip);
						CloseableHttpResponse response1 = httpclient.execute(httpGet);
						try {
							HttpEntity entity1 = response1.getEntity();
							String gson = EntityUtils.toString(entity1);
							JsonParser parse = new JsonParser();
							JsonObject object = parse.parse(gson).getAsJsonObject();
							location = (object.get("city").getAsString() + " " + object.get("countryCode").getAsString());
							EntityUtils.consume(entity1);
						} catch (Exception e) {
							System.out.println("GSON error occured in login controller - IP is probably incorrect.");
						} finally {
						    response1.close();
						}
						Object val3[] = {log.getId(), System.currentTimeMillis(), location, user};
						sql = "INSERT INTO FYP.Login(accountid, datetime, location, osbrowser)VALUES (?, ?, ?, ?);";
						try (PreparedStatement ptst1 = prepare(con, sql, val3)) {
							ptst1.executeUpdate();
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return log;
	}

	private PreparedStatement prepare(Connection con, String sql, Object values[]) throws SQLException {
		final PreparedStatement ptst = con.prepareStatement(sql);
		for (int i = 0; i < values.length; i++) {
			ptst.setObject(i+1, values[i]);
		}
		return ptst;
	}
}
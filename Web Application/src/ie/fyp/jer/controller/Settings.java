package ie.fyp.jer.controller;

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

import org.mindrot.jbcrypt.BCrypt;

import ie.fyp.jer.config.Device;
import ie.fyp.jer.domain.Logged;

/**
 * Servlet implementation class Dashboard
 */
@WebServlet("/settings")
public class Settings extends HttpServlet {
	private static final long serialVersionUID = 10L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Settings() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getSession().getAttribute("logged")!=null) {
			request.setAttribute("main", "settings");
			request.setAttribute("subtitle", "Settings");
			request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
		}
		else
			request.getRequestDispatcher("?path=/settings").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getSession().getAttribute("logged")!=null) {
			Logged log = (Logged)request.getSession().getAttribute("logged");
			String type = request.getParameter("type");
			if(!type.equals("pass")&&!type.equals("2fa")) {
				log.setEmail(updateAccount(request, log.getId()));
				request.getSession().setAttribute("logged", log);				
			}
			else if(type.equals("pass"))
				updatePassword(request, log.getId());
			else if(type.equals("2fa"))
				updateSecurity(request, log.getId());
		}
		doGet(request, response);
	}

	private String updateAccount(HttpServletRequest request, int log) {
		String params[] = {"fName", "lName", "email", "phone", "street", "town", "county"};
		Object[] values = new Object[params.length+1];
		for(int i=0; i<=params.length; i++) {
			if(i<params.length) {
				values[i] = request.getParameter(params[i]);
				if(values[i]!=null&&values[i].equals(""))
					values[i]=null;
			}
			else
				values[i] = log;
		}
		String sql = "UPDATE FYP.Account " + 
				"SET fName = COALESCE(?, fName), " + 
				"lName = COALESCE(?, lName), " + 
				"email = COALESCE(?, email), " + 
				"phone = COALESCE(?, phone), " + 
				"street = COALESCE(?, street), " + 
				"town = COALESCE(?, town), " + 
				"county = COALESCE(?, county) " + 
				"WHERE id = ?;";
		try (Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, values)) {
			ptst.executeUpdate();
			request.setAttribute("settings", "Settings updated!");
			if(values[0]!=null)
				return values[0].toString();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void updatePassword(HttpServletRequest request, int log) {
		String cPass = request.getParameter("cPass");
		String nPass1 = request.getParameter("nPass1");
		String nPass2 = request.getParameter("nPass2");
		if(nPass1.equals(nPass2)) {
			String sql = "SELECT password FROM FYP.Password " + 
					"WHERE accountId IN (SELECT id " + 
					"					FROM FYP.Account " + 
					"					WHERE id = ?) " + 
					"ORDER BY date DESC;";
			Object val[] = {log};
			try(Connection con = dataSource.getConnection();
					PreparedStatement ptst = prepare(con, sql, val);
					ResultSet rs = ptst.executeQuery()) {
				if(rs.next())
					if(BCrypt.checkpw(cPass, rs.getString(1))) {
						sql = "INSERT INTO FYP.Password (accountid, password, date) VALUES (?, ?, ?);";
						Object val2[] = {log, BCrypt.hashpw(nPass1, BCrypt.gensalt()), System.currentTimeMillis()};
						try (PreparedStatement ptst2 = prepare(con, sql, val2)) {
							ptst2.executeUpdate();
							request.setAttribute("settings", "Password Updated!");
						}
					}
					else
						request.setAttribute("settings", "Current password incorrect!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else
			request.setAttribute("settings", "New passwords did not match!");
	}

	private void updateSecurity(HttpServletRequest request, int log) {
		//UPDATE FYP.Account SET twoStep = 10 WHERE id = 1;
		String device = Device.generate();
		if(request.getParameter("2fa")==null)
			device = null;
		String sql = "UPDATE FYP.Account SET twoStep = ? WHERE id = ?;";
		Object val[] = {device, log};
		try (Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con,sql, val)) {
			ptst.executeUpdate();
			request.setAttribute("device", device);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private PreparedStatement prepare(Connection con, String sql, Object... values) throws SQLException {
		final PreparedStatement ptst = con.prepareStatement(sql);
		for (int i = 0; i < values.length; i++) {
			ptst.setObject(i+1, values[i]);
		}
		return ptst;
	}
}
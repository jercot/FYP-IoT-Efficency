package ie.fyp.jer.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Servlet implementation class Register
 */
@WebServlet("/register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 8L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource;
	private boolean registered;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Register() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("website", "IoT Efficiency");
		String forward = "/WEB-INF/register.jsp";
		if(registered)
			forward = "/WEB-INF/registered.jsp";
		if(request.getSession().getAttribute("logged")==null)
			request.getRequestDispatcher(forward).forward(request, response);
		else
			response.sendRedirect("");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String params[] = {"fName", "lName", "email", "phone", "street", "town", "county", "date", "pass", "date"};
		Object[] values = new Object[params.length];
		long currentTime = System.currentTimeMillis();
		for(int i=0; i<params.length; i++) {
			values[i] = request.getParameter(params[i]);
			if(params[i].equals("date"))
				values[i] = currentTime;
			else if(params[i].equals("pass"))
				values[i] = BCrypt.hashpw(params[i], BCrypt.gensalt());
		}
		String sql = "START TRANSACTION;" + 
				"INSERT INTO FYP.Account (fname, lname, email, phone, street, town, county, regdate) VALUES (?, ?, ?, ?, ?, ?, ?, ?);" + 
				"INSERT INTO FYP.Password (accountid, password, date) VALUES (currval('FYP.Account_id_seq'), ?, ?);" + 
				"COMMIT;";
		try (Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, values)) {
			ptst.executeUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		doGet(request, response);
	}

	private PreparedStatement prepare(Connection con, String sql, Object... values) throws SQLException {
		final PreparedStatement ptst = con.prepareStatement(sql);
		for (int i = 0; i < values.length; i++) {
			ptst.setObject(i+1, values[i]);
		}
		return ptst;
	}
}
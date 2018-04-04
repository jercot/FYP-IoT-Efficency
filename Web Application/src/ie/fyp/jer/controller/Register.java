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

/**
 * Servlet implementation class Register
 */
@WebServlet("/register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
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
		String forward = "/WEB-INF/register.jsp";
		if(registered)
			forward = "/WEB-INF/registered.jsp";
		if(request.getSession().getAttribute("logged")==null)
			request.getRequestDispatcher(forward).forward(request, response);
		else
			response.sendRedirect(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fName = request.getParameter("fName");
		String lName = request.getParameter("lName");
		String email = request.getParameter("email");
		String pass = request.getParameter("pass");
		String street = request.getParameter("street");
		String town = request.getParameter("town");
		String county = request.getParameter("county");
		String num = request.getParameter("phone");
		try {
			Connection con = dataSource.getConnection();
			int id = insertName(con, fName, lName, email, street, town, county, num);
			insertPassword(con, id, pass);
			//request.getSession().setAttribute("logged", new Logged(id));
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		registered = true;
		doGet(request, response);
	}

	private int insertName(Connection con, String fName, String lName, String email, String street, String town, String county, String num) throws SQLException {
		String accountTable = "INSERT INTO FYP.Account (fname, lname, email, phone, street, town, county, regdate) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement pdst = con.prepareStatement(accountTable);
		pdst.setString(1, fName);
		pdst.setString(2, lName);
		pdst.setString(3, email);
		pdst.setString(4, num);
		pdst.setString(5, street);
		pdst.setString(6, town);
		pdst.setString(7, county);
		pdst.setLong(8, System.currentTimeMillis());
		return pdst.executeUpdate();
	}

	private void insertPassword(Connection con, int id, String pass) throws SQLException {
		String passTable = "INSERT INTO FYP.Password (accountid, password, date) VALUES (?, ?, ?);";
		PreparedStatement pdst = con.prepareStatement(passTable);
		pdst.setInt(1, id);
		pdst.setString(2, pass);
		pdst.setLong(3, System.currentTimeMillis());
		pdst.executeUpdate();
	}
}
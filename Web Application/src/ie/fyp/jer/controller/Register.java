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

import ie.fyp.jer.domain.Account;

/**
 * Servlet implementation class Register
 */
@WebServlet("/register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource;
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
		if(request.getSession().getAttribute("logged")==null)
			request.getRequestDispatcher("/WEB-INF/register.jsp").forward(request, response);
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
			if(!prepared(con, "SELECT * FROM \"FYP\".\"Account\" WHERE UPPER(email) = UPPER(?);", email).next()) {
				insertName(con, fName, lName, email, street, town, county, num);
				insertPassword(con, pass);
				request.getSession().setAttribute("logged", getAccount(con, email));
			}
			else {
				request.setAttribute("message", "An email has been sent to the email used");
			}
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		doGet(request, response);
	}

	private Account getAccount(Connection con, String email) throws SQLException {
		PreparedStatement pdst = con.prepareStatement("SELECT id FROM \"FYP\".\"Account\" WHERE UPPER(email) = UPPER(?);");
		pdst.setString(1, email);
		ResultSet rs = pdst.executeQuery();
		if(rs.next())
			return new Account(rs.getInt(1));
		return null;
	}

	private ResultSet prepared(Connection con, String query, String email) throws SQLException {
		PreparedStatement pdst = con.prepareStatement(query);
		pdst.setString(1, email);
		return pdst.executeQuery();
	}

	private void insertName(Connection con, String fName, String lName, String email, String street, String town, String county, String num) throws SQLException {
		String accountTable = "INSERT INTO \"FYP\".\"Account\"(fname, lName, email, phone, street, town, county, regdate) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement pdst = con.prepareStatement(accountTable);
		pdst.setString(1, fName);
		pdst.setString(2, lName);
		pdst.setString(3, email);
		pdst.setString(4, num);
		pdst.setString(5, street);
		pdst.setString(6, town);
		pdst.setString(7, county);
		pdst.setLong(8, System.currentTimeMillis());
		pdst.executeUpdate();
	}

	private void insertPassword(Connection con, String pass) throws SQLException {
		String passTable = "INSERT INTO \"FYP\".\"Password\"(accountid, password, date) VALUES (?, ?, ?);";
		PreparedStatement pdst = con.prepareStatement(passTable);
		ResultSet temp = getResult(con, "SELECT MAX(id) FROM \"FYP\".\"Account\";");
		while(temp.next())
			pdst.setInt(1, temp.getInt(1));
		pdst.setString(2, pass);
		pdst.setLong(3, System.currentTimeMillis());
		pdst.executeUpdate();
	}


	private ResultSet getResult(Connection con, String query) throws SQLException {
		return con.createStatement().executeQuery(query);
	}
}
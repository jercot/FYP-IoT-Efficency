package ie.fyp.jer.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class Upload
 */
@WebServlet("/tempup")
public class TempUp extends HttpServlet {
	private static final long serialVersionUID = 11L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource;   
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TempUp() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String input = request.getQueryString()+ "&time=" + System.currentTimeMillis();
			String query = "INSERT INTO public.\"Temp\" (\"string\") VALUES('" + input + "');";
			Connection con = dataSource.getConnection();
			Statement compStmt = con.createStatement();
			compStmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}

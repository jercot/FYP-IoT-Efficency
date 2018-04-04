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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class House
 */
@WebServlet("/house")
public class House extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public House() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getSession().getAttribute("logged")!=null) {
			ArrayList<String> rooms = new ArrayList<>();
			String query = "SELECT name, floor FROM FYP.room where buildingId IN(SELECT id FROM FYP.Building WHERE name = ?)";
			try {
				Connection con = dataSource.getConnection();
				PreparedStatement ptst = con.prepareStatement(query);
				ptst.setString(1, request.getParameter("name"));
				ResultSet rs = ptst.executeQuery();
				while(rs.next()) {
					rooms.add("Name: " + rs.getString(1) + " - Floor: " + rs.getInt(2));
				}
				request.setAttribute("rooms", rooms);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			request.setAttribute("name", request.getParameter("name"));
			request.setAttribute("main", "house");
			request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
		}
		else
			response.sendRedirect(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
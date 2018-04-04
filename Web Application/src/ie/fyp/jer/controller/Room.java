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

import ie.fyp.jer.config.Bucket;
import ie.fyp.jer.domain.Logged;

/**
 * Servlet implementation class Room
 */
@WebServlet("/room")
public class Room extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Room() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getSession().getAttribute("logged")!=null) {
			response.sendRedirect("house?name="+request.getParameter("bName"));}
		else
			response.sendRedirect(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logged log = (Logged)request.getSession().getAttribute("logged");
		if(log!=null&&log.compare(request.getParameter("token"))) {
			String building = request.getParameter("bName");
			String name = request.getParameter("rName");
			int floor = Integer.parseInt(request.getParameter("floor"));
			String bucket = Bucket.generate();
			String insert = "INSERT INTO fyp.room (buildingid, name, bucket, floor) " + 
					"SELECT id, ?, ?, ? " + 
					"FROM FYP.building " + 
					"WHERE name = ?;";
			try {
				Connection con = dataSource.getConnection();
				PreparedStatement ptst = con.prepareStatement(insert);
				ptst.setString(1, name);
				ptst.setString(2, bucket);
				ptst.setInt(3, floor);
				ptst.setString(4, building);
				ptst.executeUpdate();
				request.setAttribute("message", "Room " + name + " added!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		doGet(request, response);
	}

}

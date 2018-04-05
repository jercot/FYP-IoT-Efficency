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

import ie.fyp.jer.domain.Logged;

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
		Logged log = (Logged)request.getSession().getAttribute("logged");
		if(log!=null) {
			ArrayList<String> rooms = new ArrayList<>();
			String query = "SELECT * FROM FYP.room where buildingId IN(SELECT id FROM FYP.Building WHERE name = ? AND accountId = ?)";
			try {
				Connection con = dataSource.getConnection();
				PreparedStatement ptst = con.prepareStatement(query);
				ptst.setString(1, request.getParameter("bName"));
				ptst.setInt(2, log.getId());
				ResultSet rs = ptst.executeQuery();
				while(rs.next()) {
					rooms.add("Name: " + rs.getString(3) + " - Floor: " + rs.getInt(5));
				}
				request.setAttribute("rooms", rooms);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			request.setAttribute("bName", request.getParameter("bName"));
			request.setAttribute("main", "house");
			request.setAttribute("subtitle", request.getParameter("bName"));
			request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
		}
		else
			response.sendRedirect(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logged log = (Logged)request.getSession().getAttribute("logged");
		if(log!=null&&log.compare(request.getParameter("token"))) {
			int id = log.getId();
			String bName = request.getParameter("bName");
			if(bName.equals(""))
				bName = null;
			String pName = request.getParameter("pName");
			String location = request.getParameter("location");
			if(location.equals(""))
				location = null;
			try {
				Connection con = dataSource.getConnection();
				String update = "UPDATE fyp.building SET name=COALESCE(?, name), location = COALESCE(?, location) WHERE name=? AND accountId=?;";
				PreparedStatement ptst = con.prepareStatement(update);
				ptst.setString(1, bName);
				ptst.setString(2, location);
				ptst.setString(3, pName);
				ptst.setInt(4, id);
				ptst.executeUpdate();
				if(bName!=null)
					log.editBuilding(pName, bName);
				con.close();
				request.setAttribute("message", "Building updated");
			} catch (SQLException e) {
				request.setAttribute("message", "Building with that name already exists");
				e.printStackTrace();
			}
		}
		else 
			request.getSession().setAttribute("logged", null);
		doGet(request, response);
	}
}
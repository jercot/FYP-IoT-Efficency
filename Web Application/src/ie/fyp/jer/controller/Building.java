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

import ie.fyp.jer.domain.Logged;

/**
 * Servlet implementation class AddBuilding
 */
@WebServlet("/building")
public class Building extends HttpServlet {
	private static final long serialVersionUID = 2L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource;
	private boolean added;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Building() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.print("Building");
		if(request.getSession().getAttribute("logged")!=null&&!added) {
			request.setAttribute("main", "building");
			request.setAttribute("subtitle", "Add House to System");
			request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
		}
		else {
			added = false;
			response.sendRedirect("");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logged log = (Logged)request.getSession().getAttribute("logged");
		if(log!=null&&log.compare(request.getParameter("token"))) {
			int id = log.getId();
			String name = request.getParameter("bName");
			String location = request.getParameter("location");
			String sql = "INSERT INTO FYP.Building (accountid, name, location) VALUES (?, ?, ?);";
			try (Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, id, name, location)) {
				ptst.executeUpdate();
				log.addBuilding(name);
				request.setAttribute("message", "Building added to system");
				added=true;
				con.close();
			} catch (SQLException e) {
				request.setAttribute("bName", name);
				request.setAttribute("location", location);
				request.setAttribute("message", "Building with that name already exists");
				e.printStackTrace();
			}
		}
		else 
			request.getSession().setAttribute("logged", null);
		doGet(request, response);
	}

	private PreparedStatement prepare(Connection con, String sql, int id, String name, String location) throws SQLException {
		final PreparedStatement ptst = con.prepareStatement(sql);
		ptst.setInt(1, id);
		ptst.setString(2, name);
		ptst.setString(3, location);
		return ptst;
	}
}
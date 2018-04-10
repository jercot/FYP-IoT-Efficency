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
 * Servlet implementation class Room
 */
@WebServlet("/room")
public class Room extends HttpServlet {
	private static final long serialVersionUID = 9L;
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
			response.sendRedirect("house?bName="+request.getParameter("bName"));}
		else
			response.sendRedirect(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logged log = (Logged)request.getSession().getAttribute("logged");
		if(log!=null&&log.compare(request.getParameter("token"))) {
			int floor=-1;
			if(!request.getParameter("floor").equals(""))
				floor = Integer.parseInt(request.getParameter("floor"));
			if(request.getParameter("type").equals("add")) {
				String sql = "INSERT INTO FYP.Room (buildingid, name, floor) " + 
						"SELECT id, ?, ?, ? " + 
						"FROM FYP.building " + 
						"WHERE name = ?;";
				Object val[] = {request.getParameter("rName"), floor, request.getParameter("bName")};
				try (Connection con = dataSource.getConnection();
						PreparedStatement ptst = prepare(con, sql, val)) {
					ptst.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			else {
				String sql = "UPDATE fyp.room " + 
						"SET name = COALESCE(?, name), floor = " + 
						"CASE " + 
						"WHEN ?=-1 then floor " + 
						"ELSE ? " + 
						"END " + 
						"WHERE name = ?;";
				Object val[] = {request.getParameter("rName"), floor, floor, request.getParameter("oName")};
				try (Connection con = dataSource.getConnection();
						PreparedStatement ptst = prepare(con, sql, val)) {
					ptst.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			request.setAttribute("message", "Room " + request.getParameter("rName") + " added!");
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
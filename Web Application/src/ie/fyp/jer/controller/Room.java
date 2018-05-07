package ie.fyp.jer.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import ie.fyp.jer.model.Database;
import ie.fyp.jer.model.Logged;

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
			response.sendRedirect("");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logged log = (Logged)request.getSession().getAttribute("logged");
		if(log!=null&&log.compare(request.getParameter("token"))) {
			int floor=-1;
			try {
				floor = Integer.parseInt(request.getParameter("floor"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			if(request.getParameter("type").equals("add")) {
				String sql = "INSERT INTO FYP.Room (buildingid, name, floor, notification) " + 
						"SELECT id, ?, ?, ? " + 
						"FROM FYP.building " + 
						"WHERE name = ?" +
						"AND accountId = ? ;";
				Object values[] = {getVal(request, "rName"), floor, 0, getVal(request, "bName"), log.getId()};
				execute(sql, values);
			}
			else {
				String sql = "UPDATE fyp.room " + 
						"SET name = COALESCE(?, name), floor = " + 
						"CASE " + 
						"WHEN ?=-1 then floor " + 
						"ELSE ? " + 
						"END " + 
						"WHERE name = ?" +
						"AND buildingId IN (SELECT id" +
						"					FROM FYP.building" +
						"					WHERE accountId = ?" +
						"					AND name = ?);";
				Object values[] = {getVal(request, "rName"), floor, floor, getVal(request, "oName"), log.getId(), getVal(request, "bName")};
				execute(sql, values);
			}
			request.setAttribute("message", "Room " + request.getParameter("rName") + " added!");
		}
		doGet(request, response);
	}

	private int execute(String sql, Object...values) {
		Database db = new Database(dataSource);
		return db.execute(sql, values);
	}

	private String getVal(HttpServletRequest request, String name) {
		if(request.getParameter(name)!=null&&request.getParameter(name).equals(""))
			return null;
		return request.getParameter(name);
	}
}
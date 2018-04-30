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
		if(request.getSession().getAttribute("logged")!=null&&!added) {
			request.setAttribute("main", "building");
			request.setAttribute("subtitle", "Add House to System");
			request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
		}
		else {
			added = false;
			request.getRequestDispatcher("?path=/building").forward(request, response); 
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logged log = (Logged)request.getSession().getAttribute("logged");
		if(log!=null&&log.compare(request.getParameter("token"))) {
			Object values[] = {log.getId(), request.getParameter("bName"), request.getParameter("location")};
			String sql = "INSERT INTO FYP.Building (accountid, name, location) VALUES (?, ?, ?);";
			Database db = new Database(dataSource);
			if(db.execute(sql, values)==1) {
				log.addBuilding(request.getParameter("bName"));
				request.setAttribute("message", "Building added to system");
				added=true;
			}
			else {
				request.setAttribute("bName", request.getParameter("bName"));
				request.setAttribute("location", request.getParameter("location"));
				request.setAttribute("message", "Building with that name already exists");
			}
		}
		else 
			request.getSession().setAttribute("logged", null);
		doGet(request, response);
	}
}
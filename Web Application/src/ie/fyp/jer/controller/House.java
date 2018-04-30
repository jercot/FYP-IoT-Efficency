package ie.fyp.jer.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import ie.fyp.jer.model.Database;
import ie.fyp.jer.model.HouseData;
import ie.fyp.jer.model.Logged;

/**
 * Servlet implementation class House
 */
@WebServlet("/house")
public class House extends HttpServlet {
	private static final long serialVersionUID = 4L;
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
		if(request.getQueryString()==null)
			response.sendRedirect("house?bName=" + getName(request));
		else if(log!=null&&log.houseExists(request.getParameter("bName"))) {
			Object values[] = {log.getId(), request.getParameter("bName")};
			ArrayList<HouseData> rooms = new ArrayList<>();
			String sql = "SELECT DISTINCT ON (ro.id) " + 
					"ro.name, ro.token, ro.floor, COALESCE(re.humidAve, -1), COALESCE(re.lightAve, -1), COALESCE(re.tempAve, -1) " + 
					"FROM FYP.Recording re " + 
					"FULL JOIN FYP.Room ro ON ro.id = re.roomId " +
					"WHERE ro.buildingId IN (SELECT id FROM FYP.building WHERE accountId=? AND name=?) " + 
					"ORDER BY ro.id, re.time DESC;";
			Database db = new Database(dataSource);
			rooms = db.getHouse(sql, values);
			if(rooms!=null) {
				request.setAttribute("rooms", rooms);
				request.setAttribute("bName", request.getParameter("bName"));
				request.setAttribute("main", "house");
				request.setAttribute("subtitle", request.getParameter("bName"));
				request.setAttribute("buckets", setTokens(rooms));
				request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);;
			}
		}
		else
			request.getRequestDispatcher("?path=/house?bName=" + request.getParameter("bName")).forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logged log = (Logged)request.getSession().getAttribute("logged");
		String bName = request.getParameter("bName");
		if(log!=null&&log.compare(request.getParameter("token"))) {
			int id = log.getId();
			if(bName.equals(""))
				bName = null;
			String pName = request.getParameter("pName");
			String location = request.getParameter("location");
			if(location.equals(""))
				location = null;
			String sql = "UPDATE fyp.building SET name=COALESCE(?, name), location = COALESCE(?, location) WHERE name=? AND accountId=?;";
			Object values[] = {bName, location, pName, id};
			Database db = new Database(dataSource);
			int update = db.execute(sql, values);
			if(update>0) {
				if(bName!=null)
					log.editBuilding(pName, bName);
				request.setAttribute("message", "Building updated");
			} else {
				request.setAttribute("message", "Building with that name already exists");
			}
		}
		else 
			request.getSession().setAttribute("logged", null);
		doGet(request, response);
	}
	
	private ArrayList<String> setTokens(ArrayList<HouseData> rooms) {
		ArrayList<String> tokens = new ArrayList<>();
		for(HouseData h: rooms) {
			tokens.add(h.getName() + " - " + h.getToken());
		}
		return tokens;
	}

	private String getName(HttpServletRequest request) {
		if(request.getParameter("pName")!=null)
			return request.getParameter("pName");
		return request.getParameter("bName");
	}
}
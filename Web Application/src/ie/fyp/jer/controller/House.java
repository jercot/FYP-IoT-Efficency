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
import ie.fyp.jer.domain.HouseData;

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
			response.sendRedirect("house?bName=" + request.getParameter("bName"));
		else if(log!=null) {
			Object values[] = {log.getId(), request.getParameter("bName")};
			ArrayList<HouseData> rooms = new ArrayList<>();
			String sql = "SELECT DISTINCT ON (ro.id) " + 
					"ro.name, ro.floor, COALESCE(re.humidAve, -1), COALESCE(re.lightAve, -1), COALESCE(re.tempAve, -1) " + 
					"FROM FYP.Recording re " + 
					"FULL JOIN FYP.Room ro ON ro.id = re.roomId " +
					"WHERE ro.buildingId IN (SELECT id FROM FYP.building WHERE accountId=? AND name=?) " + 
					"ORDER BY ro.id, re.time DESC;";
			try (Connection con = dataSource.getConnection();
					PreparedStatement ptst = prepare(con, sql, values);
					ResultSet rs = ptst.executeQuery()) {
				if(rs.next()) {
					do {
						rooms.add(new HouseData(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getFloat(5)));
					} while(rs.next());
					request.setAttribute("rooms", rooms);
					request.setAttribute("bName", request.getParameter("bName"));
					request.setAttribute("main", "house");
					request.setAttribute("subtitle", request.getParameter("bName"));
					request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
				}
				else
					response.sendRedirect("");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else
			response.sendRedirect("");
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
			try (Connection con = dataSource.getConnection();
					PreparedStatement ptst = prepare(con, sql, values)) {
				ptst.executeUpdate();
				if(bName!=null)
					log.editBuilding(pName, bName);
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

	private PreparedStatement prepare(Connection con, String sql, Object... values) throws SQLException {
		final PreparedStatement ptst = con.prepareStatement(sql);
		for (int i = 0; i < values.length; i++) {
			ptst.setObject(i+1, values[i]);
		}
		return ptst;
	}
}
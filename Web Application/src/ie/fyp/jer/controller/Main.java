package ie.fyp.jer.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import eu.bitwalker.useragentutils.UserAgent;
import ie.fyp.jer.domain.Logged;
import ie.fyp.jer.domain.HouseDash;

/**
 * Servlet implementation class Main
 */
@WebServlet("/index.jsp")
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Main() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getSession().getAttribute("logged")!=null) {
			request.setAttribute("website", "IoT Efficiency");
			request.setAttribute("main", "main");
			request.setAttribute("subtitle", "Dashboard");
			int log = ((Logged)request.getSession().getAttribute("logged")).getId();
			setAccount(log, request);
			setLastLog(log, request);
			setHouse(log, request);
			request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
		}
		else
			request.getRequestDispatcher("/WEB-INF/homepage.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private void setAccount(int log, HttpServletRequest request) {
		String sql ="SELECT a.*, p.date, COUNT(b.name) " + 
				"FROM FYP.Account a " + 
				"LEFT JOIN FYP.Building b ON b.accountId = a.id " + 
				"JOIN FYP.Password p ON p.accountId = a.id " +
				"WHERE a.id = ? " + 
				"GROUP BY a.id, p.date " + 
				"ORDER BY p.date DESC " + 
				"LIMIT 1;";
		Object val[] = {log};
		try (Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, val);
				ResultSet rs = ptst.executeQuery()) {
			if(rs.next()) {
				request.setAttribute("firstName", rs.getString(2));
				request.setAttribute("lastName", rs.getString(3));
				request.setAttribute("email", rs.getString(4));
				String phone = rs.getString(5);
				if(phone.equals(""))
					phone = "Not Set";
				request.setAttribute("phone", phone);
				String street = rs.getString(6);
				if(street.equals(""))
					street = "Not Set";
				request.setAttribute("street", street);
				request.setAttribute("town", rs.getString(7));
				request.setAttribute("county", rs.getString(8));
				request.setAttribute("regDate", getDate("dd-MMM-yyyy hh:mm:ss", rs.getLong(9)));
				if(rs.getLong(9)==rs.getLong(10))
					request.setAttribute("lastPas", "Password Never Changed");
				else
					request.setAttribute("lastPas", getDate("dd-MMM-yyyy hh:mm:ss", rs.getLong(10)));
				request.setAttribute("houses", rs.getInt(11));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void setLastLog(int log, HttpServletRequest request) {
		String sql = "SELECT MAX(dateTime), location, osBrowser " + 
				"FROM FYP.Login " + 
				"WHERE dateTime < (SELECT MAX(dateTime) " + 
				"					FROM FYP.Login) " + 
				"AND accountId = ? " + 
				"GROUP BY location, osBrowser " +
				"ORDER BY max DESC " +
				"LIMIT 1;";
		Object val[] = {log};
		try (Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, val);
				ResultSet rs = ptst.executeQuery()) {
			if(rs.next()) {
				request.setAttribute("prev", true);
				request.setAttribute("lastLog", getDate("dd-MMM-yyyy hh:mm:ss", rs.getLong(1)));
				request.setAttribute("location", rs.getString(2));
				request.setAttribute("system", getSystem(rs.getString(3)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void setHouse(int log, HttpServletRequest request) {
		ArrayList<HouseDash> houseList = new ArrayList<>();
		String sql = "SELECT name, location, id " + 
				"FROM FYP.building " + 
				"WHERE accountId = ?";
		Object val[] = {log};
		try (Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, val);
				ResultSet rs = ptst.executeQuery()) {
			while(rs.next()) {
				HouseDash temp = new HouseDash(rs.getString(1), rs.getString(2));
				sql = "SELECT name, floor " + 
						"FROM FYP.Room " + 
						"WHERE buildingId = ?";
				Object val1[] = {rs.getInt(3)};
				try (PreparedStatement ptst1 = prepare(con, sql, val1);
						ResultSet rs1 = ptst1.executeQuery()) {
					while(rs1.next()) {
						temp.addRoom(rs1.getString(1), rs1.getInt(2));
					}
				}
				houseList.add(temp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		request.setAttribute("houseDash", houseList);
	}

	private PreparedStatement prepare(Connection con, String sql, Object values[]) throws SQLException {
		final PreparedStatement ptst = con.prepareStatement(sql);
		for (int i = 0; i < values.length; i++) {
			ptst.setObject(i+1, values[i]);
		}
		return ptst;
	}


	private String getSystem(String details) {
		UserAgent user = UserAgent.parseUserAgentString(details);
		return user.getOperatingSystem().getName() + " - " + user.getBrowser().getName();
	}

	private String getDate(String format, long date) {		
		SimpleDateFormat df= new SimpleDateFormat(format);
		return df.format(new Date(date));
	}
}
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

import org.mindrot.jbcrypt.BCrypt;

import ie.fyp.jer.domain.Logged;

/**
 * Servlet implementation class Dashboard
 */
@WebServlet("/settings")
public class Settings extends HttpServlet {
	private static final long serialVersionUID = 10L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Settings() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getSession().getAttribute("logged")!=null) {
			request.setAttribute("main", "settings");
			request.setAttribute("subtitle", "Settings");
			request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
		}
		else
			request.getRequestDispatcher("?path=/settings").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getSession().getAttribute("logged")!=null) {
			Logged log = (Logged)request.getSession().getAttribute("logged");
			String type = request.getParameter("type");
			if(!type.equals("pass")) {
				log.setEmail(updateAccount(request, log.getId()));
				request.getSession().setAttribute("logged", log);				
			}
			else
				updatePassword(request, log.getId());
		}
		doGet(request, response);
	}

	private String updateAccount(HttpServletRequest request, int log) {
		String params[] = {"fName", "lName", "email", "phone", "street", "town", "county"};
		Object[] values = new Object[params.length+1];
		for(int i=0; i<=params.length; i++) {
			if(i<params.length) {
				values[i] = request.getParameter(params[i]);
				if(values[i]!=null&&values[i].equals(""))
					values[i]=null;
			}
			else
				values[i] = log;
		}
		String sql = "UPDATE FYP.Account " + 
				"SET fName = COALESCE(?, fName), " + 
				"lName = COALESCE(?, lName), " + 
				"email = COALESCE(?, email), " + 
				"phone = COALESCE(?, phone), " + 
				"street = COALESCE(?, street), " + 
				"town = COALESCE(?, town), " + 
				"county = COALESCE(?, county) " + 
				"WHERE id = ?;";
		try (Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, values)) {
			ptst.executeUpdate();
			request.setAttribute("settings", "Settings updated!");
			if(values[0]!=null)
				return values[0].toString();
				
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void updatePassword(HttpServletRequest request, int log) {
		// TODO Auto-generated method stub
		
	}

	private PreparedStatement prepare(Connection con, String sql, Object... values) throws SQLException {
		final PreparedStatement ptst = con.prepareStatement(sql);
		for (int i = 0; i < values.length; i++) {
			ptst.setObject(i+1, values[i]);
		}
		return ptst;
	}
}
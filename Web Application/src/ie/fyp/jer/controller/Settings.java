package ie.fyp.jer.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.mindrot.jbcrypt.BCrypt;

import ie.fyp.jer.config.Device;
import ie.fyp.jer.model.Database;
import ie.fyp.jer.model.Logged;

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
		Logged log = (Logged)request.getSession().getAttribute("logged");
		if(log!=null&&log.compare(request.getParameter("token"))) {
			String type = request.getParameter("type");
			if(!type.equals("pass")&&!type.equals("2fa")) {
				String temp = updateAccount(request, log.getId());
				if(temp!=null)
					log.setEmail(temp);
				request.getSession().setAttribute("logged", log);				
			}
			else if(type.equals("pass"))
				updatePassword(request, log.getId());
			else if(type.equals("2fa"))
				updateSecurity(request, log.getId());
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
		execute(sql, values);
		request.setAttribute("settings", "Settings updated!");
		if(values[2]!=null)
			return values[2].toString();
		return null;
	}

	private void updatePassword(HttpServletRequest request, int log) {
		String cPass = request.getParameter("cPass");
		String nPass1 = request.getParameter("nPass1");
		String nPass2 = request.getParameter("nPass2");
		if(nPass1.equals(nPass2)) {
			String sql = "SELECT password FROM FYP.Password " + 
					"WHERE accountId IN (SELECT id " + 
					"					FROM FYP.Account " + 
					"					WHERE id = ?) " + 
					"ORDER BY date DESC;";
			Object values[] = {log};
			if(BCrypt.checkpw(cPass, query(sql, values))) {
				sql = "INSERT INTO FYP.Password (accountid, password, date) VALUES (?, ?, ?);";
				Object values2[] = {log, BCrypt.hashpw(nPass1, BCrypt.gensalt()), System.currentTimeMillis()};
				if(execute(sql, values2)>0)
					request.setAttribute("settings", "Password Updated!");
			}
			else
				request.setAttribute("settings", "Current password incorrect!");
		}
		else
			request.setAttribute("settings", "New passwords did not match!");
	}

	private void updateSecurity(HttpServletRequest request, int log) {
		String device = Device.generate();
		if(request.getParameter("2fa")==null)
			device = null;
		String sql = "UPDATE FYP.Account SET twoStep = ? WHERE id = ?;";
		Object values[] = {device, log};
		if(execute(sql, values)>0)
			request.setAttribute("device", device);
	}

	private int execute(String sql, Object...values) {
		Database db = new Database(dataSource);
		return db.execute(sql, values);
	}

	private String query(String sql, Object...values) {
		Database db = new Database(dataSource);
		return db.getPassword(sql, values);
	}
}
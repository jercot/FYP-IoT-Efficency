package ie.fyp.jer.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
 * Servlet implementation class GetCode
 */
@WebServlet("/getCode")
public class GetCode extends HttpServlet {
	private static final long serialVersionUID = 14L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetCode() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String twoStep = request.getParameter("twoStep");
		if(twoStep!=null&&!twoStep.equals("null")) {
			long time = System.currentTimeMillis();
			String sql = "SELECT code " + 
					"FROM fyp.token " + 
					"WHERE accountId IN(SELECT id " + 
					"				FROM FYP.Account " + 
					"				WHERE twoStep = ?) " + 
					"AND expire > ?" +
					"ORDER BY id DESC " +
					"LIMIT 1;";
			Object val[] = {twoStep, time};
			try(Connection con = dataSource.getConnection();
					PreparedStatement ptst = prepare(con, sql, val);
					ResultSet rs = ptst.executeQuery()) {
				if(rs.next())
					request.setAttribute("code", rs.getInt(1));
				else
					request.setAttribute("code", "No recent login attempts");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else
			request.setAttribute("code", "2 Step Verification must be enabled in settings first");
		Logged log = (Logged)request.getSession().getAttribute("logged");
		if(log!=null) {
			if(!log.getType().equals("mobile"))
				response.sendRedirect("");
			else
				request.getRequestDispatcher("/WEB-INF/code.jsp").forward(request, response);
		}
		else
			request.getRequestDispatcher("?path=/getCode").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
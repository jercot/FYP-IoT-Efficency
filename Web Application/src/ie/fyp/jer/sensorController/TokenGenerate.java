package ie.fyp.jer.sensorController;

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

import ie.fyp.jer.config.Token;

/**
 * Servlet implementation class TokenTest
 */
@WebServlet("/token")
public class TokenGenerate extends HttpServlet {
	private static final long serialVersionUID = 13L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource; 
	private int c = 0;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TokenGenerate() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String token = Token.generate();
		Object val[] = {token, request.getParameter("bucket")};
		String sql = "UPDATE FYP.Room SET token=? WHERE bucket=?";
		try(Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, val)) {
			// Tried to return json but arduino json caused too many hangs
			String r = ptst.executeUpdate() + "-" + token;
			response.getWriter().write(r);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Gener: " + token + " - " + c++ + " +=+ " + request.getQueryString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private PreparedStatement prepare(Connection con, String sql, Object values[]) throws SQLException {
		final PreparedStatement ptst = con.prepareStatement(sql);
		for (int i = 0; i < values.length; i++) {
			ptst.setObject(i+1, values[i]);
		}
		return ptst;
	}
}
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
 * Servlet implementation class TempUp
 */
@WebServlet("/upload")
public class Upload extends HttpServlet {
	private static final long serialVersionUID = 12L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource; 
	int c = 0;
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Upload() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String params[] = {"mvmt", "hAve", "hMed", "hMin", "hMax", "lAve", "lMed", "lMin", "lMax", "tAve", "tMed", "tMin", "tMax", "time", "bucket"};
		Object[] values = new Object[params.length];
		long time = System.currentTimeMillis();
		for(int i=0; i<params.length; i++) {
			if(params[i].equals("time")) {
				int interval = 900000;
				values[i] = ((time%interval<interval/2) ? time-time%interval : time+interval-time%interval);
			}
			else {
				try {
					values[i] = Integer.parseInt(request.getParameter(params[i]));
				} catch (NumberFormatException e) {
					try {
						values[i] = Float.parseFloat(request.getParameter(params[i]));
					} catch (NumberFormatException e1) {
						values[i] = request.getParameter(params[i]);
					}
				}
			}
		}
		String sql = "INSERT INTO FYP.Recording(roomid, movement, humidave, humidmed, humidmin, "
				+ "humidmax, lightave, lightmed, lightmin, lightmax, tempave, tempmed, tempmin, tempmax, time) " + 
				"SELECT id, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? " + 
				"FROM FYP.room " + 
				"WHERE bucket = ?";
		try(Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, values)) {
			System.out.println(ptst.executeUpdate());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String token = Token.generate();
		Object val[] = {token, request.getParameter("bucket")};
		sql = "UPDATE FYP.Room SET token=? WHERE bucket=?";
		try(Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, val)) {
			// Tried to return json but arduino json caused too many hangs
			String r = ptst.executeUpdate() + "-" + token;
			response.getWriter().write(r);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Uploa: " + token + " - " + c++  + " +=+ " + request.getQueryString());
			request.getRequestDispatcher("tempup?"+request.getQueryString()).forward(request, response);
		}
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
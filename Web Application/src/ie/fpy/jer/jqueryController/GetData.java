package ie.fpy.jer.jqueryController;

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
 * Servlet implementation class Data
 */
@WebServlet("/data")
public class GetData extends HttpServlet {
	private static final long serialVersionUID = 13L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetData() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String output = "{\"code\":0,\"records\":[]}";
		String bName = request.getParameter("bName");
		Logged log = (Logged)request.getSession().getAttribute("logged");
		if(log!=null) {
			try (Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, log.getId(), bName);
				ResultSet rs = ptst.executeQuery()) {
				output = "{\"code\":1,\"records\":[";
				if(rs.next()) {
					output += addResult(rs);
					while(rs.next())
						output += "," + addResult(rs);
				}
				else 
					output = "{\"code\":2,\"records\":[";
				output += "]}";
				con.close();
			} catch (SQLException e) {
				output = "{\"code\":3,\"records\":[]}";
				e.printStackTrace();
			}
		}
		response.getWriter().write(output);
	}
	
	private PreparedStatement prepare(Connection con, int id, String bName) throws SQLException {
		String query = "SELECT ro.name, re.humidAve, re.lightAve, re.tempAve, re.time " + 
				"FROM FYP.Recording re " + 
				"JOIN FYP.Room ro ON ro.id = re.roomId " + 
				"WHERE ro.buildingId IN (SELECT id FROM FYP.building WHERE accountId=? AND name=?)";
		PreparedStatement ptst = con.prepareStatement(query);
		ptst.setInt(1, id);
		ptst.setString(2, bName);
		return ptst;
	}
	
	private String addResult(ResultSet rs) throws SQLException {
		String r = "{\"na\":\"" + rs.getString(1)
				 + "\",\"hu\":" + rs.getInt(2)
				 + ",\"li\":" + rs.getInt(3)
				 + ",\"te\":" + rs.getFloat(4)
				 + ",\"ti\":" + rs.getLong(5) + "}";
		return r;
	}
} //codes: 0 = No session, 1 = Data Retrieved, 2 = No Data for house, 3 = Error Occurred
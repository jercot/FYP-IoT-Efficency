package sensorController;

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

/**
 * Servlet implementation class TempUp
 */
@WebServlet("/upload")
public class Upload extends HttpServlet {
	private static final long serialVersionUID = 12L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource;   
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
		try {
			String insert = "INSERT INTO fyp.recording(roomid, movement, humidave, humidmed, humidmin, "
					+ "humidmax, lightave, lightmed, lightmin, lightmax, tempave, tempmed, tempmin, tempmax, \"time\") " + 
					"SELECT id, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? " + 
					"FROM FYP.room " + 
					"WHERE bucket = ?";
			Connection con = dataSource.getConnection();
			PreparedStatement ptst = con.prepareStatement(insert);
			ptst.setInt(1, Integer.parseInt(request.getParameter("mvmt")));
			ptst.setInt(2, Integer.parseInt(request.getParameter("hAve")));
			ptst.setInt(3, Integer.parseInt(request.getParameter("hMed")));
			ptst.setInt(4, Integer.parseInt(request.getParameter("hMin")));
			ptst.setInt(5, Integer.parseInt(request.getParameter("hMax")));
			ptst.setInt(6, Integer.parseInt(request.getParameter("lAve")));
			ptst.setInt(7, Integer.parseInt(request.getParameter("lMed")));
			ptst.setInt(8, Integer.parseInt(request.getParameter("lMin")));
			ptst.setInt(9, Integer.parseInt(request.getParameter("lMax")));
			ptst.setFloat(10, Float.parseFloat(request.getParameter("tAve")));
			ptst.setFloat(11, Float.parseFloat(request.getParameter("tMed")));
			ptst.setFloat(12, Float.parseFloat(request.getParameter("tMin")));
			ptst.setFloat(13, Float.parseFloat(request.getParameter("tMax")));
			ptst.setLong(14, System.currentTimeMillis());
			ptst.setString(15, request.getParameter("bucket"));
			ptst.executeUpdate();
			con.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		request.getRequestDispatcher("tempup?"+request.getQueryString()).forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}



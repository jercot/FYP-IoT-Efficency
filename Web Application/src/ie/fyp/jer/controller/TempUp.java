package ie.fyp.jer.controller;

import java.io.IOException;
import java.util.Date;

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
@WebServlet("/tempUp")
public class TempUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource;   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TempUp() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			int mvmt = Integer.parseInt(request.getParameter("mvmt"));
			int hAve = Integer.parseInt(request.getParameter("hAve"));
			int hMed = Integer.parseInt(request.getParameter("hMed"));
			int hMin = Integer.parseInt(request.getParameter("hMin"));
			int hMax = Integer.parseInt(request.getParameter("hMax"));
			int lAve = Integer.parseInt(request.getParameter("lAve"));
			int lMed = Integer.parseInt(request.getParameter("lMed"));
			int lMin = Integer.parseInt(request.getParameter("lMin"));
			int lMax = Integer.parseInt(request.getParameter("lMax"));
			float tAve = Float.parseFloat(request.getParameter("tAve"));
			float tMed = Float.parseFloat(request.getParameter("tMed"));
			float tMin = Float.parseFloat(request.getParameter("tMin"));
			float tMax = Float.parseFloat(request.getParameter("tMax"));
			String bucket = request.getParameter("bucket");
		long time = Long.parseLong(request.getParameter("time"));
		System.out.println(new Date(time));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

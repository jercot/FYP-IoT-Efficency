package ie.fyp.jer.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import ie.fyp.jer.model.Database;
import ie.fyp.jer.model.Logged;

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
					"AND expire > ? " + 
					"AND expire-300000 > (SELECT dateTime " + 
					"					FROM FYP.Login " + 
					"					WHERE accountId IN(SELECT id " + 
					"									FROM FYP.Account " + 
					"									WHERE twoStep = ?) " + 
					"					AND type='Session' " + 
					"					ORDER BY dateTime DESC " + 
					"					LIMIT 1) " + 
					"ORDER BY id DESC;";
			Object val[] = {twoStep, time, twoStep};
			Database db = new Database(dataSource);
			int code = db.getCode(sql, val);
			if(code!=-1)
				request.setAttribute("code", code);
			else
				request.setAttribute("code", "No recent login attempts");
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
}
package ie.fyp.jer.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ie.fyp.jer.domain.Logged;

/**
 * Servlet implementation class Local
 */
@WebServlet("/local")
public class Local extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Local() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Logged log = (Logged)request.getSession().getAttribute("logged");
		if(log!=null&&log.getType().equals("mobile"))
			request.getRequestDispatcher("/WEB-INF/local.jsp").forward(request, response);
		else if (log==null)
			request.getRequestDispatcher("?path=/local").forward(request, response);
		else
			response.sendRedirect("");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

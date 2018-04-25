package ie.fyp.jer.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TwoStep
 */
@WebServlet("/authenticate")
public class Authentication extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Authentication() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getSession().getAttribute("cookieS")!=null) {
			System.out.println(request.getSession().getAttribute("code"));
			request.getRequestDispatcher("/WEB-INF/auth.jsp").forward(request, response);
		}
		else if(request.getSession().getAttribute("checked")!=null)
			response.sendRedirect("");
		else
			response.sendRedirect("login");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean check = false;
		try {
			int att = getAttempt(request);
			if(att<5) {
				request.getSession().setAttribute("attempt", att+1);
				int code = Integer.parseInt(request.getParameter("code"));
				System.out.println(request.getSession().getAttribute("code") + " - " + code);
				check = code==(Integer)request.getSession().getAttribute("code");
				if(check)		
					response.addCookie(createCookie("login", (String)request.getSession().getAttribute("cookieS"), 60*60*24*30));
			}
			else
				request.setAttribute("message", "Too many attempts. Start over");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if(!check)
			doGet(request, response);
		else
			response.sendRedirect("");
	}

	private int getAttempt(HttpServletRequest request) {
		Object attempt = request.getSession().getAttribute("attempt");
		if(attempt==null)
			return 0;
		return (Integer)attempt;
	}

	private Cookie createCookie(String name, String details, int life) {
		Cookie temp = new Cookie(name, details);
		temp.setMaxAge(life);
		return temp;
	}
	
	private PreparedStatement prepare(Connection con, String sql, Object values[]) throws SQLException {
		final PreparedStatement ptst = con.prepareStatement(sql);
		for (int i = 0; i < values.length; i++) {
			ptst.setObject(i+1, values[i]);
		}
		return ptst;
	}
}

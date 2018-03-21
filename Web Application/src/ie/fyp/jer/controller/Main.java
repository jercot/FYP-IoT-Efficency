package ie.fyp.jer.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ie.fyp.jer.domain.Account;

/**
 * Servlet implementation class Main
 */
@WebServlet("/index.jsp")
public class Main extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Main() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getSession().getAttribute("logged")!=null) {
			if(request.getSession().getAttribute("houses")==null)
				setHouses((Account)request.getAttribute("logged"), request.getSession());
			request.setAttribute("main", "main");
			request.setAttribute("hello", "main hello");
			request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
		}
		else
			request.getRequestDispatcher("/WEB-INF/homepage.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private void setHouses(Account account, HttpSession session) {
		//Read houses from database using account id.
		ArrayList<String> houses = new ArrayList<>();
		houses.add("Cottage");
		houses.add("Holiday");
		session.setAttribute("houses", houses);
	}
}
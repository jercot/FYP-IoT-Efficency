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
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Resource(name="jdbc/aws-rds")
	private DataSource dataSource;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(request.getSession().getAttribute("logged")==null)
			request.getRequestDispatcher("/WEB-INF/homepage.jsp").forward(request, response);
		else
			response.sendRedirect(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
		String password = request.getParameter("pass");
		if(email!=null&&password!=null) {
			try {
				Connection con = dataSource.getConnection();
				String query = "SELECT CONCAT(a.fName, ' ', a.lName), a.id, p.password, p.date " + 
						"FROM FYP.Account a " + 
						"LEFT JOIN FYP.Password p ON a.id = p.accountId " + 
						"WHERE UPPER(a.email) = UPPER(?) " + 
						"ORDER BY date DESC " + 
						"LIMIT 1;";
				PreparedStatement ptst = con.prepareStatement(query);
				ptst.setString(1, email);
				ResultSet rs = ptst.executeQuery();
				if(rs.next()) {
					if(rs.getString(3).equals(password)) {
						Logged log = new Logged(rs.getString(1), rs.getInt(2));
						query = "SELECT name FROM FYP.building WHERE accountId = ?";
						ptst = con.prepareStatement(query);
						ptst.setInt(1, log.getId());
						ResultSet rs1 = ptst.executeQuery();
						while(rs1.next())
							log.addBuilding(rs1.getString(1));
						request.getSession().setAttribute("logged", log);
					}
				}
				else {
					request.setAttribute("message", "Password or Email incorrect");
				}
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		doGet(request, response);
	}
}
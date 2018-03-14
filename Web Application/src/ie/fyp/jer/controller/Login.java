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

import ie.fyp.jer.domain.Account;

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
				String query = "SELECT * FROM \"FYP\".\"Account\" WHERE UPPER(email) = UPPER(?);";
				ResultSet rs = preparedAccount(con, query, email);
				if(rs.next()) {
					query = "SELECT id FROM \"FYP\".\"Password\" WHERE id = ? and password = ?;";
					ResultSet rs2 = preparedPassword(con, query, rs.getInt(1), password);
					if(rs2.next())
						request.getSession().setAttribute("logged", new Account(rs.getInt(1)));
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
	
	private ResultSet preparedAccount(Connection con, String query, String email) throws SQLException {
		PreparedStatement pdst = con.prepareStatement(query);
		pdst.setString(1, email);
		return pdst.executeQuery();
	}
	
	private ResultSet preparedPassword(Connection con, String query, int id, String pass) throws SQLException {
		PreparedStatement pdst = con.prepareStatement(query);
		pdst.setInt(1, id);
		pdst.setString(2, pass);
		return pdst.executeQuery();
	}
}
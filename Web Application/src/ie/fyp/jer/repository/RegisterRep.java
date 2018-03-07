package ie.fyp.jer.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import ie.fyp.jer.domain.Account;

public class RegisterRep {

	public int register(HttpServletRequest request, DataSource ds, String email, String password ) {
		String pass = "INSERT INTO \"FYP\".\"Password\"(accountId, password, date) VALUES (?, ?, ?);";
		try {
			Connection con = ds.getConnection();
			if(!prepared(request, con, "SELECT * FROM \"FYP\".\"Account\" WHERE email = ?;", email).next()) {
				insertName(con, "Jaack", email, "9873490");
				insertPassword(con, pass);
			}
			else return 2;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}

	public ResultSet prepared(HttpServletRequest request, Connection con, String query, String email) throws SQLException {
		PreparedStatement ptst = con.prepareStatement(query);
		ptst.setString(1, email);
		request.setAttribute("logged", new Account());
		return ptst.executeQuery();
	}
	
	public void insertName(Connection con, String name, String email, String num) throws SQLException {
		String user = "INSERT INTO \"FYP\".\"Account\"(name, email, phone, regdate) VALUES (?, ?, ?, ?);";
		PreparedStatement pdst = con.prepareStatement(user);
		pdst.setString(1, name);
		pdst.setString(2, email);
		pdst.setString(3, num);
		pdst.setLong(4, System.currentTimeMillis());
		pdst.executeUpdate();
	}
	
	public void insertPassword(Connection con, String pass) throws SQLException {
		PreparedStatement pdst = con.prepareStatement(pass);
		ResultSet temp = getResult(con, "SELECT MAX(id) FROM \"FYP\".\"Account\";");
		while(temp.next())
			pdst.setInt(1, temp.getInt(1));
		pdst.setString(2, pass);
		pdst.setLong(3, System.currentTimeMillis());
		pdst.executeUpdate();
	}
	

	public ResultSet getResult(Connection con, String query) throws SQLException {
		return con.createStatement().executeQuery(query);
	}
}
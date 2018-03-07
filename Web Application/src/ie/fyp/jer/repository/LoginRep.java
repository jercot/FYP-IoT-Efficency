package ie.fyp.jer.repository;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import ie.fyp.jer.domain.Account;

public class LoginRep {

	public Account getAccount(DataSource dataSource, String email, String password) {
		Connection con;
		Account acc = null;
		try {
			con = dataSource.getConnection();
			con.createStatement();
			return new Account();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return acc;
	}
}
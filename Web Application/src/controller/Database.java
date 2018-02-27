package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	//INSERT INTO public."Temp"(id, "String")VALUES (1, "here");
	private Connection con;
	private boolean connected;

	public static String connect(String input) {
		try {
			Class.forName("org.postgresql.Driver");
			String dbName = "######";
			String userName = "######";
			String password = "######";
			String hostname = "######";
			String port = "######";
			String jdbcUrl = "jdbc:postgresql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password="
					+ password;
			Connection con = (Connection) DriverManager.getConnection(jdbcUrl);
			String query = "INSERT INTO public.\"Temp\" (\"string\") VALUES('" + input + "');";
			Statement compStmt = con.createStatement();
			compStmt.executeUpdate(query);
			con.close();
			//connected = true;
		} catch (ClassNotFoundException e) {
			return e.toString();
		} catch (SQLException e) {
			return e.toString();
		}
		return "finished";
	}
}
package ie.fyp.jer.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.sql.DataSource;

import org.mindrot.jbcrypt.BCrypt;

import eu.bitwalker.useragentutils.UserAgent;

public class Database {

	private DataSource dataSource;
	private boolean safety, success;

	public Database(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public int execute(String sql, Object...val) {
		try (Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, val)) {
			return ptst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private PreparedStatement prepare(Connection con, String sql, Object...values) throws SQLException {
		final PreparedStatement ptst = con.prepareStatement(sql);
		for (int i = 0; i < values.length; i++) {
			ptst.setObject(i+1, values[i]);
		}
		return ptst;
	}

	public int getCode(String sql, Object...vals) {
		try(Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, vals);
				ResultSet rs = ptst.executeQuery()) {
			if(rs.next())
				return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int getLogin(String sql, String password, String device, Object...vals) {
		try (Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, vals);
				ResultSet rs = ptst.executeQuery()) {
			if(rs.next()) {
				if(BCrypt.checkpw(password, rs.getString(4))) {
					if(rs.getString(3)!=null&&!device.equals(rs.getString(3))) 
						safety = true;
					success = true;
				}
				return rs.getInt(2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public ArrayList<HouseData> getHouse(String sql, Object...vals) {
		ArrayList<HouseData> rooms = new ArrayList<>();
		try(Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, vals);
				ResultSet rs = ptst.executeQuery()) {
			while(rs.next()) 
				rooms.add(new HouseData(rs.getString(1), rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getFloat(5)));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rooms;
	}

	public String getPassword(String sql, Object...vals) {
		try(Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, vals);
				ResultSet rs = ptst.executeQuery()) {
			if(rs.next()) 
				return rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public int getAttempts(String sql, Object...vals) {
		try(Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, vals);
				ResultSet rs = ptst.executeQuery()) {
			if(rs.next()) 
				return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public Logged getLogged(String sql, Object...vals) {
		try (Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, vals);
				ResultSet rs = ptst.executeQuery()) {
			if(rs.next()) {
				Logged log = new Logged(rs.getString(1), rs.getInt(2));
				log.setType(rs.getString(3));
				return log;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<String> getHouses(String sql, Object...vals) {
		ArrayList<String> houses = new ArrayList<>();
		try (Connection con = dataSource.getConnection();
				PreparedStatement ptst1 = prepare(con, sql, vals);
				ResultSet rs1 = ptst1.executeQuery()) {
			while(rs1.next())
				houses.add(rs1.getString(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return houses;
	}
	
	public String[] getDetails(String sql, Object...vals) {
		String details[] = new String[10];
		try (Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, vals);
				ResultSet rs = ptst.executeQuery()) {
			if(rs.next()) {
				details[0] = rs.getString(2);
				details[1] = rs.getString(3);
				details[2] = rs.getString(4);
				details[3] = ((rs.getString(5)==null) ? "Not Set" : rs.getString(5));
				details[4] = ((rs.getString(6)==null) ? "Not Set" : rs.getString(6));
				details[5] = rs.getString(7);
				details[6] = rs.getString(8);
				details[7] = getDate("dd-MMM-yyyy hh:mm:ss", rs.getLong(9));
				if(rs.getLong(9)==rs.getLong(11))
					details[8] = "Never Changed";
				else
					details[8] = getDate("dd-MMM-yyyy hh:mm:ss", rs.getLong(11));
				details[9] = rs.getInt(12) + "";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return details;
	}
	
	public String[] getLastLog(String sql, Object...vals) {
		String details[] = new String[4];
		try (Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, vals);
				ResultSet rs = ptst.executeQuery()) {
			if(rs.next()) {
				details[0] = true + "";
				details[1] = getDate("dd-MMM-yyyy hh:mm:ss", rs.getLong(1));
				details[2] = rs.getString(2);
				details[3] = getSystem(rs.getString(3));
				if(rs.getString(4).equals("mobile")) {
					details[3] = getOS(rs.getString(3)) + " - Mobile Application";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return details;
	}
	
	public ArrayList<HouseDash> setHouse(String sql, Object...vals) {
		ArrayList<HouseDash> houseList = new ArrayList<>();
		try (Connection con = dataSource.getConnection();
				PreparedStatement ptst = prepare(con, sql, vals);
				ResultSet rs = ptst.executeQuery()) {
			while(rs.next()) {
				HouseDash temp = new HouseDash(rs.getString(1), rs.getString(2));
				sql = "SELECT name, floor " + 
						"FROM FYP.Room " + 
						"WHERE buildingId = ?";
				Object val1[] = {rs.getInt(3)};
				try (PreparedStatement ptst1 = prepare(con, sql, val1);
						ResultSet rs1 = ptst1.executeQuery()) {
					while(rs1.next()) {
						temp.addRoom(rs1.getString(1), rs1.getInt(2));
					}
				}
				houseList.add(temp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return houseList;
	}

	private String getDate(String format, long date) {		
		SimpleDateFormat df= new SimpleDateFormat(format);
		return df.format(new Date(date));
	}
	
	private String getSystem(String details) {
		return getOS(details) + " - " + getBrowser(details);
	}

	private String getOS(String details) {
		UserAgent user = UserAgent.parseUserAgentString(details);
		return user.getOperatingSystem().getName();
	}

	private String getBrowser(String details) {
		UserAgent user = UserAgent.parseUserAgentString(details);
		return user.getBrowser().getName();
	}

	public boolean isSafety() {
		return safety;
	}
	
	public boolean isSuccess() {
		return success;
	}
}
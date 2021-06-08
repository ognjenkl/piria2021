package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import model.UserActivation;

/**
 * @author ognjen
 *
 */
public class UserActivationDAO {

	private static final String SQL_ALL = "SELECT * FROM user_activation;";
	private static final String SQL_GET_BY_KEY = "SELECT * FROM user_activation WHERE `key`=?;";
	private static final String SQL_INSERT = "INSERT INTO user_activation (`key`, user_id) VALUES (?, ?);";
	private static final String SQL_UPDATE = "UPDATE user_activation SET user_id=? WHERE `key`=?;";
	private static final String SQL_DELETE_BY_KEY = "DELETE FROM user_activation WHERE `key`=?";
	private static final String SQL_DELETE_BY_USER_ID = "DELETE FROM user_activation WHERE `user_id`=?";

	public static UserActivation getByKey(String key) {
		UserActivation retVal = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_BY_KEY);
			ppst.setString(1, key);
			rs = ppst.executeQuery();

			if (rs.next()) {
				retVal = new UserActivation();
				retVal.setKey(rs.getString("key"));
				retVal.setUserId(rs.getInt("user_id"));
				retVal.setCreationDate(rs.getTimestamp("creation_date"));
				return retVal;

			} else
				return null;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			if (ppst != null)
				try {
					ppst.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			ConnectionPool.getConnectionPool().checkIn(conn);
		}

	}

	public static List<UserActivation> getAll() {
		List<UserActivation> retVal = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_ALL);
			rs = ppst.executeQuery();

			while (rs.next()) {
				UserActivation userActivationDTO = new UserActivation();
				userActivationDTO.setKey(rs.getString("key"));
				userActivationDTO.setUserId(rs.getInt("user_id"));
				userActivationDTO.setCreationDate(new Timestamp(rs.getDate("creation_date").getTime()));
				retVal.add(userActivationDTO);
			}

			return retVal;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			if (ppst != null)
				try {
					ppst.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			ConnectionPool.getConnectionPool().checkIn(conn);
		}

	}

	public static int insert(UserActivation userActivationDTO) {
		Connection conn = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_INSERT);
			ppst.setString(1, userActivationDTO.getKey());
			ppst.setInt(2, userActivationDTO.getUserId());
			return ppst.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} finally {
			if (ppst != null)
				try {
					ppst.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			ConnectionPool.getConnectionPool().checkIn(conn);
		}

	}

	public static int update(UserActivation userActivationDTO) {
		Connection conn = null;
		PreparedStatement ppst = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_UPDATE);
			ppst.setInt(1, userActivationDTO.getUserId());
			ppst.setString(2, userActivationDTO.getKey());

			return ppst.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} finally {
			if (ppst != null)
				try {
					ppst.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			ConnectionPool.getConnectionPool().checkIn(conn);
		}

	}

	public static int deleteByKey(String key) {
		Connection conn = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_DELETE_BY_KEY);
			ppst.setString(1, key);
			return ppst.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} finally {
			if (ppst != null)
				try {
					ppst.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			ConnectionPool.getConnectionPool().checkIn(conn);
		}

	}
	
	public static int deleteByUserId(Integer userId) {
		Connection conn = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_DELETE_BY_USER_ID);
			ppst.setInt(1, userId);
			return ppst.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} finally {
			if (ppst != null)
				try {
					ppst.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			ConnectionPool.getConnectionPool().checkIn(conn);
		}
		
	}
	

	// public static int insert(User userRegister) {
	// userRegister.setPrivilege(3);
	// userRegister.setActive(false);
	// Connection conn = null;
	// ResultSet rs = null;
	// PreparedStatement ppst = null;
	// try {
	// conn = ConnectionPool.getConnectionPool().checkOut();
	// ppst = conn.prepareStatement(SQL_INSERT,
	// Statement.RETURN_GENERATED_KEYS);
	// ppst.setString(1, userRegister.getFirstName());
	// ppst.setString(2, userRegister.getLastName());
	// ppst.setString(3, userRegister.getSocialNo());
	// ppst.setTimestamp(4, userRegister.getBirthDate());
	// ppst.setString(5, userRegister.getEmail());
	// ppst.setInt(6, userRegister.getPrivilege());
	// ppst.setString(7, userRegister.getUsername());
	// ppst.setString(8, userRegister.getPassword());
	// ppst.setBoolean(9, userRegister.isActive());
	// ppst.executeUpdate();
	// rs = ppst.getGeneratedKeys();
	// if (rs.next())
	// ;
	// return rs.getInt(1);
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return -1;
	// } finally {
	// if (ppst != null)
	// try {
	// ppst.close();
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// ConnectionPool.getConnectionPool().checkIn(conn);
	// }
	//
	// }

}

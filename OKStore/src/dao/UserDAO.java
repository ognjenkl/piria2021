package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.User;

/**
 * @author ognjen
 *
 */
public class UserDAO {

	private static final String SQL_GET_ALL = "SELECT * FROM user";
	private static final String SQL_GET_ALL_BY_ACTIVE = "SELECT * FROM user WHERE active=?";
    private static final String SQL_GET_ALL_USER_ID_DIRECT_ACTIVE = "SELECT * FROM user WHERE id != ? AND chat_status = ? AND active = ? ORDER BY username";
    private static final String SQL_GET_ALL_USER_ID_ACTIVE = "SELECT * FROM user WHERE id != ? AND active = ? ORDER BY username";
    private static final String SQL_GET_BY_ID = "SELECT * FROM user WHERE id=?";
	private static final String SQL_GET_BY_ID_AND_ACTIVE = "SELECT * FROM user WHERE id=? AND active=?";
	private static final String SQL_GET_BY_USERNAME = "SELECT * FROM user WHERE username=? AND active=1";
	private static final String SQL_GET_BY_PRIVILEGE = "SELECT * FROM user WHERE privilege=? AND active=1";
//	private static final String SQL_GET_BY_USERNAME_AND_PASSWORD = "SELECT * FROM user where username=? AND password=? and active=1;";
	
	private static final String SQL_INSERT = "INSERT INTO user (first_name, last_name, social_no, birth_date, email, privilege, username, password, active, picture, status, chat_status, salt) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
	
	private static final String SQL_UPDATE = "UPDATE user SET first_name=?, last_name=?, social_no=?, birth_date=?, email=?, privilege=?, username=?, password=?, active=?, creation_date=?, picture=?, status=?, chat_status=? WHERE id=?";
	private static final String SQL_UPDATE_USER_WITHOUT_PRIVILEGE = "UPDATE user SET 				first_name=?, last_name=?, social_no=?, birth_date=?, email=?, password=?, salt=? WHERE id=?";
	private static final String SQL_UPDATE_USER_WITHOUT_PASSWORD_AND_PRIVILEGE = "UPDATE user SET  first_name=?, last_name=?, social_no=?, birth_date=?, email=? WHERE id=?";
	private static final String SQL_UPDATE_ADMIN_EDIT_USER = "UPDATE user SET  first_name=?, last_name=?, social_no=?, birth_date=?, email=?, privilege=? WHERE id=?";
	private static final String SQL_UPDATE_ACTIVE = "UPDATE user SET active=? WHERE id=?";
	private static final String SQL_UPDATE_CHAT_STATUS = "UPDATE user SET chat_status=? WHERE id=?";
	
	
	public static User login(String username) {
		User user = getByUsername(username);
		if (user != null)
			return user;
		else
			return null;

	}

	public static int insert(User userRegister) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
			ppst.setString(1, userRegister.getFirstName());
			ppst.setString(2, userRegister.getLastName());
			ppst.setString(3, userRegister.getSocialNo());
			ppst.setDate(4, userRegister.getBirthDate());
			ppst.setString(5, userRegister.getEmail());
			ppst.setInt(6, userRegister.getPrivilege());
			ppst.setString(7, userRegister.getUsername());
			ppst.setString(8, userRegister.getPassword());
			ppst.setBoolean(9, userRegister.isActive());
			ppst.setString(10, userRegister.getPicture());
			ppst.setInt(11, userRegister.getStatus());
			ppst.setInt(12, userRegister.getChatStatus());
			ppst.setString(13, userRegister.getSalt());
			
			ppst.executeUpdate();
			rs = ppst.getGeneratedKeys();
			if (rs.next())
				return rs.getInt(1);
			else
				return -1;

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

	public static User getByUsername(String username) {
		User retVal = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_BY_USERNAME);
			ppst.setString(1, username);
			rs = ppst.executeQuery();

			if (rs.next()) {
				retVal = new User();
				retVal.setId(rs.getInt("id"));
				retVal.setFirstName(rs.getString("first_name"));
				retVal.setLastName(rs.getString("last_name"));
				retVal.setSocialNo(rs.getString("social_no"));
				retVal.setBirthDate(rs.getDate("birth_date"));
				retVal.setEmail(rs.getString("email"));
				retVal.setPrivilege(rs.getInt("privilege"));
				retVal.setUsername(rs.getString("username"));
				retVal.setPassword(rs.getString("password"));
				retVal.setActive(rs.getBoolean("active"));
				retVal.setCreationDate(rs.getTimestamp("creation_date"));
				retVal.setPicture(rs.getString("picture"));
				retVal.setStatus(rs.getInt("status"));
				retVal.setChatStatus(rs.getInt("chat_status"));
				retVal.setSalt(rs.getString("salt"));
	
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
	
	public static User getByIdAndActive(Integer userId, boolean active) {
		User retVal = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_BY_ID_AND_ACTIVE);
			ppst.setInt(1, userId);
			ppst.setBoolean(2, active);
			rs = ppst.executeQuery();
			
			if (rs.next()) {
				retVal = new User();
				retVal.setId(rs.getInt("id"));
				retVal.setFirstName(rs.getString("first_name"));
				retVal.setLastName(rs.getString("last_name"));
				retVal.setSocialNo(rs.getString("social_no"));
				retVal.setBirthDate(rs.getDate("birth_date"));
				retVal.setEmail(rs.getString("email"));
				retVal.setPrivilege(rs.getInt("privilege"));
				retVal.setUsername(rs.getString("username"));
				retVal.setPassword(rs.getString("password"));
				retVal.setActive(rs.getBoolean("active"));
				retVal.setCreationDate(rs.getTimestamp("creation_date"));
				retVal.setPicture(rs.getString("picture"));
				retVal.setStatus(rs.getInt("status"));
				retVal.setChatStatus(rs.getInt("chat_status"));
				retVal.setSalt(rs.getString("salt"));
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
		
	public static User getById(Integer userId) {
		User retVal = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_BY_ID);
			ppst.setInt(1, userId);
			rs = ppst.executeQuery();
			
			if (rs.next()) {
				retVal = new User();
				retVal.setId(rs.getInt("id"));
				retVal.setFirstName(rs.getString("first_name"));
				retVal.setLastName(rs.getString("last_name"));
				retVal.setSocialNo(rs.getString("social_no"));
				retVal.setBirthDate(rs.getDate("birth_date"));
				retVal.setEmail(rs.getString("email"));
				retVal.setPrivilege(rs.getInt("privilege"));
				retVal.setUsername(rs.getString("username"));
				retVal.setPassword(rs.getString("password"));
				retVal.setActive(rs.getBoolean("active"));
				retVal.setCreationDate(rs.getTimestamp("creation_date"));
				retVal.setPicture(rs.getString("picture"));
				retVal.setStatus(rs.getInt("status"));
				retVal.setChatStatus(rs.getInt("chat_status"));
				retVal.setSalt(rs.getString("salt"));
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

	public static List<User> getByPrivilege(Integer privilege) {
		List<User> retVal = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_BY_PRIVILEGE);
			ppst.setInt(1, privilege);
			rs = ppst.executeQuery();

			while (rs.next()) {
				User userDTO = new User();
				userDTO.setId(rs.getInt("id"));
				userDTO.setFirstName(rs.getString("first_name"));
				userDTO.setLastName(rs.getString("last_name"));
				userDTO.setSocialNo(rs.getString("social_no"));
				userDTO.setBirthDate(rs.getDate("birth_date"));
				userDTO.setEmail(rs.getString("email"));
				userDTO.setPrivilege(rs.getInt("privilege"));
				userDTO.setUsername(rs.getString("username"));
				userDTO.setPassword(rs.getString("password"));
				userDTO.setActive(rs.getBoolean("active"));
				userDTO.setCreationDate(rs.getTimestamp("creation_date"));
				userDTO.setPicture(rs.getString("picture"));
				userDTO.setStatus(rs.getInt("status"));
				userDTO.setChatStatus(rs.getInt("chat_status"));
				userDTO.setSalt(rs.getString("salt"));
				
				retVal.add(userDTO);
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

	public static List<User> getAll() {
		List<User> retVal = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL);
			rs = ppst.executeQuery();

			while (rs.next()) {
				User userDTO = new User();
				userDTO.setId(rs.getInt("id"));
				userDTO.setFirstName(rs.getString("first_name"));
				userDTO.setLastName(rs.getString("last_name"));
				userDTO.setSocialNo(rs.getString("social_no"));
				userDTO.setBirthDate(rs.getDate("birth_date"));
				userDTO.setEmail(rs.getString("email"));
				userDTO.setPrivilege(rs.getInt("privilege"));
				userDTO.setUsername(rs.getString("username"));
				userDTO.setPassword(rs.getString("password"));
				userDTO.setActive(rs.getBoolean("active"));
				userDTO.setCreationDate(rs.getTimestamp("creation_date"));
				userDTO.setPicture(rs.getString("picture"));
				userDTO.setStatus(rs.getInt("status"));
				userDTO.setChatStatus(rs.getInt("chat_status"));
				userDTO.setSalt(rs.getString("salt"));
				
				retVal.add(userDTO);
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

	public static List<User> getAllByActive(boolean active) {
		List<User> retVal = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL_BY_ACTIVE);
			ppst.setBoolean(1, active);
			rs = ppst.executeQuery();
			
			while (rs.next()) {
				User userDTO = new User();
				userDTO.setId(rs.getInt("id"));
				userDTO.setFirstName(rs.getString("first_name"));
				userDTO.setLastName(rs.getString("last_name"));
				userDTO.setSocialNo(rs.getString("social_no"));
				userDTO.setBirthDate(rs.getDate("birth_date"));
				userDTO.setEmail(rs.getString("email"));
				userDTO.setPrivilege(rs.getInt("privilege"));
				userDTO.setUsername(rs.getString("username"));
				userDTO.setPassword(rs.getString("password"));
				userDTO.setActive(rs.getBoolean("active"));
				userDTO.setCreationDate(rs.getTimestamp("creation_date"));
				userDTO.setPicture(rs.getString("picture"));
				userDTO.setStatus(rs.getInt("status"));
				userDTO.setChatStatus(rs.getInt("chat_status"));
				userDTO.setSalt(rs.getString("salt"));
				
				retVal.add(userDTO);
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

	public static boolean update(User userDTO) {
		Connection conn = null;
		PreparedStatement ppst = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_UPDATE);
			ppst.setString(1, userDTO.getFirstName());
			ppst.setString(2, userDTO.getLastName());
			ppst.setString(3, userDTO.getSocialNo());
			ppst.setDate(4, userDTO.getBirthDate());
			ppst.setString(5, userDTO.getEmail());
			ppst.setInt(6, userDTO.getPrivilege());
			ppst.setString(7, userDTO.getUsername());
			ppst.setString(8, userDTO.getPassword());
			ppst.setBoolean(9, userDTO.isActive());
			ppst.setTimestamp(10, userDTO.getCreationDate());
			ppst.setString(11, userDTO.getPicture());
			ppst.setInt(12, userDTO.getStatus());
			ppst.setInt(13, userDTO.getChatStatus());

			ppst.setInt(14, userDTO.getId());

			// ppst.setString(1, newUsername != null ? newUsername : null);
			// ppst.setString(2, password != null ? password : null);
			// ppst.setString(3, firstName != null ? firstName : null);
			// ppst.setString(4, lastName != null ? lastName : null);
			// ppst.setString(5, socialNo != null ? socialNo : null);
			// ppst.setString(6, email != null ? email : null);
			// ppst.setInt(7, privilege != null ? Integer.valueOf(privilege) :
			// 30);
			// ppst.setString(8, picture != null ? picture : null);
			// ppst.setBoolean(9, active != null ? Boolean.valueOf(active) :
			// null);
			// ppst.setBoolean(10, editable != null ? Boolean.valueOf(editable)
			// : null);

			// ppst.setString(11, username);

			if (ppst.executeUpdate() > 0)
				return true;
			else
				return false;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
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

	/**
	 * User update but without password and privilege active etc.
	 * 
	 */
	public static int updateUserWithoutPasswordAndPrivilege(User userDTO) {
		Connection conn = null;
		PreparedStatement ppst = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_UPDATE_USER_WITHOUT_PASSWORD_AND_PRIVILEGE);
			ppst.setString(1, userDTO.getFirstName());
			ppst.setString(2, userDTO.getLastName());
			ppst.setString(3, userDTO.getSocialNo());
			ppst.setDate(4, userDTO.getBirthDate());
			ppst.setString(5, userDTO.getEmail());
		
			ppst.setInt(6, userDTO.getId());

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

	/**
	 * Update user without privilege and active.
	 * 
	 */
	public static int updateUserWithoutPrivilege(User userDTO) {
		Connection conn = null;
		PreparedStatement ppst = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_UPDATE_USER_WITHOUT_PRIVILEGE);
			ppst.setString(1, userDTO.getFirstName());
			ppst.setString(2, userDTO.getLastName());
			ppst.setString(3, userDTO.getSocialNo());
			ppst.setDate(4, userDTO.getBirthDate());
			ppst.setString(5, userDTO.getEmail());
			ppst.setString(6, userDTO.getPassword());
			ppst.setString(7, userDTO.getSalt());

			ppst.setInt(8, userDTO.getId());

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

	/**
	 * Update user for admin edit user mode.
	 * 
	 */
	public static int updateAdminEditUser(User userDTO) {
		Connection conn = null;
		PreparedStatement ppst = null;
		
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_UPDATE_ADMIN_EDIT_USER);
			ppst.setString(1, userDTO.getFirstName());
			ppst.setString(2, userDTO.getLastName());
			ppst.setString(3, userDTO.getSocialNo());
			ppst.setDate(4, userDTO.getBirthDate());
			ppst.setString(5, userDTO.getEmail());
			ppst.setInt(6, userDTO.getPrivilege());
			
			ppst.setInt(7, userDTO.getId());
			
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

	public static int updateChatStatus(Integer userId, Integer chatStatus) {
		Connection conn = null;
		PreparedStatement ppst = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_UPDATE_CHAT_STATUS);
			ppst.setInt(1, chatStatus);
			ppst.setInt(2, userId);
			
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
	
//	public static User getByUsernameAndPassword(String username, String password) {
//		Connection conn = null;
//		ResultSet rs = null;
//		PreparedStatement ppst = null;
//
//		try {
//			conn = ConnectionPool.getConnectionPool().checkOut();
//			ppst = conn.prepareStatement(SQL_GET_BY_USERNAME_AND_PASSWORD);
//			ppst.setString(1, username);
//			ppst.setString(2, password);
//			rs = ppst.executeQuery();
//
//			if (rs.next()) {
//				User userDTO = new User();
//				userDTO.setId(rs.getInt("id"));
//				userDTO.setFirstName(rs.getString("first_name"));
//				userDTO.setLastName(rs.getString("last_name"));
//				userDTO.setSocialNo(rs.getString("social_no"));
//				userDTO.setBirthDate(rs.getTimestamp("birth_date"));
//				userDTO.setEmail(rs.getString("email"));
//				userDTO.setPrivilege(rs.getInt("privilege"));
//				userDTO.setUsername(rs.getString("username"));
//				userDTO.setPassword(rs.getString("password"));
//				userDTO.setActive(rs.getBoolean("active"));
//				userDTO.setCreationDate(rs.getTimestamp("creation_date"));
//				userDTO.setPicture(rs.getString("picture"));
//				userDTO.setStatus(rs.getInt("status"));
//				userDTO.setChatStatus(rs.getInt("chat_status"));
//				userDTO.setSalt(rs.getString("salt"));
//				
//				return userDTO;
//
//			} else
//				return null;
//
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		} finally {
//			if (ppst != null)
//				try {
//					ppst.close();
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			ConnectionPool.getConnectionPool().checkIn(conn);
//		}
//	}

	public static int activate(int userId) {
		return updateActive(userId, true);
	}

	public static int deactivate(int userId) {
		return updateActive(userId, false);
	}
	
	public static int updateActive(int userId, boolean active) {
		Connection conn = null;
		PreparedStatement ppst = null;
		
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_UPDATE_ACTIVE);
			ppst.setBoolean(1, active);
			ppst.setInt(2, userId);
			
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

    public static List<User> getAllChatUsersByUserIdDirectActive(Integer userId, Boolean chatStatus, Boolean active) {
        List<User> retVal = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ppst = null;

        try {
            conn = ConnectionPool.getConnectionPool().checkOut();
            ppst = conn.prepareStatement(SQL_GET_ALL_USER_ID_DIRECT_ACTIVE);
            ppst.setInt(1, userId);
            ppst.setBoolean(2, chatStatus);
            ppst.setBoolean(3, active);
            rs = ppst.executeQuery();

            while (rs.next()) {
                User userDTO = new User();
                userDTO.setId(rs.getInt("id"));
                userDTO.setFirstName(rs.getString("first_name"));
                userDTO.setLastName(rs.getString("last_name"));
                userDTO.setSocialNo(rs.getString("social_no"));
                userDTO.setBirthDate(rs.getDate("birth_date"));
                userDTO.setEmail(rs.getString("email"));
                userDTO.setPrivilege(rs.getInt("privilege"));
                userDTO.setUsername(rs.getString("username"));
                userDTO.setPassword(rs.getString("password"));
                userDTO.setActive(rs.getBoolean("active"));
                userDTO.setCreationDate(rs.getTimestamp("creation_date"));
                userDTO.setPicture(rs.getString("picture"));
                userDTO.setStatus(rs.getInt("status"));
                userDTO.setChatStatus(rs.getInt("chat_status"));
                userDTO.setSalt(rs.getString("salt"));

                retVal.add(userDTO);
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
	
    public static List<User> getAllChatUsersByUserIdActive(Integer userId, Boolean active) {
        List<User> retVal = new ArrayList<>();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ppst = null;

        try {
            conn = ConnectionPool.getConnectionPool().checkOut();
            ppst = conn.prepareStatement(SQL_GET_ALL_USER_ID_ACTIVE);
            ppst.setInt(1, userId);
            ppst.setBoolean(2, active);
            rs = ppst.executeQuery();

            while (rs.next()) {
                User userDTO = new User();
                userDTO.setId(rs.getInt("id"));
                userDTO.setFirstName(rs.getString("first_name"));
                userDTO.setLastName(rs.getString("last_name"));
                userDTO.setSocialNo(rs.getString("social_no"));
                userDTO.setBirthDate(rs.getDate("birth_date"));
                userDTO.setEmail(rs.getString("email"));
                userDTO.setPrivilege(rs.getInt("privilege"));
                userDTO.setUsername(rs.getString("username"));
                userDTO.setPassword(rs.getString("password"));
                userDTO.setActive(rs.getBoolean("active"));
                userDTO.setCreationDate(rs.getTimestamp("creation_date"));
                userDTO.setPicture(rs.getString("picture"));
                userDTO.setStatus(rs.getInt("status"));
                userDTO.setChatStatus(rs.getInt("chat_status"));
                userDTO.setSalt(rs.getString("salt"));

                retVal.add(userDTO);
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

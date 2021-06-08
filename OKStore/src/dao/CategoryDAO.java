package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.Category;

public class CategoryDAO {

	
	
	private static final String SQL_GET_ALL = "SELECT * FROM category";
	private static final String SQL_GET_ALL_BY_ARTICLE_ID = "select * from category c join article_has_category ahc on c.id = ahc.category_id where ahc.article_id = ?";
	private static final String SQL_GET_BY_ID = "SELECT * FROM category WHERE id=?";
	private static final String SQL_INSERT = "INSERT INTO category (name) VALUES (?)";

	
	
	
	

	public static List<Category> getAll() {
		List<Category> categoryList = new LinkedList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL);
			
			rs = ppst.executeQuery();
			while (rs.next()) {
				Category retVal = new Category();
				retVal.setId(rs.getInt("id"));
				retVal.setName(rs.getString("name"));
				categoryList.add(retVal);
			}
			
			return (categoryList.size() > 0) ? categoryList : null;
			
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
	
	
	public static List<Category> getAllByArticleId(Integer articleId) {
		List<Category> categoryList = new LinkedList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL_BY_ARTICLE_ID);
			ppst.setInt(1, articleId);
			
			rs = ppst.executeQuery();
			while (rs.next()) {
				Category retVal = new Category();
				retVal.setId(rs.getInt("id"));
				retVal.setName(rs.getString("name"));
				categoryList.add(retVal);
			}
			
			return (categoryList.size() > 0) ? categoryList : null;
			
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
	
	
	public static Category getById(Integer id) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_BY_ID);
			ppst.setInt(1, id);
			
			rs = ppst.executeQuery();
			if (rs.next()) {
				Category retVal = new Category();
				retVal.setId(rs.getInt("id"));
				retVal.setName(rs.getString("name"));
				
				return retVal;
			}
			else
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
	
	public static Integer insert(Category category) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
			ppst.setString(1, category.getName());
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

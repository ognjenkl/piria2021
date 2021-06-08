package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import model.Article;
import model.Picture;

public class PictureDAO {

	// private static final String SQL_GET_ALL = "SELECT * FROM gallery_pictures
	// ORDER BY create_date desc;";
	// private static final String SQL_INSERT = "INSERT INTO gallery_pictures
	// (name, location) VALUES (?,?);";
	private static final String SQL_GET_ALL_BY_ARTICLE_ID = "SELECT * FROM picture WHERE article_id = ?";
	private static final String SQL_INSERT_PICTURE = "INSERT INTO picture (name, location, locationURL, article_id) VALUES (?,?,?,?)";
	private static final String SQL_DELETE = "DELETE FROM picture WHERE id = ?";
	//
	// public static List<GalleryPictureDTO> getAll() {
	// List<GalleryPictureDTO> retVal = new ArrayList<>();
	// Connection conn = null;
	// PreparedStatement ppst = null;
	// ResultSet resultSet = null;
	//
	// try {
	// conn = ConnectionPool.getConnectionPool().checkOut();
	// ppst = conn.prepareStatement(SQL_GET_ALL);
	//
	// resultSet = ppst.executeQuery();
	//
	// while (resultSet.next()){
	// GalleryPictureDTO gp = new GalleryPictureDTO();
	// gp.setId(resultSet.getInt(1));
	// gp.setName(resultSet.getString(2));
	// gp.setLocation(resultSet.getString(3));
	// gp.setCreateDate(resultSet.getDate(4));
	// retVal.add(gp);
	// }
	//
	// ppst.close();
	// return retVal;
	//
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return null;
	// } finally {
	// ConnectionPool.getConnectionPool().checkIn(conn);
	// }
	//
	// }
	//

	public static List<Picture> getAllByArticleId(Integer articleId) {
		List<Picture> retVal = new ArrayList<>();
		Connection conn = null;
		PreparedStatement ppst = null;
		ResultSet resultSet = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL_BY_ARTICLE_ID);
			ppst.setInt(1, articleId);
			resultSet = ppst.executeQuery();

			while (resultSet.next()) {
				Picture picture = new Picture();
				picture.setId(resultSet.getInt("id"));
				picture.setName(resultSet.getString("name"));
				picture.setLocation(resultSet.getString("location"));
				picture.setLocationURL(resultSet.getString("locationURL"));
				picture.setArticleId(articleId);
				retVal.add(picture);
			}

			ppst.close();
			return retVal;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			ConnectionPool.getConnectionPool().checkIn(conn);
		}

	}

	public static Integer insert(Picture picture) {
		Integer retVal = null;
		Connection conn = null;
		PreparedStatement ppst = null;
		ResultSet resultSet = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_INSERT_PICTURE, Statement.RETURN_GENERATED_KEYS);
			ppst.setString(1, picture.getName());
			ppst.setString(2, picture.getLocation());
			ppst.setString(3, picture.getLocationURL());
			ppst.setInt(4, picture.getArticleId());

			int rowCount = ppst.executeUpdate();
			resultSet = ppst.getGeneratedKeys();

			if (rowCount > 0 && resultSet.next())
				retVal = resultSet.getInt(1);

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

	public static Integer insert(Connection connection, Picture picture) {
		Integer retVal = null;
		Connection conn = connection;
		PreparedStatement ppst = null;
		ResultSet resultSet = null;

		try {
			ppst = conn.prepareStatement(SQL_INSERT_PICTURE, Statement.RETURN_GENERATED_KEYS);
			ppst.setString(1, picture.getName());
			ppst.setString(2, picture.getLocation());
			ppst.setString(3, picture.getLocationURL());
			ppst.setInt(4, picture.getArticleId());

			int rowCount = ppst.executeUpdate();
			resultSet = ppst.getGeneratedKeys();

			if (rowCount > 0 && resultSet.next())
				retVal = resultSet.getInt(1);

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
		}

	}
	
	public static Integer delete(Picture picture) {
		Connection conn = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_DELETE);
			ppst.setInt(1, picture.getId());

			return ppst.executeUpdate();

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
	
	// public static Integer insert(Picture picture) {
	// Integer retVal = null;
	// Connection conn = null;
	// PreparedStatement ppst = null;
	// ResultSet resultSet = null;
	//
	// try {
	// conn = ConnectionPool.getConnectionPool().checkOut();
	// ppst = conn.prepareStatement(SQL_INSERT,
	// Statement.RETURN_GENERATED_KEYS);
	// ppst.setInt(1, userId);
	// ppst.setInt(2, movieId);
	// ppst.setString(3, comment);
	//
	// int rowCount = ppst.executeUpdate();
	// resultSet = ppst.getGeneratedKeys();
	//
	// if (rowCount > 0 && resultSet.next()) {
	// retVal = resultSet.getInt(1);
	// }
	//
	// ppst.close();
	// return retVal;
	//
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// return null;
	// } finally {
	// if (ppst != null)
	//	try {
	//			ppst.close();
	//		} catch (SQLException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		ConnectionPool.getConnectionPool().checkIn(conn);
	// }
	//
	// }

}

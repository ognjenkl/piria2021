package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import model.Advertise;
import model.Article;
import model.ArticleUserHasArticle;
import model.Picture;

public class AdvertiseDAO {

	private static final String SQL_INSERT = "INSERT INTO advertise (user_advertiser_id, frequency, reach, price, target_date_from, target_date_to) VALUES (?,?,?,?,?,?)";
	private static final String SQL_GET_BY_USER_ADVERTISER_ID = "SELECT * FROM advertise WHERE user_advertiser_id=?";
	private static final String SQL_UPDATE = "UPDATE advertise SET user_advertiser_id = ?, frequency = ?, reach = ?, price = ?, start_date = ?, active = ?, count_ads = ?, count_ads_to_pay = ?, target_date_from = ?, target_date_to = ? WHERE id = ?";

	
	public static Advertise getByUserAdvertiserId(Integer userAdvertisesId) {
		Advertise retVal = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_BY_USER_ADVERTISER_ID);
			ppst.setInt(1, userAdvertisesId);
			rs = ppst.executeQuery();

			if (rs.next()) {
				retVal = new Advertise();
				retVal.setId(rs.getInt("id"));
				retVal.setUserAdvertiserId(rs.getInt("user_advertiser_id"));
				retVal.setFrequency(rs.getInt("frequency"));
				retVal.setReach(rs.getInt("reach"));
				retVal.setPrice(rs.getBigDecimal("price"));
				retVal.setStartDate(rs.getTimestamp("start_date"));
				retVal.setActive(rs.getBoolean("active"));
				retVal.setCountAds(rs.getInt("count_ads"));
				retVal.setCountAdsToPay(rs.getInt("count_ads_to_pay"));
				retVal.setTargetDateFrom(rs.getDate("target_date_from"));
				retVal.setTargetDateTo(rs.getDate("target_date_to"));
				
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
	
	
	public static Integer insert(Advertise advertise) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
//			conn.setAutoCommit(false);
			ppst = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
			 
			ppst.setInt(1, advertise.getUserAdvertiserId());
			ppst.setInt(2, advertise.getFrequency());
			if (advertise.getReach() != null)
				ppst.setInt(3, advertise.getReach());
			else
				ppst.setNull(3, Types.INTEGER);
			ppst.setBigDecimal(4, advertise.getPrice());
//			ppst.setTimestamp(5, advertise.getStartDate());
//			ppst.setBoolean(6, advertise.getActive());
//			ppst.setInt(7, advertise.getCountAds());
//			ppst.setInt(8, advertise.getCountAdsToPay());
			ppst.setDate(5, advertise.getTargetDateFrom());
			ppst.setDate(6, advertise.getTargetDateTo());
			
			Integer rowCount = ppst.executeUpdate();
			rs = ppst.getGeneratedKeys();
			Integer advertiseId = -1;
			if (rowCount > 0 && rs.next()) {
				advertiseId = rs.getInt(1);

//				conn.commit();
//				conn.setAutoCommit(true);
				return advertiseId;
			} else {
//				conn.rollback();
				return -1;
			}
		} catch (SQLException e) {
//			try {
//				conn.rollback();
//			} catch (SQLException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
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
//			try {
//				conn.setAutoCommit(true);
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			ConnectionPool.getConnectionPool().checkIn(conn);
		}

	}

	public static Integer update(Advertise advertise) {
		Integer retVal = null;
		Connection conn = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_UPDATE);
			
			ppst.setInt(1, advertise.getUserAdvertiserId());
			ppst.setInt(2, advertise.getFrequency());
			ppst.setInt(3, advertise.getReach());
			ppst.setBigDecimal(4, advertise.getPrice());
			ppst.setTimestamp(5, advertise.getStartDate());
			ppst.setBoolean(6, advertise.getActive());
			ppst.setInt(7, advertise.getCountAds());
			ppst.setInt(8, advertise.getCountAdsToPay());
			ppst.setDate(9, advertise.getTargetDateFrom());
			ppst.setDate(10, advertise.getTargetDateTo());
			ppst.setInt(11, advertise.getId());

			retVal = ppst.executeUpdate();

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
	
}

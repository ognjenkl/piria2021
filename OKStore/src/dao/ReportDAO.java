package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ReportDAO {

	private static final String SQL_GET_SUM_FOR_BUYERS = 
			"select if(isnull(u.username), '???' , u.username) as 'username', sum(a.price) as 'sum' "
			+ "from user_has_article u_has_a "
			+ "join article a on u_has_a.article_id = a.id "
			+ "left join user u on u_has_a.buyer_id = u.id "
			+ "where u_has_a.buyer_id is not null "
			+ "group by u_has_a.buyer_id;";
	private static final String SQL_GET_SUM_FOR_SELLERS = 
			"select if(isnull(u.username), '???' , u.username) as 'username', sum(a.price) as 'sum' "
			+ "from user_has_article u_has_a "
			+ "join article a on u_has_a.article_id = a.id "
			+ "left join user u on u_has_a.seller_id = u.id "
			+ "where u_has_a.seller_id is not null "
			+ "group by u_has_a.seller_id;";

	
	public static Map<String, Double> getSumForBuyers() {
		Map<String, Double> map = new HashMap<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_SUM_FOR_BUYERS);

			rs = ppst.executeQuery();
			
			while (rs.next()) 
				map.put(rs.getString("username"), rs.getDouble("sum"));

			return map;

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
	
	public static Map<String, Double> getSumForSellers() {
		Map<String, Double> map = new HashMap<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_SUM_FOR_SELLERS);

			rs = ppst.executeQuery();
			
			while (rs.next()) 
				map.put(rs.getString("username"), rs.getDouble("sum"));

			return map;

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

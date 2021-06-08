package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Account;


public class AccountDAO {

	private static final String SQL_GET_BY_SOCIAL_NO = "SELECT * FROM account WHERE social_no=? AND active=1";
	private static final String SQL_GET_BY_CARD_PARAMS = "SELECT * FROM account WHERE card_number=? AND card_type=? AND expiration_date=? AND pin=?";
	private static final String SQL_UPDATE_PAYMENT_SELLER = "UPDATE account SET amount=amount+? WHERE id=?";
	private static final String SQL_UPDATE_PAYMENT_BUYER = "UPDATE account SET amount=amount-? WHERE id=?";

	public static Account getBySocialNo(String socialNo) {
		Account retVal = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_BY_SOCIAL_NO);
			ppst.setString(1, socialNo);
			rs = ppst.executeQuery();

			if (rs.next()) {
				retVal = new Account();
				retVal.setId(rs.getInt("id"));
				retVal.setAccount(rs.getString("account"));
				retVal.setCardNumber(rs.getString("card_number"));
				retVal.setCardType(rs.getString("card_type"));
				retVal.setExpirationDate(rs.getString("expiration_date"));
				retVal.setPin(rs.getString("pin"));
				retVal.setAmount(rs.getBigDecimal("amount"));
				retVal.setActive(rs.getInt("active"));
				retVal.setSocialNo(rs.getString("social_no"));

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

	public static Account getByCardParams(String cardNumber, String cardType, String expirationDate, String cardPin) {
		Account retVal = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_BY_CARD_PARAMS);
			ppst.setString(1, cardNumber);
			ppst.setString(2, cardType);
			ppst.setString(3, expirationDate);
			ppst.setString(4, cardPin);
			rs = ppst.executeQuery();

			if (rs.next()) {
				retVal = new Account();
				retVal.setId(rs.getInt("id"));
				retVal.setAccount(rs.getString("account"));
				retVal.setCardNumber(rs.getString("card_number"));
				retVal.setCardType(rs.getString("card_type"));
				retVal.setExpirationDate(rs.getString("expiration_date"));
				retVal.setPin(rs.getString("pin"));
				retVal.setAmount(rs.getBigDecimal("amount"));
				retVal.setActive(rs.getInt("active"));
				retVal.setSocialNo(rs.getString("social_no"));

			}

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

		return retVal;
	}

	public static Integer payment(Integer buyerAccountId, Integer sellerAccountId, BigDecimal amount) {
		Integer retVal = null;
		Connection conn = null;
//		ResultSet rs = null;
		PreparedStatement ppstBuyer = null;
		PreparedStatement ppst1 = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			conn.setAutoCommit(false);
			ppstBuyer = conn.prepareStatement(SQL_UPDATE_PAYMENT_BUYER);
			ppstBuyer.setBigDecimal(1, amount);
			ppstBuyer.setInt(2, buyerAccountId);

			if (ppstBuyer.executeUpdate() > 0) {
				ppst1 = conn.prepareStatement(SQL_UPDATE_PAYMENT_SELLER);
				ppst1.setBigDecimal(1, amount);
				ppst1.setInt(2, sellerAccountId);
				retVal = ppst1.executeUpdate();

				if (retVal > 0)  {
					conn.commit();
					conn.setAutoCommit(true);
				} else
					conn.rollback();
				
			} else
				conn.rollback();

		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} finally {
			if (ppstBuyer != null)
				try {
					ppstBuyer.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (ppst1 != null)
				try {
					ppst1.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ConnectionPool.getConnectionPool().checkIn(conn);
		}
		
		return retVal;
	}

}

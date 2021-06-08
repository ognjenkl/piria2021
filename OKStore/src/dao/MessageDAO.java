package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import model.Message;
import model.MessageUser;

public class MessageDAO {

	private static final String SQL_GET_BY_ID = "SELECT * FROM message WHERE id=? AND active=1";
	private static final String SQL_GET_ALL_BY_ARTICLE_ID_READ_DIRECT_ACTIVE = "SELECT * FROM message WHERE article_id = ? AND read = ? AND direct = ? AND active = ?";
	private static final String SQL_INSERT = "INSERT INTO message (sender_id, receiver_id, article_id, content, creation_time, `read`, direct) VALUES (?,?,?,?,?,?,?)";
	private static final String SQL_UPDATE_ACTIVE = "UPDATE message SET active = 0 WHERE id = ?";
	private static final String SQL_GET_ALL_BY_RECEIVER_ID_DIRECT_ACTIVE = "SELECT * FROM message WHERE receiver_id = ? AND direct = ? AND active = ?";
	private static final String SQL_GET_ALL_BY_RECEIVER_ID_DIRECT_ACTIVE_CUSTOM = "SELECT m.*, u.username AS sender_name FROM message m JOIN user u ON m.sender_id = u.id WHERE m.receiver_id = ? AND m.direct = ? AND m.active = ?";
	private static final String SQL_COUNT_ALL_BY_RECEIVER_ID_DIRECT_ACTIVE = "SELECT COUNT(*) AS unread FROM message WHERE receiver_id = ? AND `read` = ? AND direct = ? AND active = ?";
	private static final String SQL_COUNT_BY_RECEIVER_ID_SENDER_ID_DIRECT_ACTIVE = "SELECT COUNT(*) AS unread FROM message WHERE receiver_id = ? AND sender_id = ? AND `read` = ? AND direct = ? AND active = ?";
	private static final String SQL_UPDATE_READ_BY_RECEIVER_ID_READ_DIRECT_ACTIVE = "UPDATE message SET `read` = ? WHERE receiver_id = ? AND `read` = ? AND direct = ? AND active = ?";
	private static final String SQL_UPDATE_READ_BY_SENDER_ID_READ_DIRECT_ACTIVE = "UPDATE message SET `read` = ? WHERE sender_id = ? AND `read` = ? AND direct = ? AND active = ?";    
	private static final String SQL_GET_ALL_BY_SENDER_ID_RECEIVER_ID_DIRECT_ACTIVE = "SELECT * FROM message WHERE sender_id IN (?,?) AND receiver_id IN (?,?) AND direct = ? AND active = ?";
    private static final String SQL_GET_ALL_BY_SENDER_ID_RECEIVER_ID_DIRECT_ACTIVE_CUSTOM = "SELECT m.*, u.username as sender_name FROM message m JOIN user u ON m.sender_id = u.id WHERE m.sender_id IN (?,?) AND m.receiver_id IN (?,?) AND m.direct = ? AND m.active = ? ORDER BY creation_time";
    
    public static Message getById(Integer id) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_BY_ID);
			ppst.setInt(1, id);

			rs = ppst.executeQuery();
			if (rs.next()) {
				Message retVal = new Message();
				retVal.setId(rs.getInt("id"));
				retVal.setSenderId(rs.getInt("sender_id"));
				retVal.setReceiverId(rs.getInt("receiver_id"));
				retVal.setArticleId(rs.getInt("article_id"));
				retVal.setContent(rs.getString("content"));
				retVal.setCreationTime(rs.getTimestamp("creation_time"));
				retVal.setRead(rs.getInt("read"));
				retVal.setDirect(rs.getInt("direct"));

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
    
	public static List<Message> getAllByArticleId(Integer articleId, Integer read, Integer direct, Integer active) {
		List<Message> retVal = new ArrayList<>();
		Connection conn = null;
		PreparedStatement ppst = null;
		ResultSet resultSet = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL_BY_ARTICLE_ID_READ_DIRECT_ACTIVE);
			ppst.setInt(1, articleId);
			ppst.setInt(2, read);
			ppst.setInt(3, direct);
			ppst.setInt(4, active);
			resultSet = ppst.executeQuery();

			while (resultSet.next()) {
				Message message = new Message();
				message.setId(resultSet.getInt("id"));
				message.setSenderId(resultSet.getInt("sender_id"));
				message.setReceiverId(resultSet.getInt("receiver_id"));
				message.setArticleId(articleId);
				message.setContent(resultSet.getString("content"));
				message.setCreationTime(resultSet.getTimestamp("creation_time"));
				message.setRead(read);
				message.setDirect(direct);
				retVal.add(message);
			}

			return retVal;

		} catch (SQLException e) {
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

	public static Integer insert(Message message) {
		Integer retVal = null;
		Connection conn = null;
		PreparedStatement ppst = null;
		ResultSet resultSet = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
			ppst.setInt(1, message.getSenderId());
			ppst.setInt(2, message.getReceiverId());
            if (message.getArticleId() != null)
                ppst.setInt(3, message.getArticleId());
            else
                ppst.setNull(3, Types.INTEGER);			
            ppst.setString(4, message.getContent());
			ppst.setTimestamp(5, message.getCreationTime());
			ppst.setInt(6, message.getRead());
			ppst.setInt(7, message.getDirect());

			int rowCount = ppst.executeUpdate();
			resultSet = ppst.getGeneratedKeys();

			if (rowCount > 0 && resultSet.next())
				retVal = resultSet.getInt(1);

			return retVal;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (ppst != null)
				try {
					ppst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			ConnectionPool.getConnectionPool().checkIn(conn);
		}

	}

	public static Integer updateActive(Integer messageId, Boolean active) {
		Connection conn = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_UPDATE_ACTIVE);
			ppst.setBoolean(1, active);
			ppst.setInt(2, messageId);

			return ppst.executeUpdate();

		} catch (SQLException e) {
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

	public static List<Message> getAllByReceiverIdAndDirectAndActive(Integer receiverId, Integer direct,
			Integer active) {
		List<Message> retVal = new ArrayList<>();
		Connection conn = null;
		PreparedStatement ppst = null;
		ResultSet resultSet = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL_BY_RECEIVER_ID_DIRECT_ACTIVE);
			ppst.setInt(1, receiverId);
			ppst.setInt(2, direct);
			ppst.setInt(3, active);
			resultSet = ppst.executeQuery();

			while (resultSet.next()) {
				Message message = new Message();
				message.setId(resultSet.getInt("id"));
				message.setSenderId(resultSet.getInt("sender_id"));
				message.setReceiverId(resultSet.getInt("receiver_id"));
				message.setArticleId(resultSet.getInt("article_id"));
				message.setContent(resultSet.getString("content"));
				message.setCreationTime(resultSet.getTimestamp("creation_time"));
				message.setRead(resultSet.getInt("read"));
				message.setDirect(resultSet.getInt("direct"));

				retVal.add(message);
			}

			return retVal;

		} catch (SQLException e) {
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
	
	public static List<MessageUser> getAllByReceiverIdAndDirectAndActiveCustom(Integer receiverId, Integer direct,
			Integer active) {
		List<MessageUser> retVal = new ArrayList<>();
		Connection conn = null;
		PreparedStatement ppst = null;
		ResultSet resultSet = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL_BY_RECEIVER_ID_DIRECT_ACTIVE_CUSTOM);
			ppst.setInt(1, receiverId);
			ppst.setInt(2, direct);
			ppst.setInt(3, active);
			resultSet = ppst.executeQuery();

			while (resultSet.next()) {
				MessageUser messageUser = new MessageUser();
				messageUser.setId(resultSet.getInt("id"));
				messageUser.setSenderId(resultSet.getInt("sender_id"));
				messageUser.setReceiverId(resultSet.getInt("receiver_id"));
				messageUser.setArticleId(resultSet.getInt("article_id"));
				messageUser.setContent(resultSet.getString("content"));
				messageUser.setCreationTime(resultSet.getTimestamp("creation_time"));
				messageUser.setRead(resultSet.getInt("read"));
				messageUser.setDirect(resultSet.getInt("direct"));
				messageUser.setSenderName(resultSet.getString("sender_name"));

				retVal.add(messageUser);
			}

			return retVal;

		} catch (SQLException e) {
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

    public static List<Message> getAllBySenderIdReceiverIdDirectActive(Integer senderId, Integer receiverId, Integer direct, Integer active) {
        List<Message> retVal = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ppst = null;
        ResultSet resultSet = null;

        try {
            conn = ConnectionPool.getConnectionPool().checkOut();
            ppst = conn.prepareStatement(SQL_GET_ALL_BY_SENDER_ID_RECEIVER_ID_DIRECT_ACTIVE);
            ppst.setInt(1, senderId);
            ppst.setInt(2, receiverId);
            ppst.setInt(3, senderId);
            ppst.setInt(4, receiverId);
            ppst.setInt(5, direct);
            ppst.setInt(6, active);
            resultSet = ppst.executeQuery();

            while (resultSet.next()) {
                Message message = new Message();
                message.setId(resultSet.getInt("id"));
                message.setSenderId(resultSet.getInt("sender_id"));
                message.setReceiverId(resultSet.getInt("receiver_id"));
                message.setArticleId(resultSet.getInt("article_id"));
                message.setContent(resultSet.getString("content"));
                message.setCreationTime(resultSet.getTimestamp("creation_time"));
                message.setRead(resultSet.getInt("read"));
                message.setDirect(resultSet.getInt("direct"));

                retVal.add(message);
            }

            return retVal;

        } catch (SQLException e) {
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
    
    public static List<MessageUser> getAllBySenderIdReceiverIdDirectActiveCustom(Integer senderId, Integer receiverId, Integer direct,
			Integer active) {
		List<MessageUser> retVal = new ArrayList<>();
		Connection conn = null;
		PreparedStatement ppst = null;
		ResultSet resultSet = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL_BY_SENDER_ID_RECEIVER_ID_DIRECT_ACTIVE_CUSTOM);
			ppst.setInt(1, senderId);
            ppst.setInt(2, receiverId);
            ppst.setInt(3, senderId);
            ppst.setInt(4, receiverId);
            ppst.setInt(5, direct);
            ppst.setInt(6, active);
			resultSet = ppst.executeQuery();

			while (resultSet.next()) {
				MessageUser messageUser = new MessageUser();
				messageUser.setId(resultSet.getInt("id"));
				messageUser.setSenderId(resultSet.getInt("sender_id"));
				messageUser.setReceiverId(resultSet.getInt("receiver_id"));
				messageUser.setArticleId(resultSet.getInt("article_id"));
				messageUser.setContent(resultSet.getString("content"));
				messageUser.setCreationTime(resultSet.getTimestamp("creation_time"));
				messageUser.setRead(resultSet.getInt("read"));
				messageUser.setDirect(resultSet.getInt("direct"));
				messageUser.setSenderName(resultSet.getString("sender_name"));

				retVal.add(messageUser);
			}

			return retVal;

		} catch (SQLException e) {
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
    
	public static Integer getMessageCountByReceiverIdReadDirectActive(Integer receiverId, Integer read, Integer direct, Integer active) {
		Integer retVal = null;
		Connection conn = null;
		PreparedStatement ppst = null;
		ResultSet resultSet = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_COUNT_ALL_BY_RECEIVER_ID_DIRECT_ACTIVE);
			ppst.setInt(1, receiverId);
			ppst.setInt(2, read);
			ppst.setInt(3, direct);
			ppst.setInt(4, active);
			resultSet = ppst.executeQuery();

			if (resultSet.next()) {
				retVal = resultSet.getInt("unread");
			}

			return retVal;

		} catch (SQLException e) {
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
	
	public static Integer getMessageCountByReceiverIdSenderIdReadDirectActive(Integer receiverId, Integer senderId, Integer read, Integer direct, Integer active) {
		Integer retVal = null;
		Connection conn = null;
		PreparedStatement ppst = null;
		ResultSet resultSet = null;

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_COUNT_BY_RECEIVER_ID_SENDER_ID_DIRECT_ACTIVE);
			ppst.setInt(1, receiverId);
			ppst.setInt(2, senderId);
			ppst.setInt(3, read);
			ppst.setInt(4, direct);
			ppst.setInt(5, active);
			resultSet = ppst.executeQuery();

			if (resultSet.next()) {
				retVal = resultSet.getInt("unread");
			}

			return retVal;

		} catch (SQLException e) {
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
	
	/**
	 * Update unread messages to new read value <code>readNew</code>.
	 * @param readNew the new read value
	 * @param receiverId
	 * @param read old read value
	 * @param direct
	 * @param active
	 * @return
	 */
	public static Integer updateReadByByReceiverIdReadDirectActive(Integer readNew, Integer receiverId, Integer read, Integer direct, Integer active) {
		Connection conn = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_UPDATE_READ_BY_RECEIVER_ID_READ_DIRECT_ACTIVE);
			ppst.setInt(1, readNew);
			ppst.setInt(2, receiverId);
			ppst.setInt(3, read);
			ppst.setInt(4, direct);
			ppst.setInt(5, active);

			return ppst.executeUpdate();

		} catch (SQLException e) {
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
	
	public static Integer updateReadBySenderIdReadDirectActive(Integer readNew, Integer senderId, Integer read, Integer direct, Integer active) {
		Connection conn = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_UPDATE_READ_BY_SENDER_ID_READ_DIRECT_ACTIVE);
			ppst.setInt(1, readNew);
			ppst.setInt(2, senderId);
			ppst.setInt(3, read);
			ppst.setInt(4, direct);
			ppst.setInt(5, active);

			return ppst.executeUpdate();

		} catch (SQLException e) {
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

package dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import javax.faces.context.FacesContext;

import bean.UserBean;
import model.Article;
import model.ArticleUserHasArticle;
import model.ArticleUserHasArticleUser;
import model.Picture;
import model.User;
import redis.Redis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import utilok.UtilOKSql;

public class ArticleDAO {

	private static final String SQL_GET_ALL = "SELECT * FROM article WHERE active=1";
	private static final String SQL_GET_ALL_BY_SELLER_ID = "SELECT * FROM article a JOIN user_has_article uha ON a.id = uha.article_id WHERE active=1 AND uha.seller_id=?";
	private static final String SQL_GET_ALL_BY_NAME = "SELECT * FROM article a WHERE active=1 AND a.status=5 AND a.name LIKE ?";
	private static final String SQL_GET_ALL_BY_DESCRIPTION_SHORT = "SELECT * FROM article a WHERE active=1 AND a.status=5 AND a.description_short LIKE ?";
	private static final String SQL_GET_ALL_BY_DESCRIPTION = "SELECT * FROM article a WHERE active=1 AND a.status=5 AND a.description LIKE ?";
	private static final String SQL_GET_ALL_BY_CATEGORY = "SELECT * FROM article a JOIN article_has_category ahc ON ahc.article_id = a.id JOIN category c ON c.id = ahc.category_id WHERE active=1 AND a.STATUS=5 AND c.name LIKE ?";
	private static final String SQL_GET_ALL_BY_COLUMN_AND_PARAM_1 = "SELECT * FROM article a WHERE a.active=1 AND a.status=5 AND a.columnName LIKE ?";
	private static final String SQL_GET_ALL_TO_BUY = "SELECT * FROM article a JOIN user_has_article uha ON a.id = uha.article_id WHERE active=1 AND (NOT uha.seller_id=?) AND a.status=?";
	private static final String SQL_GET_ALL_BOUGHT = "SELECT * FROM article a JOIN user_has_article uha ON a.id = uha.article_id WHERE uha.buyer_id=? AND a.status=?";
	private static final String SQL_GET_BY_ID = "SELECT * FROM article WHERE id=? AND active=1";
	private static final String SQL_INSERT = "INSERT INTO article (name, description_short, description, price, start_selling_date, end_selling_period) VALUES (?,?,?,?,?,?)";
	private static final String SQL_INSERT_ARTICLE_HAS_CATEGORY = "INSERT INTO article_has_category (article_id, category_id) VALUES (?,?)";
	private static final String SQL_INSERT_USER_HAS_ARTICLE_SELLER_AND_ARTICLE = "INSERT INTO user_has_article (seller_id, article_id) VALUES (?,?)";
	private static final String SQL_DELETE_ARTICLE_HAS_CATEOGORY_BY_ARTICLE_ID = "DELETE FROM article_has_category WHERE article_id = ?";
	private static final String SQL_UPDATE = "UPDATE article SET name = ?, description_short = ?, description = ?, price = ?, start_selling_date = ?, end_selling_period = ? WHERE id = ?";
	private static final String SQL_UPDATE_ACTIVE = "UPDATE article SET active = ? WHERE id = ?";
	private static final String SQL_UPDATE_STATUS = "UPDATE article SET status = ? WHERE id = ?";
	private static final String SQL_UPDATE_BUYER_ID_AND_STATUS = "UPDATE article a JOIN user_has_article uha ON a.id = uha.article_id SET uha.buyer_id = ?, uha.buy_date = ?, uha.rate_seller_expiration_date = ?, a.status = ? WHERE a.id = ?";
	private static final String SQL_UPDATE_RATE_SELLER = "UPDATE user_has_article uha SET uha.rate_seller = ?, uha.rate_date = ? WHERE uha.buyer_id = ? AND uha.article_id = ?";

	private static final String SQL_GET_ALL_BOUGHT_CUSTOM = "SELECT a.*, uha.id AS user_has_article_id, uha.seller_id, uha.buyer_id, uha.buy_date, uha.rate_seller, uha.rate_seller_expiration_date, uha.rate_date, buyer.username AS buyer_username, buyer.first_name AS buyer_first_name, buyer.last_name AS buyer_last_name, seller.username AS seller_username, seller.first_name AS seller_first_name, seller.last_name AS seller_last_name FROM article a JOIN user_has_article uha ON a.id = uha.article_id LEFT JOIN user buyer ON buyer.id = uha.buyer_id LEFT JOIN user seller ON seller.id = uha.seller_id WHERE a.status=? order by uha.buy_date desc";
	private static final String SQL_GET_ALL_BOUGHT_BY_BUYER_ID_CUSTOM = "SELECT a.*, uha.id AS user_has_article_id, uha.seller_id, uha.buyer_id, uha.buy_date, uha.rate_seller, uha.rate_seller_expiration_date, uha.rate_date FROM article a JOIN user_has_article uha ON a.id = uha.article_id WHERE uha.buyer_id=? AND a.status=? order by uha.buy_date desc";
	private static final String SQL_GET_BY_ARTICLE_ID_CUSTOM = "SELECT a.*, uha.id AS user_has_article_id, uha.seller_id, uha.buyer_id, uha.buy_date, uha.rate_seller, uha.rate_seller_expiration_date, uha.rate_date FROM article a JOIN user_has_article uha ON a.id = uha.article_id WHERE uha.article_id=?";
	private static final String SQL_GET_ALL_ACTIVE_TO_BUY_IN_CATEGORIES_CUSTOM = "SELECT DISTINCT a.* FROM article a JOIN user_has_article uha ON a.id = uha.article_id JOIN article_has_category ahc ON a.id = ahc.article_id WHERE active=1 AND (NOT uha.seller_id=?) AND a.status=? AND ahc.category_id IN (categoryPlaceholder)";

	public static List<Article> getAll() {
		List<Article> articleList = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL);

			rs = ppst.executeQuery();
			while (rs.next()) {
				Article retVal = new Article();
				retVal.setId(rs.getInt("id"));
				retVal.setName(rs.getString("name"));
				retVal.setDescriptionShort(rs.getString("description_short"));
				retVal.setDescription(rs.getString("description"));
				retVal.setPrice(rs.getBigDecimal("price"));
				retVal.setStatus(rs.getInt("status"));
				retVal.setCreationDate(rs.getTimestamp("creation_date"));
				retVal.setStartSellingDate(rs.getTimestamp("start_selling_date"));
				retVal.setEndSellingPeriod(
						rs.getInt("end_selling_period") > 0 ? rs.getInt("end_selling_period") : null);
				retVal.setActive(rs.getBoolean("active"));

				retVal.setCategoryList(CategoryDAO.getAllByArticleId(retVal.getId()));

				articleList.add(retVal);
			}

			return (articleList.size() > 0) ? articleList : null;

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

	public static List<Article> getAllBySellerId(Integer sellerId) {
		List<Article> articleList = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL_BY_SELLER_ID);
			ppst.setInt(1, sellerId);
			rs = ppst.executeQuery();

			while (rs.next()) {
				Article retVal = new Article();
				retVal.setId(rs.getInt("id"));
				retVal.setName(rs.getString("name"));
				retVal.setDescriptionShort(rs.getString("description_short"));
				retVal.setDescription(rs.getString("description"));
				retVal.setPrice(rs.getBigDecimal("price"));
				retVal.setStatus(rs.getInt("status"));
				retVal.setCreationDate(rs.getTimestamp("creation_date"));
				retVal.setStartSellingDate(rs.getTimestamp("start_selling_date"));
				retVal.setEndSellingPeriod(
						rs.getInt("end_selling_period") > 0 ? rs.getInt("end_selling_period") : null);
				retVal.setActive(rs.getBoolean("active"));

				retVal.setCategoryList(CategoryDAO.getAllByArticleId(retVal.getId()));

				retVal.setPictureList(PictureDAO.getAllByArticleId(retVal.getId()));

				articleList.add(retVal);
			}

			return (articleList.size() > 0) ? articleList : null;

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

	public static List<Article> getAllByName(String keyWord) {
		List<Article> articleList = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL_BY_NAME);
			ppst.setString(1, "%" + keyWord + "%");
			rs = ppst.executeQuery();

			while (rs.next()) {
				Article retVal = new Article();
				retVal.setId(rs.getInt("id"));
				retVal.setName(rs.getString("name"));
				retVal.setDescriptionShort(rs.getString("description_short"));
				retVal.setDescription(rs.getString("description"));
				retVal.setPrice(rs.getBigDecimal("price"));
				retVal.setStatus(rs.getInt("status"));
				retVal.setCreationDate(rs.getTimestamp("creation_date"));
				retVal.setStartSellingDate(rs.getTimestamp("start_selling_date"));
				retVal.setEndSellingPeriod(
						rs.getInt("end_selling_period") > 0 ? rs.getInt("end_selling_period") : null);
				retVal.setActive(rs.getBoolean("active"));

				retVal.setCategoryList(CategoryDAO.getAllByArticleId(retVal.getId()));

				retVal.setPictureList(PictureDAO.getAllByArticleId(retVal.getId()));

				articleList.add(retVal);
			}

			return (articleList.size() > 0) ? articleList : null;

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

	public static List<Article> getAllByDescriptionShort(String keyWord) {
		List<Article> articleList = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL_BY_DESCRIPTION_SHORT);
			ppst.setString(1, "%" + keyWord + "%");
			rs = ppst.executeQuery();

			while (rs.next()) {
				Article retVal = new Article();
				retVal.setId(rs.getInt("id"));
				retVal.setName(rs.getString("name"));
				retVal.setDescriptionShort(rs.getString("description_short"));
				retVal.setDescription(rs.getString("description"));
				retVal.setPrice(rs.getBigDecimal("price"));
				retVal.setStatus(rs.getInt("status"));
				retVal.setCreationDate(rs.getTimestamp("creation_date"));
				retVal.setStartSellingDate(rs.getTimestamp("start_selling_date"));
				retVal.setEndSellingPeriod(
						rs.getInt("end_selling_period") > 0 ? rs.getInt("end_selling_period") : null);
				retVal.setActive(rs.getBoolean("active"));

				retVal.setCategoryList(CategoryDAO.getAllByArticleId(retVal.getId()));

				retVal.setPictureList(PictureDAO.getAllByArticleId(retVal.getId()));

				articleList.add(retVal);
			}

			return (articleList.size() > 0) ? articleList : null;

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

	public static List<Article> getAllByDescription(String keyWord) {
		List<Article> articleList = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL_BY_DESCRIPTION);
			ppst.setString(1, "%" + keyWord + "%");
			rs = ppst.executeQuery();

			while (rs.next()) {
				Article retVal = new Article();
				retVal.setId(rs.getInt("id"));
				retVal.setName(rs.getString("name"));
				retVal.setDescriptionShort(rs.getString("description_short"));
				retVal.setDescription(rs.getString("description"));
				retVal.setPrice(rs.getBigDecimal("price"));
				retVal.setStatus(rs.getInt("status"));
				retVal.setCreationDate(rs.getTimestamp("creation_date"));
				retVal.setStartSellingDate(rs.getTimestamp("start_selling_date"));
				retVal.setEndSellingPeriod(
						rs.getInt("end_selling_period") > 0 ? rs.getInt("end_selling_period") : null);
				retVal.setActive(rs.getBoolean("active"));

				retVal.setCategoryList(CategoryDAO.getAllByArticleId(retVal.getId()));

				retVal.setPictureList(PictureDAO.getAllByArticleId(retVal.getId()));

				articleList.add(retVal);
			}

			return (articleList.size() > 0) ? articleList : null;

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

	public static List<Article> getAllByCategory(String keyWord) {
		List<Article> articleList = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL_BY_CATEGORY);
			ppst.setString(1, "%" + keyWord + "%");
			rs = ppst.executeQuery();

			while (rs.next()) {
				Article retVal = new Article();
				retVal.setId(rs.getInt("id"));
				retVal.setName(rs.getString("name"));
				retVal.setDescriptionShort(rs.getString("description_short"));
				retVal.setDescription(rs.getString("description"));
				retVal.setPrice(rs.getBigDecimal("price"));
				retVal.setStatus(rs.getInt("status"));
				retVal.setCreationDate(rs.getTimestamp("creation_date"));
				retVal.setStartSellingDate(rs.getTimestamp("start_selling_date"));
				retVal.setEndSellingPeriod(
						rs.getInt("end_selling_period") > 0 ? rs.getInt("end_selling_period") : null);
				retVal.setActive(rs.getBoolean("active"));

				retVal.setCategoryList(CategoryDAO.getAllByArticleId(retVal.getId()));

				retVal.setPictureList(PictureDAO.getAllByArticleId(retVal.getId()));

				articleList.add(retVal);
			}

			return articleList;

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

	public static List<Article> getAllByColumntAndParam1(String columnName, String keyWord) {
		List<Article> articleList = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL_BY_COLUMN_AND_PARAM_1.replaceAll("columnName", columnName));
			ppst.setString(1, "%" + keyWord + "%");
			rs = ppst.executeQuery();

			while (rs.next()) {
				Article retVal = new Article();
				retVal.setId(rs.getInt("id"));
				retVal.setName(rs.getString("name"));
				retVal.setDescriptionShort(rs.getString("description_short"));
				retVal.setDescription(rs.getString("description"));
				retVal.setPrice(rs.getBigDecimal("price"));
				retVal.setStatus(rs.getInt("status"));
				retVal.setCreationDate(rs.getTimestamp("creation_date"));
				retVal.setStartSellingDate(rs.getTimestamp("start_selling_date"));
				retVal.setEndSellingPeriod(
						rs.getInt("end_selling_period") > 0 ? rs.getInt("end_selling_period") : null);
				retVal.setActive(rs.getBoolean("active"));

				retVal.setCategoryList(CategoryDAO.getAllByArticleId(retVal.getId()));

				retVal.setPictureList(PictureDAO.getAllByArticleId(retVal.getId()));

				articleList.add(retVal);
			}

			return articleList;

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

	public static List<Article> getAllByBuyerIdAndArticleStatus(Integer buyerId, Integer articleStatus) {
		List<Article> articleList = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			if (articleStatus == 5)
				ppst = conn.prepareStatement(SQL_GET_ALL_TO_BUY);
			else if (articleStatus == 7)
				ppst = conn.prepareStatement(SQL_GET_ALL_BOUGHT);
			else
				return null;

			ppst.setInt(1, buyerId);
			ppst.setInt(2, articleStatus);
			rs = ppst.executeQuery();

			while (rs.next()) {
				Article retVal = new Article();
				retVal.setId(rs.getInt("id"));
				retVal.setName(rs.getString("name"));
				retVal.setDescriptionShort(rs.getString("description_short"));
				retVal.setDescription(rs.getString("description"));
				retVal.setPrice(rs.getBigDecimal("price"));
				retVal.setStatus(rs.getInt("status"));
				retVal.setCreationDate(rs.getTimestamp("creation_date"));
				retVal.setStartSellingDate(rs.getTimestamp("start_selling_date"));
				retVal.setEndSellingPeriod(
						rs.getInt("end_selling_period") > 0 ? rs.getInt("end_selling_period") : null);
				retVal.setActive(rs.getBoolean("active"));

				retVal.setCategoryList(CategoryDAO.getAllByArticleId(retVal.getId()));

				retVal.setPictureList(PictureDAO.getAllByArticleId(retVal.getId()));
				Properties prop = new Properties();
				try {
					prop.load(FacesContext.getCurrentInstance().getExternalContext()
							.getResourceAsStream("/WEB-INF/config/config.properties"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Integer maxEndSellingPeriod = Integer.parseInt(prop.getProperty("article.endSellingPerionMax"));

				if (retVal.getStartSellingDate() == null) // no start selling period
					articleList.add(retVal);
				else if (retVal.getEndSellingPeriod() != null && retVal.getStartSellingDate().getTime() // has end selling period
						+ retVal.getEndSellingPeriod() * 24L * 60 * 60 * 1000 > new Date().getTime())
					articleList.add(retVal);
				else if (retVal.getEndSellingPeriod() == null && retVal.getStartSellingDate().getTime() // no end selling period, uses max
						+ maxEndSellingPeriod * 24L * 60 * 60 * 1000 > new Date().getTime())
					articleList.add(retVal);
			}

			return (articleList.size() > 0) ? articleList : null;

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

	public static List<ArticleUserHasArticleUser> getAllBoughtCustom() {
		List<ArticleUserHasArticleUser> articleList = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL_BOUGHT_CUSTOM);
			// status 7 sold
			ppst.setInt(1, 7);
			rs = ppst.executeQuery();

			while (rs.next()) {
				ArticleUserHasArticleUser retVal = new ArticleUserHasArticleUser();
				retVal.setId(rs.getInt("id"));
				retVal.setName(rs.getString("name"));
				retVal.setDescriptionShort(rs.getString("description_short"));
				retVal.setDescription(rs.getString("description"));
				retVal.setPrice(rs.getBigDecimal("price"));
				retVal.setStatus(rs.getInt("status"));
				retVal.setCreationDate(rs.getTimestamp("creation_date"));
				retVal.setStartSellingDate(rs.getTimestamp("start_selling_date"));
				retVal.setEndSellingPeriod(
						rs.getInt("end_selling_period") > 0 ? rs.getInt("end_selling_period") : null);
				retVal.setActive(rs.getBoolean("active"));

				retVal.setCategoryList(CategoryDAO.getAllByArticleId(retVal.getId()));
				retVal.setPictureList(PictureDAO.getAllByArticleId(retVal.getId()));

				// user_has_article
				retVal.setUserHasArticleId(rs.getInt("user_has_article_id"));
				retVal.setSellerId(rs.getInt("seller_id"));
				retVal.setBuyerId(rs.getInt("buyer_id"));
				retVal.setBuyDate(rs.getTimestamp("buy_date"));
				retVal.setRateSeller(rs.getInt("rate_seller"));
				retVal.setRateSellerExpirationDate(rs.getTimestamp("rate_seller_expiration_date"));

				// buyere
				retVal.setBuyerUsername(rs.getString("buyer_username"));
				retVal.setBuyerFirstName(rs.getString("buyer_first_name"));
				retVal.setBuyerLastName(rs.getString("buyer_last_name"));
				
				// seller
				retVal.setSellerUsername(rs.getString("seller_username"));
				retVal.setSellerFirstName(rs.getString("seller_first_name"));
				retVal.setSellerLastName(rs.getString("seller_last_name"));
				
				articleList.add(retVal);
			}

			return (articleList.size() > 0) ? articleList : null;

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
	
	public static List<ArticleUserHasArticle> getAllBoughtByBuyerIdCustom(Integer buyerId) {
		List<ArticleUserHasArticle> articleList = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_ALL_BOUGHT_BY_BUYER_ID_CUSTOM);
			ppst.setInt(1, buyerId);
			// status 7 sold
			ppst.setInt(2, 7);
			rs = ppst.executeQuery();

			while (rs.next()) {
				ArticleUserHasArticle retVal = new ArticleUserHasArticle();
				retVal.setId(rs.getInt("id"));
				retVal.setName(rs.getString("name"));
				retVal.setDescriptionShort(rs.getString("description_short"));
				retVal.setDescription(rs.getString("description"));
				retVal.setPrice(rs.getBigDecimal("price"));
				retVal.setStatus(rs.getInt("status"));
				retVal.setCreationDate(rs.getTimestamp("creation_date"));
				retVal.setStartSellingDate(rs.getTimestamp("start_selling_date"));
				retVal.setEndSellingPeriod(
						rs.getInt("end_selling_period") > 0 ? rs.getInt("end_selling_period") : null);
				retVal.setActive(rs.getBoolean("active"));

				retVal.setCategoryList(CategoryDAO.getAllByArticleId(retVal.getId()));
				retVal.setPictureList(PictureDAO.getAllByArticleId(retVal.getId()));

				// user_has_article
				retVal.setUserHasArticleId(rs.getInt("user_has_article_id"));
				retVal.setSellerId(rs.getInt("seller_id"));
				retVal.setBuyerId(rs.getInt("buyer_id"));
				retVal.setBuyDate(rs.getTimestamp("buy_date"));
				retVal.setRateSeller(rs.getInt("rate_seller"));
				retVal.setRateSellerExpirationDate(rs.getTimestamp("rate_seller_expiration_date"));

				articleList.add(retVal);
			}

			return (articleList.size() > 0) ? articleList : null;

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
	
	public static List<Article> getSuggestionForUser(String buyerUsername) {
		List<Article> articleList = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;

		try {

			String keyBuy = "buy." + buyerUsername;
			String keyPreview = "preview." + buyerUsername;
			String keyLast = "last." + buyerUsername;
			Set<String> categoriesSet = Redis.getCategoriesForArticleSuggestion(keyBuy, keyPreview, keyLast);
			if (categoriesSet.size() > 0) {
				User buyer = UserDAO.getByUsername(buyerUsername);
				List<String> categoriesList = new ArrayList<String>(categoriesSet);
				String sqlAdjusted = UtilOKSql.adjustSqlInOperatorValues(SQL_GET_ALL_ACTIVE_TO_BUY_IN_CATEGORIES_CUSTOM,
						categoriesList, "categoryPlaceholder");

				conn = ConnectionPool.getConnectionPool().checkOut();
				ppst = conn.prepareStatement(sqlAdjusted);
				ppst.setInt(1, buyer.getId());
				// status 5 selling
				ppst.setInt(2, 5);
				for (int i = 0; i < categoriesList.size(); i++) {
					ppst.setInt(3 + i, Integer.valueOf(categoriesList.get(i)));
				}

				rs = ppst.executeQuery();

				while (rs.next()) {
					Article retVal = new Article();
					retVal.setId(rs.getInt("id"));
					retVal.setName(rs.getString("name"));
					retVal.setDescriptionShort(rs.getString("description_short"));
					retVal.setDescription(rs.getString("description"));
					retVal.setPrice(rs.getBigDecimal("price"));
					retVal.setStatus(rs.getInt("status"));
					retVal.setCreationDate(rs.getTimestamp("creation_date"));
					retVal.setStartSellingDate(rs.getTimestamp("start_selling_date"));
					retVal.setEndSellingPeriod(
							rs.getInt("end_selling_period") > 0 ? rs.getInt("end_selling_period") : null);
					retVal.setActive(rs.getBoolean("active"));

					retVal.setCategoryList(CategoryDAO.getAllByArticleId(retVal.getId()));

					retVal.setPictureList(PictureDAO.getAllByArticleId(retVal.getId()));

					articleList.add(retVal);
				}

				if (articleList.size() > 0 && articleList.size() <= 5) {
					return articleList;
				} else if (articleList.size() > 5) { 
					Random rand = new Random();
					List<Article> tempList = new ArrayList<Article>();
					for (int i = 0; i < 5; i++) {
						int tempIndex= rand.nextInt(articleList.size());
						tempList.add(articleList.get(tempIndex));
						articleList.remove(tempIndex);
					}
					return tempList;
				} else
					return null;

			} else {
				return articleList;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (JedisConnectionException e) {
//			e.printStackTrace();
			System.err.println("No redis connection!!!");
			return null;
		} catch (Exception e) {
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

//		return articleList;
	}

	public static ArticleUserHasArticle getByArticleIdCustom(Integer articleId) {
		ArticleUserHasArticle retVal = new ArticleUserHasArticle();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_BY_ARTICLE_ID_CUSTOM);
			ppst.setInt(1, articleId);
			rs = ppst.executeQuery();

			if (rs.next()) {
				retVal.setId(rs.getInt("id"));
				retVal.setName(rs.getString("name"));
				retVal.setDescriptionShort(rs.getString("description_short"));
				retVal.setDescription(rs.getString("description"));
				retVal.setPrice(rs.getBigDecimal("price"));
				retVal.setStatus(rs.getInt("status"));
				retVal.setCreationDate(rs.getTimestamp("creation_date"));
				retVal.setStartSellingDate(rs.getTimestamp("start_selling_date"));
				retVal.setEndSellingPeriod(
						rs.getInt("end_selling_period") > 0 ? rs.getInt("end_selling_period") : null);
				retVal.setActive(rs.getBoolean("active"));

				retVal.setCategoryList(CategoryDAO.getAllByArticleId(retVal.getId()));
				retVal.setPictureList(PictureDAO.getAllByArticleId(retVal.getId()));

				// user_has_article
				retVal.setUserHasArticleId(rs.getInt("user_has_article_id"));
				retVal.setSellerId(rs.getInt("seller_id"));
				retVal.setBuyerId(rs.getInt("buyer_id"));
				retVal.setBuyDate(rs.getTimestamp("buy_date"));
				retVal.setRateSeller(rs.getInt("rate_seller"));
				retVal.setRateSellerExpirationDate(rs.getTimestamp("rate_seller_expiration_date"));

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

	public static Article getById(Integer id) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_GET_BY_ID);
			ppst.setInt(1, id);

			rs = ppst.executeQuery();
			if (rs.next()) {
				Article retVal = new Article();
				retVal.setId(rs.getInt("id"));
				retVal.setName(rs.getString("name"));
				retVal.setDescriptionShort(rs.getString("description_short"));
				retVal.setDescription(rs.getString("description"));
				retVal.setPrice(rs.getBigDecimal("price"));
				retVal.setStatus(rs.getInt("status"));
				retVal.setCreationDate(rs.getTimestamp("creation_date"));
				retVal.setStartSellingDate(rs.getTimestamp("start_selling_date"));
				retVal.setEndSellingPeriod(rs.getInt("end_selling_period"));
				retVal.setActive(rs.getBoolean("active"));

				retVal.setCategoryList(CategoryDAO.getAllByArticleId(retVal.getId()));

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

	public static Integer insert(Article article, List<Integer> categoryIdList, Integer userId) {
//		public static Integer insert(Article article, List<String> categoryIdList, Integer userId) {
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ppst = null;
		PreparedStatement ppst1 = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			conn.setAutoCommit(false);
			ppst = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
			ppst.setString(1, article.getName());
			ppst.setString(2, article.getDescriptionShort());
			ppst.setString(3, article.getDescription());
			ppst.setBigDecimal(4, article.getPrice());
			ppst.setTimestamp(5, article.getStartSellingDate());
			if (article.getEndSellingPeriod() != null)
				ppst.setInt(6, article.getEndSellingPeriod());
			else
				ppst.setNull(6, Types.INTEGER);
			Integer rowCount = ppst.executeUpdate();
			rs = ppst.getGeneratedKeys();
			Integer articleId = -1;
			if (rowCount > 0 && rs.next()) {
				// TODO make it transactional (whole insert)
				articleId = rs.getInt(1);
				for (Integer categoryId : categoryIdList) {
//					for (String categoryId : categoryIdList) {
					ppst = conn.prepareStatement(SQL_INSERT_ARTICLE_HAS_CATEGORY);
					ppst.setInt(1, new Integer(articleId));
					ppst.setInt(2, new Integer(categoryId));
					ppst.executeUpdate();
				}

				if (article.getPictureList() != null && article.getPictureList().size() > 0)
					for (Picture picture : article.getPictureList()) {
						picture.setArticleId(articleId);
						PictureDAO.insert(conn, picture);
					}

				ppst1 = conn.prepareStatement(SQL_INSERT_USER_HAS_ARTICLE_SELLER_AND_ARTICLE);
				ppst1.setInt(1, userId);
				ppst1.setInt(2, articleId);
				ppst1.executeUpdate();

				conn.commit();
				conn.setAutoCommit(true);
				return articleId;
			} else {
				conn.rollback();
				return -1;
			}
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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

	}

	public static Integer update(Article article, List<String> selectedCategoryList) {
		Integer retVal = null;
		Connection conn = null;
		PreparedStatement ppst = null;
		PreparedStatement ppst1 = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_UPDATE);
			ppst.setString(1, article.getName());
			ppst.setString(2, article.getDescriptionShort());
			ppst.setString(3, article.getDescription());
			ppst.setBigDecimal(4, article.getPrice());
			ppst.setTimestamp(5, article.getStartSellingDate());
			if (article.getEndSellingPeriod() != null)
				ppst.setInt(6, article.getEndSellingPeriod());
			else
				ppst.setNull(6, Types.INTEGER);
			ppst.setInt(7, article.getId());

			retVal = ppst.executeUpdate();

			if (retVal != null && retVal > 0) {
				// TODO make it transaction
				ppst1 = conn.prepareStatement(SQL_DELETE_ARTICLE_HAS_CATEOGORY_BY_ARTICLE_ID);
				ppst1.setInt(1, new Integer(article.getId()));
				ppst1.executeUpdate();

				// can't cast directly selectCategoryList elements to strings
				for (int i = 0; i < selectedCategoryList.size(); i++) {
					ppst1 = conn.prepareStatement(SQL_INSERT_ARTICLE_HAS_CATEGORY);
					ppst1.setInt(1, article.getId());
					
					// for some reason it won't convert to string directly
					Object o = selectedCategoryList.get(i);
					Integer val = (Integer) o;
					ppst1.setInt(2, val);
					ppst1.executeUpdate();
				}

				for (Picture picture : article.getPictureList()) {
					if (picture.getCrudFlag() != null) {
						picture.setArticleId(article.getId());
						switch (picture.getCrudFlag()) {
						case 1:
							PictureDAO.insert(picture);
							break;
						case 4:
							PictureDAO.delete(picture);
						}
					}
				}

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
			if (ppst1 != null)
				try {
					ppst1.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			ConnectionPool.getConnectionPool().checkIn(conn);
		}

	}

	public static Integer updateActive(Integer articleId, Boolean active) {
		Integer retVal = null;
		Connection conn = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_UPDATE_ACTIVE);
			ppst.setBoolean(1, active);
			ppst.setInt(2, articleId);

			retVal = ppst.executeUpdate();

			if (retVal != null && retVal > 0)
				return retVal;
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

	public static Integer updateStatus(Integer articleId, Integer status) {
		Integer retVal = null;
		Connection conn = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_UPDATE_STATUS);
			ppst.setInt(1, status);
			ppst.setInt(2, articleId);

			retVal = ppst.executeUpdate();

			if (retVal != null && retVal > 0)
				return retVal;
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

	public static Integer updateBuyerIdAndStatus(Integer articleId, Integer buyerId, Integer articleStatus) {
		Integer retVal = null;
		Connection conn = null;
		PreparedStatement ppst = null;

		Date now = new Date();

		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_UPDATE_BUYER_ID_AND_STATUS);
			ppst.setInt(1, buyerId);
			ppst.setTimestamp(2, new java.sql.Timestamp(now.getTime()));
			ppst.setTimestamp(3, new java.sql.Timestamp(now.getTime() + 12 * 60 * 60 * 1000));
			ppst.setInt(4, articleStatus);
			ppst.setInt(5, articleId);

			retVal = ppst.executeUpdate();

			if (retVal != null && retVal > 0)
				return retVal;
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

	public static Integer updateRateSeller(Integer buyerId, Integer articleId, Integer rate) {
		Integer retVal = null;
		Connection conn = null;
		PreparedStatement ppst = null;
		try {
			conn = ConnectionPool.getConnectionPool().checkOut();
			ppst = conn.prepareStatement(SQL_UPDATE_RATE_SELLER);
			ppst.setInt(1, rate);
			ppst.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
			ppst.setInt(3, buyerId);
			ppst.setInt(4, articleId);

			retVal = ppst.executeUpdate();

			if (retVal != null && retVal > 0)
				return retVal;
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

	// public static Integer insert(Article article, List<String>
	// categoryIdList, Integer userId) {
	// Connection conn = null;
	// ResultSet rs = null;
	// PreparedStatement ppst = null;
	// try {
	// conn = ConnectionPool.getConnectionPool().checkOut();
	// ppst = conn.prepareStatement(SQL_INSERT,
	// Statement.RETURN_GENERATED_KEYS);
	// ppst.setString(1, article.getName());
	// ppst.setString(2, article.getDescriptionShort());
	// ppst.setString(3, article.getDescription());
	// ppst.setBigDecimal(4, article.getPrice());
	// ppst.setTimestamp(5, article.getStartSellingDate());
	// if (article.getEndSellingPeriod() != null)
	// ppst.setInt(6, article.getEndSellingPeriod());
	// else
	// ppst.setNull(6, Types.INTEGER);
	// Integer rowCount = ppst.executeUpdate();
	// rs = ppst.getGeneratedKeys();
	// Integer articleId = -1;
	// if (rowCount > 0 && rs.next()) {
	// // TODO make it transactional (whole insert)
	// articleId = rs.getInt(1);
	// for (String categoryId : categoryIdList){
	// ppst = conn.prepareStatement(SQL_INSERT_ARTICLE_HAS_CATEGORY);
	// ppst.setInt(1, new Integer(articleId));
	// ppst.setInt(2, new Integer(categoryId));
	// ppst.executeUpdate();
	// }
	//
	// for(Picture picture : article.getPictureList()) {
	// picture.setArticleId(articleId);
	// PictureDAO.insert(picture);
	// }
	//
	// ppst =
	// conn.prepareStatement(SQL_INSERT_USER_HAS_ARTICLE_SELLER_AND_ARTICLE);
	// ppst.setInt(1, userId);
	// ppst.setInt(2, articleId);
	// ppst.executeUpdate();
	//
	// return articleId;
	// }
	// else
	// return -1;
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

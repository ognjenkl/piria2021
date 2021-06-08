package bean;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import dao.ArticleDAO;
import dao.MessageDAO;
import dao.UserDAO;
import model.Article;
import model.ArticleUserHasArticle;
import model.ArticleUserHasArticleUser;
import model.Message;
import model.User;

@ViewScoped
@Named
public class ArticlesBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int NOT_PUBLISHED = 3;
	private static final int SELLING = 5;
	private static final int SOLD = 7;
	private static final int BLOCKED = 9;

	@Inject
	@ManagedProperty(value = "#{userBean}")
	UserBean userBean;

	@Inject
	@ManagedProperty(value = "#{messageBean}")
	MessageBean messageBean;
	
	List<Article> sellingArticleList;
	List<Article> buyingArticleList;
	List<ArticleUserHasArticle> boughtArticleList;
	// for admin review
	List<ArticleUserHasArticleUser> purchasesList;
	List<Article> suggestionArticleList;

	Article article;

	// message to send for some article
	String messageToSend;

	@PostConstruct
	public void init() {
		// FacesContext context = FacesContext.getCurrentInstance();
		// ExternalContext externalContext= context.getExternalContext();
		// HttpServletRequest request=
		// (HttpServletRequest)externalContext.getRequest();
		// System.out.println("ArticlesBean context path:" +
		// request.getContextPath());
		// System.out.println("ArticlesBean loclal port:" +
		// request.getLocalPort());
		// System.out.println("ArticlesBean remote port:" +
		// request.getRemotePort());
		// System.out.println("ArticlesBean server port:" +
		// request.getServerPort());
		// System.out.println("ArticlesBean request url:" +
		// request.getRequestURL());
		// System.out.println("ArticlesBean servlet path:" +
		// request.getServletPath());
		// System.out.println("ArticlesBean server name:" +
		// request.getServerName());
		// System.out.println("ArticlesBean request uri:" +
		// request.getRequestURI());
		// System.out.println("ArticlesBean path info:" +
		// request.getPathInfo());
		// System.out.println("ArticlesBean protocol:" + request.getProtocol());
		// System.out.println("pic url: " +
		// request.getRequestURL().toString().split(request.getContextPath())[0]
		// + "" + request.getContextPath());
		sellingArticleList = ArticleDAO.getAllBySellerId(userBean.getUser().getId());
		buyingArticleList = ArticleDAO.getAllByBuyerIdAndArticleStatus(userBean.getUser().getId(), SELLING);
		suggestionArticleList = ArticleDAO.getSuggestionForUser(userBean.getUser().getUsername());
		boughtArticleList = ArticleDAO.getAllBoughtByBuyerIdCustom(userBean.getUser().getId());
		purchasesList = ArticleDAO.getAllBoughtCustom();
	}

	public String displayAddArticle() {
		return "addArticle?faces-redirect=true";
	}

	public String deleteArticle(Integer articleId) {
		if (ArticleDAO.updateActive(articleId, false) > 0)
			return "sellingArticles?faces-redirect=true";
		return null;
	}

//	public String buyArticle(Article article) {
////	public String buyArticle(Integer articleId) {
////		if (ArticleDAO.updateBuyerIdAndStatus(articleId, userBean.getUser().getId(), SOLD) > 0) {
////			Jedis jedis = new Jedis();
////			Article article = ArticleDAO.getById(articleId);
////			for (Category category : article.getCategoryList()) {
////				jedis.zadd("buy." + userBean.getUsername(), jedis.zincrby("buy." + userBean.getUsername(), article.getPrice().doubleValue(), category.getId().toString()), category.getId().toString());
////				jedis.set("last." + userBean.getUsername(), article.getCategoryList().toString());
////			}
////			jedis.close();
////			return "buyingArticles?faces-redirect=true";
////		}
////			
////		return null;
//		Integer retVal = UtilOKArticle.buyArticle(userBean.getUser(), article, "buyingArticles?faces-redirect=true");
//		if (retVal != null && retVal == 0)
//			return "buyingArticles?faces-redirect=true";
//
//		return null;
//	}

	public String publishArticle(Integer articleId) {
		if (ArticleDAO.updateStatus(articleId, SELLING) > 0)
			return "sellingArticles?faces-redirect=true";
		return null;
	}

	public String stopSellingArticle(Integer articleId) {
		if (ArticleDAO.updateStatus(articleId, NOT_PUBLISHED) > 0)
			return "sellingArticles?faces-redirect=true";
		return null;
	}

	public String intToStr(int intVal) {
		String retVal = null;
		ResourceBundle bundle = FacesContext.getCurrentInstance().getApplication()
				.getResourceBundle(FacesContext.getCurrentInstance(), "msg");
		switch (intVal) {
		case NOT_PUBLISHED:
			retVal = bundle.getString("articleStatusNotPublished");
			break;
		case SELLING:
			retVal = bundle.getString("articleStatusSelling");
			break;
		case SOLD:
			retVal = bundle.getString("articleStatusSold");
			break;
		case BLOCKED:
			retVal = bundle.getString("articleStatusBlocked");
			break;
		default:
			retVal = bundle.getString("articleStatusNotPublished");
			break;
		}
		return retVal;
	}

	public String sendMessageToSeller(Integer articleId) {
		ArticleUserHasArticle articleUserHasArticle = ArticleDAO.getByArticleIdCustom(articleId);
		Message message = new Message();
		message.setSenderId(userBean.getUser().getId());
		message.setReceiverId(articleUserHasArticle.getSellerId());
		message.setArticleId(articleId);
		message.setContent(messageToSend);
		MessageDAO.insert(message);

		// send websocket message to seller
		messageBean.executeTopMessageChannel();
		// after message sent, clear the input field
		messageToSend = null;
		return null;
	}

	public String sendMessageReport(Integer articleId) {
		List<User> adminList = UserDAO.getByPrivilege(1);

		for (User admin : adminList) {
			Message message = new Message();
			message.setSenderId(userBean.getUser().getId());
			message.setReceiverId(admin.getId());
			message.setArticleId(articleId);
			message.setContent(messageToSend);
			MessageDAO.insert(message);
		}

		// send websocket message
		messageBean.executeTopMessageChannel();
		
		// after messages have been sent, clear the input field
		messageToSend = null;
		return null;
	}

	////////////////////////// setters and getters /////////////////////
	public List<Article> getSellingArticleList() {
		return sellingArticleList;
	}

	public void setSellingArticleList(List<Article> sellingArticleList) {
		this.sellingArticleList = sellingArticleList;
	}

	public List<Article> getBuyingArticleList() {
		return buyingArticleList;
	}

	public void setBuyingArticleList(List<Article> buyingArticleList) {
		this.buyingArticleList = buyingArticleList;
	}

	public List<ArticleUserHasArticle> getBoughtArticleList() {
		return boughtArticleList;
	}

	public void setBoughtArticleList(List<ArticleUserHasArticle> boughtArticleList) {
		this.boughtArticleList = boughtArticleList;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public String getMessageToSend() {
		return messageToSend;
	}

	public void setMessageToSend(String messageToSend) {
		this.messageToSend = messageToSend;
	}

	public List<Article> getSuggestionArticleList() {
		return suggestionArticleList;
	}

	public void setSuggestionArticleList(List<Article> suggestionArticleList) {
		this.suggestionArticleList = suggestionArticleList;
	}

	public List<ArticleUserHasArticleUser> getPurchasesList() {
		return purchasesList;
	}

	public void setPurchasesList(List<ArticleUserHasArticleUser> purchasesList) {
		this.purchasesList = purchasesList;
	}

	

}

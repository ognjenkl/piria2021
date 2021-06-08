package bean;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import org.primefaces.event.RateEvent;

import dao.ArticleDAO;
import dao.CategoryDAO;
import model.Article;
import model.ArticleUserHasArticle;
import model.Category;
import model.Picture;
import utilok.UtilOKJSF;
import utilok.UtilOKValidator;

@ViewScoped
@Named
public class ArticleBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	@ManagedProperty(value = "#{userBean}")
	UserBean userBean;

	Article article;
	// all categories from db
	List<Category> categoryList;
	// just selected categories ids as strings
	List<Integer> selectedCategoryList;
//	List<String> selectedCategoryList;
	// for picture
	Part articlePicPart;
	// config properties
	Properties prop;

	// search
	String keyWord;
	String[] searchCriteriaCheckBoxes;
	Map<String, String> searchCriteriaPredefinedValues;
	List<Article> foundArticlesList;
	Map<Integer, Article> foundArticlesMap;

	// rate
	Integer rateArticleId;
	Long createdBeanTime;

	@PostConstruct
	public void init() {
		article = new Article();
		categoryList = CategoryDAO.getAll();
		selectedCategoryList = new LinkedList<>();

		prop = new Properties();
		try {
			prop.load(FacesContext.getCurrentInstance().getExternalContext()
					.getResourceAsStream("/WEB-INF/config/config.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ResourceBundle msgResourceBundle = ResourceBundle.getBundle("resources.lang", locale, loader);
		searchCriteriaPredefinedValues = new HashMap<>();
		searchCriteriaPredefinedValues.put(msgResourceBundle.getString("searchCriteriaName"), "name");
		searchCriteriaPredefinedValues.put(msgResourceBundle.getString("searchCriteriaDescriptionShort"),
				"descriptionShort");
		searchCriteriaPredefinedValues.put(msgResourceBundle.getString("searchCriteriaDescription"), "description");
		searchCriteriaPredefinedValues.put(msgResourceBundle.getString("searchCriteriaCategory"), "category");

		createdBeanTime = new Date().getTime();
	}

	public String add() {
		List<String> fileNameList = UtilOKJSF.uploadArticlePicMultiple(articlePicPart, prop);
		List<Picture> pictureList = UtilOKJSF.populetePictureList(fileNameList, prop);
		if (pictureList != null && pictureList.size() > 0)
			if (article.getPictureList() != null)
				article.getPictureList().addAll(pictureList);
			else
				article.setPictureList(pictureList);

		if (UtilOKValidator.articleValidation(article, prop)) {
			if (ArticleDAO.insert(article, selectedCategoryList, userBean.getUser().getId()) > 0) {
				System.out.println("Insert article Success");
				return "sellingArticles?faces-redirect=true";
			} else {
				UtilOKJSF.jsfMessage("formAddArticle", "errorAddArticle");
				System.out.println("Insert article FAIL");
			}
		} else {
			UtilOKJSF.jsfMessage("formAddArticle", "errorAddArticle");
			System.out.println("Add article validation FAIL");
		}
		return null;
	}

	public String search() {
		foundArticlesList = new ArrayList<Article>();
		foundArticlesMap = new HashMap<>();
		List<Article> articleList = null;
		if (keyWord != null && !keyWord.equals("")) {
			if (searchCriteriaCheckBoxes.length > 0) {
				for (String c : searchCriteriaCheckBoxes) {
					switch (c) {
					case "name":
						articleList = ArticleDAO.getAllByColumntAndParam1("name", keyWord);
						for (Article a : articleList)
							foundArticlesMap.put(a.getId(), a);
						break;
					case "descriptionShort":
						articleList = ArticleDAO.getAllByColumntAndParam1("description_short", keyWord);
						for (Article a : articleList)
							foundArticlesMap.put(a.getId(), a);
						break;
					case "description":
						articleList = ArticleDAO.getAllByColumntAndParam1("description", keyWord);
						for (Article a : articleList)
							foundArticlesMap.put(a.getId(), a);
						break;
					case "category":
						articleList = ArticleDAO.getAllByCategory(keyWord);
						for (Article a : articleList)
							foundArticlesMap.put(a.getId(), a);
						break;
					}
				}
			} else {
				articleList = ArticleDAO.getAllByColumntAndParam1("name", keyWord);
				if (articleList != null)
					for (Article a : articleList)
						foundArticlesMap.put(a.getId(), a);
			}
		}

		foundArticlesList = new ArrayList<>(foundArticlesMap.values());

		return null;
	}

	public void onRate(RateEvent rateEvent) {
//		System.out.println("Rating: " + rateEvent.getRating());
//		System.out.println(((UIComponent) rateEvent.getComponent()).getId());
//		System.out.println(((UIComponent) rateEvent.getComponent()).getClientId());
//		System.out.println("rate article id: " + rateArticleId);
		if (rateArticleId != null) {
			ArticleDAO.updateRateSeller(userBean.getUser().getId(), rateArticleId, (Integer) rateEvent.getRating());
			rateArticleId = null;
		}
//		UtilOKJSF.jsfMessage("article" + rateArticleId, "errorRateTwelveHours");
	}

	public void rate(Integer articleId) {
		ArticleUserHasArticle articleUserHasArticle = ArticleDAO.getByArticleIdCustom(articleId);
//		ArticleUserHasArticle articleUserHasArticle = ArticleDAO.getByArticleIdCustom(rateArticleId);
		// 12 hours for rating after buying
		Long twelveHoursInMillis = 12 * 60 * 60 * 1000L;
		if (articleUserHasArticle.getBuyDate().getTime() + twelveHoursInMillis >= new Date().getTime()) {
			rateArticleId = articleId;
			// System.out.println("rate articleId: " + articleId);
		} else {
			UtilOKJSF.jsfMessage("boughtArticlesDataTable:1:ratePanel", "errorRateTwelveHours");
		}
	}

	public Long dateToLong(Date date) {
		return date.getTime();
	}
	
	public boolean rateAllowed(Timestamp rateTime) {
		Long twelveHoursInMillis = 12 * 60 * 60 * 1000L;
		return rateTime.getTime() + twelveHoursInMillis < new Date().getTime();
	}
	
	
	
	
	
	
	////////////////////////// getters and setters //////////////////////////
	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public List<Category> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<Category> categoryList) {
		this.categoryList = categoryList;
	}

//	public List<String> getSelectedCategoryList() {
//		return selectedCategoryList;
//	}
//
//	public void setSelectedCategoryList(List<String> selectedCategoryList) {
//		this.selectedCategoryList = selectedCategoryList;
//	}

	public Part getArticlePicPart() {
		return null;
	}

	public List<Integer> getSelectedCategoryList() {
		return selectedCategoryList;
	}

	public void setSelectedCategoryList(List<Integer> selectedCategoryList) {
		this.selectedCategoryList = selectedCategoryList;
	}

	public void setArticlePicPart(Part articlePicPart) {
		this.articlePicPart = articlePicPart;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public String[] getSearchCriteriaCheckBoxes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ResourceBundle msgResourceBundle = ResourceBundle.getBundle("resources.lang", locale, loader);
		searchCriteriaPredefinedValues = new HashMap<>();
		searchCriteriaPredefinedValues.put(msgResourceBundle.getString("searchCriteriaName"), "name");
		searchCriteriaPredefinedValues.put(msgResourceBundle.getString("searchCriteriaDescriptionShort"),
				"descriptionShort");
		searchCriteriaPredefinedValues.put(msgResourceBundle.getString("searchCriteriaDescription"), "description");
		searchCriteriaPredefinedValues.put(msgResourceBundle.getString("searchCriteriaCategory"), "category");
		return searchCriteriaCheckBoxes;
	}

	public void setSearchCriteriaCheckBoxes(String[] searchCriteriaCheckBoxes) {
		this.searchCriteriaCheckBoxes = searchCriteriaCheckBoxes;
	}

	public Map<String, String> getSearchCriteriaPredefinedValues() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ResourceBundle msgResourceBundle = ResourceBundle.getBundle("resources.lang", locale, loader);

		searchCriteriaPredefinedValues = new HashMap<>();
		searchCriteriaPredefinedValues.put(msgResourceBundle.getString("searchCriteriaName"), "name");
		searchCriteriaPredefinedValues.put(msgResourceBundle.getString("searchCriteriaDescriptionShort"),
				"descriptionShort");
		searchCriteriaPredefinedValues.put(msgResourceBundle.getString("searchCriteriaDescription"), "description");
		searchCriteriaPredefinedValues.put(msgResourceBundle.getString("searchCriteriaCategory"), "category");
		return searchCriteriaPredefinedValues;
	}

	public void setSearchCriteriaPredefinedValues(Map<String, String> searchCriteriaPredefinedValues) {
		this.searchCriteriaPredefinedValues = searchCriteriaPredefinedValues;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public List<Article> getFoundArticlesList() {
		return foundArticlesList;
	}

	public void setFoundArticlesList(List<Article> foundArticlesList) {
		this.foundArticlesList = foundArticlesList;
	}

	public Long getCreatedBeanTime() {
		return createdBeanTime;
	}

	public void setCreatedBeanTime(Long createdBeanTime) {
		this.createdBeanTime = createdBeanTime;
	}

	

}

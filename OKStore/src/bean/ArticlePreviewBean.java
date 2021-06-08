package bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

import com.fasterxml.jackson.databind.ObjectMapper;

import dao.ArticleDAO;
import dao.CategoryDAO;
import model.Article;
import model.Category;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

@ViewScoped
@Named
public class ArticlePreviewBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int NOT_PUBLISHED = 3;
	private static final int SELLING = 5;
	private static final int SOLD = 7;
	private static final int BLOCKED = 9;

	@Inject
	@ManagedProperty(value = "#{userBean}")
	UserBean userBean;

	// article to preview
	Article articlePreview;

	// to get data from params
	ObjectMapper objectMapper;

	// categories from db for the articlePreview
	List<Category> categoryList;

	// for picture
	Part articlePicPart;

	// set on get request from f:metada f:viewParam
	Integer articleToPreviewId;

	@PostConstruct
	public void init() {
		objectMapper = new ObjectMapper();
		String articleCameAsParamToPreview = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("articleToPreview");
		if (articleCameAsParamToPreview != null) {
			Article article = null;
			try {
				article = objectMapper.readValue(articleCameAsParamToPreview, Article.class);
				articlePreviewBusinessLogic(article);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void articlePreviewBusinessLogic(Article article) {
		try (Jedis jedis = new Jedis()) {
			articlePreview = article;
			if (jedis != null) {
				for (Category category : articlePreview.getCategoryList()) {
					jedis.zadd("preview." + userBean.getUsername(),
							jedis.zincrby("preview." + userBean.getUsername(), 1, category.getId().toString()),
							category.getId().toString());
				}
			}
		} catch (JedisConnectionException e) {
			System.err.println("No redis connection!!!");
		}

		categoryList = CategoryDAO.getAllByArticleId(articlePreview.getId());
	}

	// called from <f:viewAction> listener attribute
	public void articlePreviewForGetParam() {
		if (!FacesContext.getCurrentInstance().isPostback()) {
			Article article = ArticleDAO.getById(articleToPreviewId);
			articlePreviewBusinessLogic(article);
			
		}
	}

	public String statusToString() {
		String retVal = null;
		ResourceBundle bundle = FacesContext.getCurrentInstance().getApplication()
				.getResourceBundle(FacesContext.getCurrentInstance(), "msg");
		if (articlePreview != null) {
			switch (articlePreview.getStatus()) {
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
		} else
			retVal = bundle.getString("articleStatusNotPublished");
		
		return retVal;
	}

	public Article getArticlePreview() {
		return articlePreview;
	}

	public void setArticlePreview(Article articlePreview) {
		this.articlePreview = articlePreview;
	}

	public List<Category> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<Category> categoryList) {
		this.categoryList = categoryList;
	}

	public Integer getArticleToPreviewId() {
		return articleToPreviewId;
	}

	public void setArticleToPreviewId(Integer articleToPreviewId) {
		this.articleToPreviewId = articleToPreviewId;
	}

}

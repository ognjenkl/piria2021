package bean;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import model.Picture;
import utilok.UtilOKJSF;
import utilok.UtilOKValidator;

@ViewScoped
@Named
public class ArticleEditBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	@ManagedProperty(value = "#{userBean}")
	UserBean userBean;

	// artilcle to edit
	Article article;
	// to get data from params
	ObjectMapper objectMapper;

	// all categories from db
	List<Category> categoryList;
	// just selected categories ids as strings
	List<String> selectedCategoryList;
	// for picture
	Part articlePicPart;
	// config properties
	Properties prop;

	@PostConstruct
	public void init() {
		String articleCameAsParam = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("articleToEdit");
		objectMapper = new ObjectMapper();
		prop = new Properties();
		try {
			article = objectMapper.readValue(articleCameAsParam, Article.class);
			prop.load(FacesContext.getCurrentInstance().getExternalContext()
					.getResourceAsStream("/WEB-INF/config/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		categoryList = CategoryDAO.getAll();
		selectedCategoryList = new ArrayList<>();
		for (Category category : article.getCategoryList()) {
			selectedCategoryList.add(String.valueOf(category.getId()));
		}

	}

	public String editArticle() {
		if (UtilOKValidator.articleValidation(article, prop)) {
			// same code as in ArticleBean to add pictures
			List<String> fileNameList = UtilOKJSF.uploadArticlePicMultiple(articlePicPart, prop);
			List<Picture> pictureList = UtilOKJSF.populetePictureList(fileNameList, prop);
			if (pictureList != null && pictureList.size() > 0)
				if (article.getPictureList() != null)
					article.getPictureList().addAll(pictureList);
				else
					article.setPictureList(pictureList);

			if (article.getPictureList().stream()
					.filter(pic -> pic != null && pic.getCrudFlag() != null && pic.getCrudFlag() == 4)
					.count() == article.getPictureList().size()) {
				UtilOKJSF.jsfMessage("formEditArticle:picture", "errorEdtiArticleDeleteAll");
				return null;
			}

			if (ArticleDAO.update(article, selectedCategoryList) > 0) {
				for (Picture picture : article.getPictureList())
					if (picture.getCrudFlag() != null && picture.getCrudFlag() == 4)
						UtilOKJSF.deletePictureOnFileSystem(picture, prop);
				System.out.println("Update article Success");
				return "sellingArticles?faces-redirect=true";
			} else {
				UtilOKJSF.jsfMessage("formEditArticle", "errorEditArticle");
				System.out.println("Update article FAIL");
			}
		} else {
			UtilOKJSF.jsfMessage("formEditArticle", "errorEditArticle");
			System.out.println("Edit article validation FAIL");
		}
		return null;
	}

	/**
	 * Not a real deletion, just to select picture for deletion on article edit save
	 */
	public String deletePicture(Picture picture) {
		// hide delete button for this picture, 4 - to delete
		picture.setCrudFlag(4);
		System.out.println("to delete picture id: " + picture.getId());
		return null;
	}

	// setters and getters
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

	public List<String> getSelectedCategoryList() {
		return selectedCategoryList;
	}

	public void setSelectedCategoryList(List<String> selectedCategoryList) {
		this.selectedCategoryList = selectedCategoryList;
	}

	public Part getArticlePicPart() {
		return articlePicPart;
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

}

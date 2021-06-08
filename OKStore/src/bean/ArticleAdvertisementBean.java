package bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.annotation.ManagedProperty;
import javax.inject.Inject;
import javax.inject.Named;

import dao.AdvertiseDAO;
import dao.ArticleDAO;
import model.Advertise;
import model.Article;
import model.ArticleUserHasArticle;

@Named
@RequestScoped
public class ArticleAdvertisementBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int NOT_PUBLISHED = 3;
	private static final int SELLING = 5;
	private static final int SOLD = 7;
	private static final int BLOCKED = 9;
	
	@Inject
	@ManagedProperty(value = "#{userBean}")
	UserBean userBean;
	
//	Article articleToAdvertise;
	List<Article> articleToAdvertiseList;
	
	@PostConstruct
	public void init() {
		articleToAdvertiseList = ArticleDAO.getAllByBuyerIdAndArticleStatus(userBean.getUser().getId(), SELLING);
		// count++
		Map<Integer, Integer> sellerMap = new HashMap<>();
		if (articleToAdvertiseList != null) {
			for (Article a : articleToAdvertiseList) {
				ArticleUserHasArticle aa = ArticleDAO.getByArticleIdCustom(a.getId());
				if (sellerMap.get(aa.getSellerId()) == null) {
					Advertise advertise = AdvertiseDAO.getByUserAdvertiserId(aa.getSellerId());
					if (advertise != null) {
						advertise.setCountAdsToPay(advertise.getCountAdsToPay() + 1);
						AdvertiseDAO.update(advertise);
						sellerMap.put(aa.getSellerId(), advertise.getCountAdsToPay());
					}
					
					
				}
			}
		}
	}

	
	public List<Article> getArticleToAdvertiseList() {
		return articleToAdvertiseList;
	}

	public void setArticleToAdvertiseList(List<Article> articleToAdvertiseList) {
		this.articleToAdvertiseList = articleToAdvertiseList;
	}

	
}

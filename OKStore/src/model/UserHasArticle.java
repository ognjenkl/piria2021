package model;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

public class UserHasArticle {
	Integer id;
	Integer sellerId;
	Integer articleId;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy. HH:mm:ss",  timezone = "GMT+2")
	Timestamp buyDate;
	Integer rateSeller;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy. HH:mm:ss",  timezone = "GMT+2")
	Timestamp rateSellerExpirationDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy. HH:mm:ss",  timezone = "GMT+2")
	Timestamp rateDate;

	
	
	
	////////////////// setters and getters /////////////////////////
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSellerId() {
		return sellerId;
	}
	public void setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
	}
	public Integer getArticleId() {
		return articleId;
	}
	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}
	public Timestamp getBuyDate() {
		return buyDate;
	}
	public void setBuyDate(Timestamp buyDate) {
		this.buyDate = buyDate;
	}
	public Integer getRateSeller() {
		return rateSeller;
	}
	public void setRateSeller(Integer rateSeller) {
		this.rateSeller = rateSeller;
	}
	public Timestamp getRateSellerExpirationDate() {
		return rateSellerExpirationDate;
	}
	public void setRateSellerExpirationDate(Timestamp rateSellerExpirationDate) {
		this.rateSellerExpirationDate = rateSellerExpirationDate;
	}
	public Timestamp getRateDate() {
		return rateDate;
	}
	public void setRateDate(Timestamp rateDate) {
		this.rateDate = rateDate;
	}
	
}

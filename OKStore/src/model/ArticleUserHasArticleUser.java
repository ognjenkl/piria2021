package model;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ArticleUserHasArticleUser extends Article {

	private static final long serialVersionUID = 1L;

	Integer userHasArticleId;
	Integer sellerId;
	Integer buyerId;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy. HH:mm:ss",  timezone = "GMT+2")
	Timestamp buyDate;
	Integer rateSeller;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy. HH:mm:ss",  timezone = "GMT+2")
	Timestamp rateSellerExpirationDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy. HH:mm:ss",  timezone = "GMT+2")
	Timestamp rateDate;
	String buyerUsername;
	String buyerFirstName;
	String buyerLastName;
	String sellerUsername;
	String sellerFirstName;
	String sellerLastName;
	
	
	// setters and getters
	public Integer getUserHasArticleId() {
		return userHasArticleId;
	}

	public void setUserHasArticleId(Integer userHasArticleId) {
		this.userHasArticleId = userHasArticleId;
	}

	public Integer getSellerId() {
		return sellerId;
	}

	public void setSellerId(Integer sellerId) {
		this.sellerId = sellerId;
	}

	public Integer getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(Integer buyerId) {
		this.buyerId = buyerId;
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

	public String getBuyerUsername() {
		return buyerUsername;
	}

	public void setBuyerUsername(String buyerUsername) {
		this.buyerUsername = buyerUsername;
	}

	public String getBuyerFirstName() {
		return buyerFirstName;
	}

	public void setBuyerFirstName(String buyerFirstName) {
		this.buyerFirstName = buyerFirstName;
	}

	public String getBuyerLastName() {
		return buyerLastName;
	}

	public void setBuyerLastName(String buyerLastName) {
		this.buyerLastName = buyerLastName;
	}

	public String getSellerUsername() {
		return sellerUsername;
	}

	public void setSellerUsername(String sellerUsername) {
		this.sellerUsername = sellerUsername;
	}

	public String getSellerFirstName() {
		return sellerFirstName;
	}

	public void setSellerFirstName(String sellerFirstName) {
		this.sellerFirstName = sellerFirstName;
	}

	public String getSellerLastName() {
		return sellerLastName;
	}

	public void setSellerLastName(String sellerLastName) {
		this.sellerLastName = sellerLastName;
	}
	
}

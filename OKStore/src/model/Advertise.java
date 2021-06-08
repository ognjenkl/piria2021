package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class Advertise implements Serializable{

	private static final long serialVersionUID = 1L;

	Integer id;
	Integer userAdvertiserId;
	Integer frequency;
	Integer reach;
	BigDecimal price;
	Timestamp startDate;
	Boolean active;
	Integer countAds;
	Integer countAdsToPay;
	Date targetDateFrom;
	Date targetDateTo;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUserAdvertiserId() {
		return userAdvertiserId;
	}
	public void setUserAdvertiserId(Integer userAdvertiserId) {
		this.userAdvertiserId = userAdvertiserId;
	}
	public Integer getFrequency() {
		return frequency;
	}
	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}
	public Integer getReach() {
		return reach;
	}
	public void setReach(Integer reach) {
		this.reach = reach;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Timestamp getStartDate() {
		return startDate;
	}
	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public Integer getCountAds() {
		return countAds;
	}
	public void setCountAds(Integer countAds) {
		this.countAds = countAds;
	}
	public Integer getCountAdsToPay() {
		return countAdsToPay;
	}
	public void setCountAdsToPay(Integer countAdsToPay) {
		this.countAdsToPay = countAdsToPay;
	}
	public Date getTargetDateFrom() {
		return targetDateFrom;
	}
	public void setTargetDateFrom(Date targetDateFrom) {
		this.targetDateFrom = targetDateFrom;
	}
	public Date getTargetDateTo() {
		return targetDateTo;
	}
	public void setTargetDateTo(Date targetDateTo) {
		this.targetDateTo = targetDateTo;
	}
	
	
}

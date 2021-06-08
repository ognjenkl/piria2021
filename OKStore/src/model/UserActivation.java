package model;

import java.io.Serializable;
import java.sql.Timestamp;

public class UserActivation implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	String key;
	Integer userId;
	Timestamp creationDate;
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}
	@Override
	public String toString() {
		return "UserActivation [key=" + key + ", userId=" + userId + ", creationDate=" + creationDate + "]";
	}
	
	
}

package model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Picture {
	Integer id;
	String name;
	String location;
	String locationURL;
	Integer articleId;
	// 1 to create, 2 to read, 3 to update, 4 to delete
	Integer crudFlag;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLocationURL() {
		return locationURL;
	}
	public void setLocationURL(String locationURL) {
		this.locationURL = locationURL;
	}
	public Integer getArticleId() {
		return articleId;
	}
	public void setArticleId(Integer articleId) {
		this.articleId = articleId;
	}
	public Integer getCrudFlag() {
		return crudFlag;
	}
	public void setCrudFlag(Integer crudFlag) {
		this.crudFlag = crudFlag;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}
	
}

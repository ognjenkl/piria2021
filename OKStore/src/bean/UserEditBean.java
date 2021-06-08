package bean;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dao.UserDAO;
import model.User;

@ViewScoped
@Named
public class UserEditBean implements Serializable{

	private static final long serialVersionUID = 1L;

	User user;
	
	ObjectMapper objectMapper;
	
	@PostConstruct
	private void init() {
		String came = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("userToEdit");
		objectMapper = new ObjectMapper();
		try {
			user = objectMapper.readValue(came, User.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	
	public String editUser(){
		UserDAO.updateAdminEditUser(user);
		return "manageUsers?faces-redirect=true";
	}
	
}

package bean;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import dao.UserDAO;
import model.User;
import utilok.UtilOKJSF;
import utilok.UtilOKSecure;

/**
 * @author ognjen
 *
 */
@ViewScoped
@Named(value = "userAccount")
public class UserAccountBean implements Serializable{

	private static final long serialVersionUID = 1L;

	@Inject
	@ManagedProperty(value = "#{userBean}")
	UserBean userBean;

	User user;

	@PostConstruct
	public void init() {
		if (userBean != null) {
			user = new User();
			user.setId(userBean.getUser().getId());
			user.setFirstName(userBean.getUser().getFirstName());
			user.setLastName(userBean.getUser().getLastName());
			user.setSocialNo(userBean.getUser().getSocialNo());
			user.setBirthDate(userBean.getUser().getBirthDate());
			user.setEmail(userBean.getUser().getEmail());
			user.setPrivilege(userBean.getUser().getPrivilege());
			user.setUsername(userBean.getUser().getUsername());
			user.setPassword(userBean.getUser().getPassword());
			user.setActive(userBean.getUser().isActive());
			user.setCreationDate(userBean.getUser().getCreationDate());
		}
	}

	public String userSave() {
		Properties prop = new Properties();
		try {
			prop.load(FacesContext.getCurrentInstance().getExternalContext()
					.getResourceAsStream("/WEB-INF/config/config.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (user.getPassword() != null && !user.getPassword().equals("")){
			user.setSalt(UtilOKSecure.generateSalt(Integer.parseInt(prop.getProperty("saltLength"))));
			try {
				user.setPassword(UtilOKSecure.transformStringPasswordToByteArray(user.getPassword(), user.getSalt(), prop.getProperty("hashAlgorithm"), Integer.parseInt(prop.getProperty("hashRepeatNum"))));
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (UserDAO.updateUserWithoutPrivilege(user) > 0) {
				userBean.setUser(user);
				FacesContext.getCurrentInstance().addMessage("formUser",
						new FacesMessage(UtilOKJSF.getLangMessage("saveUserSuccessfulMessage")));
			} else
				FacesContext.getCurrentInstance().addMessage("formUser",
						new FacesMessage(UtilOKJSF.getLangMessage("saveUserErrorMessage")));
		} else if (UserDAO.updateUserWithoutPasswordAndPrivilege(user) > 0) {
			userBean.setUser(user);
			FacesContext.getCurrentInstance().addMessage("formUser",
						new FacesMessage(UtilOKJSF.getLangMessage("saveUserSuccessfulMessage")));
		} else
			FacesContext.getCurrentInstance().addMessage("formUser",
						new FacesMessage(UtilOKJSF.getLangMessage("saveUserErrorMessage")));

		return null;
	}

//	public String userSave(User user) {
//		// user.setEditable(false);
//		// this.user = user;
//		// loginBean.updateUser(user);
//		//
//		System.out.println("user account bean save user account with argument");
//		return null;
//	}
//
//	public String accoountEdit(User user) {
//		user.setEditable(true);
//		this.user = user;
//
//		return null;
//	}
//
//	public String userActivate(User user) {
//		user.setActive(true);
//		return null;
//	}
//
//	public String userDeactivate(User user) {
//		user.setActive(false);
//		return null;
//	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

}

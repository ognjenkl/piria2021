package bean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import dao.UserActivationDAO;
import dao.UserDAO;
import model.User;
import utilok.UtilOKJSF;

/**
 * @author ognjen
 *
 */
@ViewScoped
@Named
public class ManageUsersBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	@ManagedProperty(value = "#{userBean}")
	UserBean userBean;

	List<User> usersList;

	@PostConstruct
	public void init() {
		usersList = UserDAO.getAll();
	}

	public String activateUser(User user) {
		user.setActive(true);
		UserDAO.activate(user.getId());
		UserActivationDAO.deleteByUserId(user.getId());
		return null;
	}

	public String deactivateUser(User user) {
		if (user.getId() == userBean.getUser().getId()) {
			UtilOKJSF.jsfMessage("manageUsersForm", "errorDeactivateYourself");
			return null;
		}
		user.setActive(false);
		UserDAO.deactivate(user.getId());
		return null;
	}
	
	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public List<User> getUsersList() {
		return usersList;
	}

	public void setUsersList(List<User> usersList) {
		this.usersList = usersList;
	}

}

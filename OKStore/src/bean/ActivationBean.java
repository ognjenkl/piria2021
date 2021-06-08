package bean;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.annotation.ManagedProperty;
import javax.inject.Inject;
import javax.inject.Named;

import dao.UserActivationDAO;
import dao.UserDAO;
import model.UserActivation;
import model.User;

@RequestScoped
@Named
public class ActivationBean implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject
	@ManagedProperty(value = "#{param.key}")
	private String key;

	private boolean valid;

	@PostConstruct
	public void init() {
		User userRegister = null;
		UserActivation userActivationDTO = null;

		if ((userActivationDTO = UserActivationDAO.getByKey(key)) != null
				&& (userRegister = UserDAO.getByIdAndActive(userActivationDTO.getUserId(), false)) != null) {
			if ((new Date().getTime() - userActivationDTO.getCreationDate().getTime()) < 4 * 60 * 60 * 1000) {
				if (UserDAO.activate(userRegister.getId()) > 0) {
					UserActivationDAO.deleteByKey(key);
					valid = true;
				} else
					System.out.println("Activation was NOT successful");
			} else {
				System.out.println("Activation link time expired");
			}
		} else
			valid = false;
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param valid
	 *            the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key
	 *            the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

}

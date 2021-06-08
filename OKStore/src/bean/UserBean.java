package bean;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.annotation.FacesConfig;
import javax.faces.annotation.ManagedProperty;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.xml.rpc.ServiceException;

import dao.UserActivationDAO;
import dao.UserDAO;
import model.UserActivation;
//import payment.OKBank;
//import payment.OKBankServiceLocator;
import model.Article;
import model.User;
import utilok.Mailer;
import utilok.UtilOKJSF;
import utilok.UtilOKSecure;
import utilok.UtilOKValidator;


/**
 * @author ognjen
 *
 */
@Named
@SessionScoped
@FacesConfig(version = FacesConfig.Version.JSF_2_3)
public class UserBean implements Serializable {

	private static final long serialVersionUID = 1L;

	// login: user is set if logged in
	User user;

	// prelogin
	String username;
	String password;
	boolean loggedIn;

	// registration: set properties during registration process
	User userRegister;

	// localization
	Locale locale;
	Map<String, String> availableItems;
	String language;

	// Localization messages
	@Inject
	@ManagedProperty("#{msg}")
	ResourceBundle msgResourceBundle;

	// config properties
	Properties prop;

	// admin
	List<User> usersAll;

	// mails send
	// Mailer mailer;

	@Inject
	FacesContext facesContext;
	
	@Inject
	private ExternalContext externalContext;

	@Inject
	private UIViewRoot viewRoot;

	public UserBean() {
		username = "";
		password = "";
		loggedIn = false;
		user = new User();
		userRegister = new User();
		availableItems = new TreeMap<>();
		locale = null;

		// admin
		usersAll = null;
		prop = new Properties();
		
//		OKBank okBank;
//		try {
//			okBank = new OKBankServiceLocator().getOKBank();
//			System.out.println(okBank.check());
//			okBank.processPaymentSecure(""
//					+ "{\"envelope\":\"fOFsqsXPjCD2PeWH015NKGNw1IExPGqhLFFxL0i9y58sCtkXRo/BLeAJqxb5beq6thS0J4EFrnMHKO3K5qkjm+oCoCz0fHwA5FDSZsjHmsxIDrgMb4hUUI3FY7ajtpTvhkBUkGEuItKpW8QjhbVJlvOcgktRfih8Zg1IGP5n+Zff2pPWCPA0WG7rfaL3zHzM4l848WoJDxg2Y02LYgCPVXToid/9vRsGznPHe5ZX/RI3dsF1JdnItBkV8AKC7BQKtIx8QphwbgXDZeT4t1efiBBi3wsbgtZ4YdUxliFWert3YSgP3lVNt5A/Qh0bWP1ah8gu3Z6CRRKIs4B8xCnH7Q==\","
//					+ "\"digsig\":\"LK6V0xUCuhb92wRNknsDk+/ocVtkXlvyRu0JEOqcX0BQdxyU/Oe2yKB7I7BqIQBdaqMeqyvW8jCOZlBAd4avDSP4cLIR4+U1Z82xkgE95WAvn5/9cepBv9ayu8LXqIcmTEAVexdEqeQlhdiu4aBoLB9LeBI9bSsFZIuLMGS1/s7UCGlBT4WhsU3RsyIs0NnJqEeF+G+3dTlIjyxF2jBqLTrlWbElkRQekFDFFapO3rk5/5jOTZuCTPSuQRHJ9KVQQ5qHXhqz3tLdHTWk3pN3TAFASyGNDBxIdvGCjIYw13WM+IdZ+IAsJPzNDFDmpH2BNSX8hwbQQ+w+4ljscreb3A==\"}");
//		} catch (ServiceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	@PostConstruct
	public void init() {
		try {
			prop.load(externalContext.getResourceAsStream("/WEB-INF/config/config.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		availableItems.put(prop.getProperty("langShortEn"), prop.getProperty("langLongEn"));
		availableItems.put(prop.getProperty("langShortSr"), prop.getProperty("langLongSr"));
		availableItems.put(prop.getProperty("langShortFr"), prop.getProperty("langLongFr"));
		availableItems.put(prop.getProperty("langShortDe"), prop.getProperty("langLongDe"));
		
		String languageFromCookie = readLanguageFromCookie();
		if (languageFromCookie != null) {
			// set language from cookie if already defined
			setLanguage(languageFromCookie);
		} else {
			locale = externalContext.getRequestLocale();
			// setLanguage("sr");

		}


		// mailer = new Mailer(prop.getProperty("mail.smtp"),
		// Integer.valueOf(prop.getProperty("mail.port")),
		// prop.getProperty("mail.username"), prop.getProperty("mail.password"),
		// Boolean.valueOf(prop.getProperty("mail.sslOn")));

		userRegister = new User();
		
	}

	public String login() {
		if (UtilOKValidator.isValidExcessiveCharsLength("username", username, 1000)
				&& UtilOKValidator.isValidExcessiveCharsLength("password", password, 1000)
				&& UtilOKValidator.isValidAllowedCharsAndLength("username", username, 1, 45)
				&& UtilOKValidator.isValidBasic("username", username)
				&& UtilOKValidator.isValidAllowedCharsAndLength("password", password, 1, 512)
				&& UtilOKValidator.isValidBasic("password", password)) {
			User loggedUser = UserDAO.getByUsername(username);
			try {
				if (loggedUser != null && UtilOKSecure.verifyPassword(password, loggedUser.getPassword(),
						loggedUser.getSalt(), prop.getProperty("hashAlgorithm"),
						Integer.parseInt(prop.getProperty("hashRepeatNum")))) {
					user = loggedUser;
					externalContext.getSessionMap().put("userBean", this);
					loggedIn = true;

				} else {
					loggedIn = false;
					facesContext.addMessage("loginForm",
							new FacesMessage(UtilOKJSF.getLangMessage("loginErrorMessage")));
					return null;
				}
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			loggedIn = false;
			facesContext.addMessage("loginForm",
					new FacesMessage(UtilOKJSF.getLangMessage("loginErrorMessage")));
		}

//		return null;
		return "guest?faces-redirect=true";
	}

	public String logout() {
		((HttpSession) externalContext.getSession(false)).invalidate();
		loggedIn = false;
		// reset user to guest
		user = new User();
		userRegister = new User();
		return "guest?faces-redirect=true";
	}

	/**
	 * Add user when user registers himself.
	 * 
	 * @return
	 */
	public String register() {
		int userId = 0;

		if (addUserValidation(userRegister, true)) {
			// if user does NOT exist, start registration i.e. insert new user
			// into db
			if (UserDAO.getByUsername(userRegister.getUsername()) == null) {
				userRegister.setSalt(UtilOKSecure.generateSalt(Integer.parseInt(prop.getProperty("saltLength"))));
				try {
					userRegister.setPassword(UtilOKSecure.transformStringPasswordToByteArray(userRegister.getPassword(),
							userRegister.getSalt(), prop.getProperty("hashAlgorithm"),
							Integer.parseInt(prop.getProperty("hashRepeatNum"))));
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// not guest (10 provilege) any more
				userRegister.setPrivilege(Integer.parseInt(msgResourceBundle.getString("userNum")));

				if ((userId = UserDAO.insert(userRegister)) > 0) {

					UUID uuid = UUID.randomUUID();
					UserActivation userActivation = new UserActivation();
					userActivation.setKey(uuid.toString());
					userActivation.setUserId(userId);

					if (UserActivationDAO.insert(userActivation) > 0)
						sendMailWithActivationLink(uuid.toString(), userRegister.getEmail());

					// reset object for user registration
					userRegister = new User();

					UtilOKJSF.jsfMessage("formAddUser", "addUserSuccess");
					System.out.println("Register user insert success");
				} else {
					UtilOKJSF.jsfMessage("formAddUser", "errorAddUsserUnsuccessful");
					System.out.println("Register add User: Insert FAIL");
				}
			} else {
				UtilOKJSF.jsfMessage("formAddUser", "errorAddUsserUnsuccessful");
				System.out.println("Add User: user already exists");
			}
		} else
			UtilOKJSF.jsfMessage("formAddUser", "errorAddUsserUnsuccessful");

		return null;
	}

	public String getLanguage() {
		return locale.getLanguage();
	}

	public void setLanguage(String language) {
		// language by ISO 639 code 2 letters
		locale = new Locale(language);
		viewRoot.setLocale(locale);
		createCookieLang(language);
	}

	/**
	 * Reads language cookie
	 * 
	 * @return
	 */
	public String readLanguageFromCookie() {
		Map<String, Object> map = externalContext.getRequestCookieMap();
		Cookie c = null;
//		System.out.println("prop size: " + prop.size());
		if (prop != null && prop.size() > 0)
			c = (Cookie) map.get(prop.getProperty("cookieLangName"));
		try {
			if (c != null) {
				return URLDecoder.decode(c.getValue(), "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Creates language cookie.
	 * 
	 * @param language
	 */
	public void createCookieLang(String language) {
		try {

			Map<String, Object> propertiesCookie = new Hashtable<>();
			propertiesCookie.put("secure", false);
			propertiesCookie.put("domain", prop.getProperty("cookieLangDomain"));
			propertiesCookie.put("path", prop.getProperty("cookieLangPath"));
			propertiesCookie.put("httpOnly", true);
			propertiesCookie.put("maxAge", 60 * 60 * 24 * 30 * 12);
			externalContext.addResponseCookie(prop.getProperty("cookieLangName"), URLEncoder.encode(language, "UTF-8"),
					propertiesCookie);

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	////////////////////////////////// getters and setters
	////////////////////////////////// /////////////////////////////
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUserRegister() {
		return userRegister;
	}

	public void setUserRegister(User userRegister) {
		this.userRegister = userRegister;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Map<String, String> getAvailableItems() {
		return availableItems;
	}

	public void setAvailableItems(Map<String, String> availableItems) {
		this.availableItems = availableItems;
	}

	public ResourceBundle getMsgResourceBundle() {
		return msgResourceBundle;
	}

	public void setMsgResourceBundle(ResourceBundle msgResourceBundle) {
		this.msgResourceBundle = msgResourceBundle;
	}

	
	
	
	public List<User> getUsersAll() {
		if (user.getPrivilege() < 2)
			usersAll = UserDAO.getAll();
		else {
			usersAll = new ArrayList<User>();
			usersAll.add(user);
		}
		return usersAll;
	}

	public void setUsersAll(List<User> usersAll) {
		this.usersAll = usersAll;
	}

	/**
	 * Generates activation link with uuid
	 * 
	 * @param uuid
	 * @return
	 */
	public String generateActivationLink(String uuid) {
		StringBuffer retVal = new StringBuffer();
//		retVal.append("http://localhost:8080/OKStore/activate.xhtml");
		retVal.append("https://localhost:8443/OKStore/activate.xhtml");
		retVal.append("?");
		retVal.append("key");
		retVal.append("=");
		retVal.append(uuid);
		return retVal.toString();
	}

	/**
	 * Sends mail with activation link.
	 * 
	 * @param uuid
	 */
	public void sendMailWithActivationLink(String uuid, String password, String mailTo) {
		ResourceBundle bundle = ResourceBundle.getBundle("resources.lang", locale);
		String body = "";
		MessageFormat mf = null;
		if (password != null) {
			mf = new MessageFormat(bundle.getString("mailActivationBodyWithPassword"));
			body = mf.format(new Object[] { userRegister.getUsername(), generateActivationLink(uuid), password });
		} else {
			mf = new MessageFormat(bundle.getString("mailActivationBody"));
			body = mf.format(new Object[] { userRegister.getUsername(), generateActivationLink(uuid) });
		}

		// mailer.sendMail(prop.getProperty("mail.from"), "ogi@mailnesia.com",
		// bundle.getString("mailActivationSubject"), body);
		Mailer.send(prop.getProperty("mail.from"), prop.getProperty("mail.password"),
				mailTo != null ? mailTo : prop.getProperty("mail.to"),
				// prop.getProperty("mail.to"),
				bundle.getString("mailActivationSubject"), body, prop);

	}

	public void sendMailWithActivationLink(String uuid, String mailTo) {
		sendMailWithActivationLink(uuid, null, mailTo);
	}

	/**
	 * Add user with admin privileges.
	 * 
	 * @return
	 */
	public String addUser() {
		int userId = 0;
		String password = "";

		if (addUserValidation(userRegister, false)) {
			// if user does NOT exist, start registration id. insert new user
			// into db
			if (UserDAO.getByUsername(userRegister.getUsername()) == null) {
				userRegister.setSalt(UtilOKSecure.generateSalt(Integer.parseInt(prop.getProperty("saltLength"))));
				try {
					password = UtilOKSecure
							.generatePassword(Integer.parseInt(prop.getProperty("autoGeneratedPasswordLength")));
					userRegister.setPassword(UtilOKSecure.transformStringPasswordToByteArray(password,
							userRegister.getSalt(), prop.getProperty("hashAlgorithm"),
							Integer.parseInt(prop.getProperty("hashRepeatNum"))));
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if ((userId = UserDAO.insert(userRegister)) > 0) {
					UUID uuid = UUID.randomUUID();
					UserActivation userActivation = new UserActivation();
					userActivation.setKey(uuid.toString());
					userActivation.setUserId(userId);

					// after succesful insert send mail
					if (UserActivationDAO.insert(userActivation) > 0)
						sendMailWithActivationLink(uuid.toString(), password, userRegister.getEmail());

					// reset object for user registration
					userRegister = new User();

					UtilOKJSF.jsfMessage("formAddUser", "addUserSuccess");
					System.out.println("Register user insert success");

					return "manageUsers?faces-redirect=true";
				} else {
					UtilOKJSF.jsfMessage("formAddUser", "errorAddUsserUnsuccessful");
					System.out.println("Add User: Insert FAIL");
				}
			} else {
				UtilOKJSF.jsfMessage("formAddUser", "errorAddUsserUnsuccessful");
				System.out.println("Add User: user already exists");
			}
		} else 
			UtilOKJSF.jsfMessage("formAddUser", "errorAddUsserUnsuccessful");

		return null;
	}

	public String displayAddUser() {
		userRegister = new User();
		return "addUser?faces-redirect=true";
	}

	/**
	 * Validates <code>userToAdd</code>.
	 * 
	 * @param userToAdd
	 *            is validated.
	 * @param validatePassword
	 *            if true validates <code>userToAdd</code> password field.
	 * @return
	 */
	public boolean addUserValidation(User userToAdd, Boolean validatePassword) {
		if (userToAdd == null)
			return false;
		if (!UtilOKValidator.isValidStringInput("email", userToAdd.getEmail(), 1, 254, 1000))
			return false;
		if (!UtilOKValidator.isValidStringInput("socialNo", userToAdd.getSocialNo(), 1, 45, 1000))
			return false;
		if (!UtilOKValidator.isValidStringInput("username", userToAdd.getUsername(), 1, 45, 1000))
			return false;
		if (validatePassword && !UtilOKValidator.isValidStringInput("password", userToAdd.getPassword(), 1, 512, 1000))
			return false;
		if (!UtilOKValidator.isValidStringInput("firstName", userToAdd.getFirstName(), 0, 100, 1000))
			return false;
		if (!UtilOKValidator.isValidStringInput("lastName", userToAdd.getLastName(), 0, 100, 1000))
			return false;
		if (!UtilOKValidator.isValidDate(userToAdd.getBirthDate()))
			return false;
		if (user != null && userToAdd.getPrivilege() != null && userToAdd.getPrivilege() > 0
				&& userToAdd.getPrivilege() < 1000 && user.getPrivilege() > userToAdd.getPrivilege())
			return false;

		return true;
	}

}

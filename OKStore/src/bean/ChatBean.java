package bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import dao.MessageDAO;
import dao.UserDAO;
import model.MessageUser;
import model.User;
import utilok.UtilOKJSF;

@Named
@ViewScoped
public class ChatBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	@ManagedProperty(value = "#{userBean}")
	UserBean userBean;

	@Inject
	@Push
	private PushContext chatChannel;

	@Inject
	@Push
	PushContext chatToggleChannel;
	
	@Inject
	@Push
	PushContext topChatChannel;
	
	
	List<User> chatUsers;
	List<MessageUser> chatMessages;
	String chatMessageToSend;
	User currentReceiver;
	private Integer enabled;
	// new chat messages count
	Integer unreadChatCount;
	Map<String, Integer> unreadChatCountPerUserMap;

	
	@PostConstruct
	public void init() {
		chatUsers = UserDAO.getAllChatUsersByUserIdActive(userBean.user.getId(), true);
		enabled = userBean.getUser().getChatStatus();
		unreadChatCount = MessageDAO.getMessageCountByReceiverIdReadDirectActive(userBean.getUser().getId(), 0, 1, 1);
		getUnreadChatMessagesPerUser();
	}

	// after send chat message button is pressed
	public String chatMessageSend() {
		System.out.println("Chat message to send: " + chatMessageToSend);

		if (chatMessageToSend != null && !chatMessageToSend.isEmpty()) {
			MessageUser messageUser = new MessageUser();
			messageUser.setSenderId(userBean.getUser().getId());
			messageUser.setReceiverId(currentReceiver.getId());
			messageUser.setContent(chatMessageToSend);
			messageUser.setDirect(1);
			messageUser.setSenderName(userBean.getUser().getUsername());
			if (MessageDAO.insert(messageUser) > 0) {
				// clear the chat message input field
				chatMessageToSend = "";
				chatMessages.add(messageUser);
				
//				chatChannel.send("ajaxEvent", Arrays.asList(currentReceiver.getUsername()));
				chatChannel.send("ajaxEvent");
				topChatChannel.send("ajaxTopChatEvent");


			} else
				UtilOKJSF.jsfMessage("chatMessageForm:chatMessage", "errorChatMessageSend");
		}
		return null;
	}

	public void ajaxPushed(AjaxBehaviorEvent e) {
		if (currentReceiver != null) { // not selected chat user
			chatMessages = MessageDAO.getAllBySenderIdReceiverIdDirectActiveCustom(userBean.user.getId(), currentReceiver.getId(), 1, 1);

			// when click on sender all new chat messages mark as read
			MessageDAO.updateReadBySenderIdReadDirectActive(1, currentReceiver.getId(), 0, 1, 1);
		}
		getUnreadChatMessagesPerUser();
	}

	public void ajaxTopChatPushed(AjaxBehaviorEvent e) {
		unreadChatCount = MessageDAO.getMessageCountByReceiverIdReadDirectActive(userBean.getUser().getId(), 0, 1, 1);
	}
	
	// after user for chat has been selected this method is called
	public String getChatMessagesFor(Integer userId) {
		// when click on sender all new chat messages mark as read
		MessageDAO.updateReadBySenderIdReadDirectActive(1, userId, 0, 1, 1);
		getUnreadChatMessagesPerUser();
				
		currentReceiver = UserDAO.getById(userId);
		chatMessages = MessageDAO.getAllBySenderIdReceiverIdDirectActiveCustom(userBean.user.getId(), currentReceiver.getId(), 1, 1);
		
		unreadChatCount = MessageDAO.getMessageCountByReceiverIdReadDirectActive(userBean.getUser().getId(), 0, 1, 1);

		topChatChannel.send("ajaxTopChatEvent");
		
	
		return null;
	}

	public void toggleAjaxPushed(AjaxBehaviorEvent e) {
		chatUsers = UserDAO.getAllChatUsersByUserIdActive(userBean.user.getId(), true);
		enabled = UserDAO.getById(userBean.getUser().getId()).getChatStatus();
		userBean.getUser().setChatStatus(enabled);
	}
	
	public void toggle() {
    	enabled = (userBean.getUser().getChatStatus() + 1) % 2;
        userBean.getUser().setChatStatus(enabled);
        UserDAO.updateChatStatus(userBean.getUser().getId(), enabled);
        chatToggleChannel.send("toggleAjaxEvent");
    }
    
    public Integer countUnreadBySender(String senderUsername) {
    	return unreadChatCountPerUserMap.get(senderUsername);
    }
    
    public void getUnreadChatMessagesPerUser() {
    	unreadChatCountPerUserMap = new HashMap<String, Integer>();
		for(User user : chatUsers) {
			unreadChatCountPerUserMap.put(
					user.getUsername(), 
					MessageDAO.getMessageCountByReceiverIdSenderIdReadDirectActive(userBean.getUser().getId(), user.getId(), 0, 1, 1));
			
		}
    }
    
	/////// getters and setters //////
	public List<User> getChatUsers() {
		return chatUsers;
	}

	public void setChatUsers(List<User> chatUsers) {
		this.chatUsers = chatUsers;
	}

	public List<MessageUser> getChatMessages() {
		return chatMessages;
	}

	public void setChatMessages(List<MessageUser> chatMessages) {
		this.chatMessages = chatMessages;
	}

	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public String getChatMessageToSend() {
		return chatMessageToSend;
	}

	public void setChatMessageToSend(String chatMessageToSend) {
		this.chatMessageToSend = chatMessageToSend;
	}

	public User getCurrentReceiver() {
		return currentReceiver;
	}

	public void setCurrentReceiver(User currentReceiver) {
		this.currentReceiver = currentReceiver;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public Integer getUnreadChatCount() {
		return unreadChatCount;
	}

	public void setUnreadChatCount(Integer unreadChatCount) {
		this.unreadChatCount = unreadChatCount;
	}

}

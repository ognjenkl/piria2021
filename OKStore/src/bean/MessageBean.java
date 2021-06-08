package bean;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.application.FacesMessage;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import dao.ArticleDAO;
import dao.MessageDAO;
import dao.UserDAO;
import model.ArticleUserHasArticle;
import model.Message;
import model.MessageUser;
import utilok.UtilOKJSF;

@Named
@ViewScoped
public class MessageBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	@ManagedProperty(value = "#{userBean}")
	UserBean userBean;
	
	@Inject
	@Push
	private PushContext topMessageChannel;

	List<MessageUser> indirectMessages;
	// new messages count
	Integer unreadMessagesCount;

	String messageToSend;
	
	@PostConstruct
	public void init() {
		indirectMessages = MessageDAO.getAllByReceiverIdAndDirectAndActiveCustom(userBean.getUser().getId(), 0, 1);
		unreadMessagesCount = MessageDAO.getMessageCountByReceiverIdReadDirectActive(userBean.getUser().getId(), 0, 0, 1);

	}

	
	public String sendMessageToSeller(Integer articleId) {
		ArticleUserHasArticle articleUserHasArticle = ArticleDAO.getByArticleIdCustom(articleId);
		Message message = new Message();
		message.setSenderId(userBean.getUser().getId());
		message.setReceiverId(articleUserHasArticle.getSellerId());
		message.setArticleId(articleId);
		message.setContent(messageToSend);
		if (MessageDAO.insert(message) > 0) {
			// after message sent, clear the input field
			messageToSend = null;
		
			unreadMessagesCount = MessageDAO.getMessageCountByReceiverIdReadDirectActive(userBean.getUser().getId(), 0, 0, 1);
			
			// send websocket message
			executeTopMessageChannel();
			
		} else
	        UtilOKJSF.jsfMessage(null, FacesMessage.SEVERITY_ERROR, "errorMessageSend", null);
			return null;
		}

    public String sendMessageToBuyer(Message message) {
        try {

            Message messageToBuyer = new Message();
            messageToBuyer.setSenderId(userBean.getUser().getId());
            messageToBuyer.setReceiverId(message.getSenderId());
            messageToBuyer.setArticleId(message.getArticleId());
            messageToBuyer.setContent(messageToSend);
            if (MessageDAO.insert(messageToBuyer) > 0) {
                UtilOKJSF.jsfMessage(null, FacesMessage.SEVERITY_INFO, "successMessageSend", null);
                // after message sent, clear the input field
                messageToSend = null;
                unreadMessagesCount = MessageDAO.getMessageCountByReceiverIdReadDirectActive(userBean.getUser().getId(), 0, 0, 1);
                
                // send websocket message
                executeTopMessageChannel();

            } else
                UtilOKJSF.jsfMessage(null, FacesMessage.SEVERITY_ERROR, "errorMessageSend", null);
        } catch (Exception e) {
            UtilOKJSF.jsfMessage(null, FacesMessage.SEVERITY_ERROR, "errorMessageSend", null);
            return null;
        }

        return null;
    }
    
    public String deleteArticle(Integer messageId) {
    	Message message = MessageDAO.getById(messageId);
		try {
			if (message != null && ArticleDAO.updateActive(message.getArticleId(), false) > 0 && ArticleDAO.updateStatus(message.getArticleId(), 9) > 0) {
				UtilOKJSF.jsfMessage("messageDataTable", FacesMessage.SEVERITY_INFO, "messageDeleteArticleSuccess", null);
			} else
				UtilOKJSF.jsfMessage(null, FacesMessage.SEVERITY_ERROR, "errorMessageDeleteArticle", null);

		} catch (Exception e) {
			UtilOKJSF.jsfMessage(null, FacesMessage.SEVERITY_ERROR, "errorMessageDeleteArticle", null);
			return null;
		}

		return null;
	}
    
    public void updateUnreadMessages() {
		MessageDAO.updateReadByByReceiverIdReadDirectActive(1, userBean.getUser().getId(), 0, 0, 1);
	}
    
    public void ajaxTopMessagePushed(AjaxBehaviorEvent e) {
//		chatMessages = MessageDAO.getAllBySenderIdReceiverIdDirectActiveCustom(userBean.user.getId(), currentReceiver.getId(), 1, 1);
//		updateUnreadMessages();
    	unreadMessagesCount = MessageDAO.getMessageCountByReceiverIdReadDirectActive(userBean.getUser().getId(), 0, 0, 1);

    }

    public void onload() {
		updateUnreadMessages();
		unreadMessagesCount = MessageDAO.getMessageCountByReceiverIdReadDirectActive(userBean.getUser().getId(), 0, 0, 1);
		executeTopMessageChannel();
	}
    
    public void executeTopMessageChannel() {
        topMessageChannel.send("ajaxTopMessageEvent");    	
    }
    
    
    
    
    
	////////////// getters and setters ///////////////////
	public UserBean getUserBean() {
		return userBean;
	}

	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public List<MessageUser> getIndirectMessages() {
        return indirectMessages;
	}

	public void setIndirectMessages(List<MessageUser> indirectMessages) {
		this.indirectMessages = indirectMessages;
	}

	public Integer getUnreadMessagesCount() {
		return unreadMessagesCount;
	}

	public void setUnreadMessagesCount(Integer unreadMessagesCount) {
		this.unreadMessagesCount = unreadMessagesCount;
	}

	public String getMessageToSend() {
		return messageToSend;
	}

	public void setMessageToSend(String messageToSend) {
		this.messageToSend = messageToSend;
	}


}

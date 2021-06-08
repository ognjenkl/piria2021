package utilok;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

//import org.apache.commons.mail.DefaultAuthenticator;
//import org.apache.commons.mail.Email;
//import org.apache.commons.mail.EmailException;
//import org.apache.commons.mail.SimpleEmail;

public class Mailer {

//	Email email;

	public Mailer(String mailSmtp, int mailPort, String mailUsername, String mailPassword, boolean sslOn) {
//		email = new SimpleEmail();
//		email.setHostName(mailSmtp);
//		email.setSmtpPort(mailPort);
//		email.setAuthenticator(new DefaultAuthenticator(mailUsername, mailPassword));
//		email.setSSLOnConnect(sslOn);
	}

//	public void sendMail(String from, String to, String subject, String message) {
//		try {
//			email.setFrom(from);
//			email.setSubject(subject);
//			email.setMsg(message);
//			email.addTo(to);
//			email.send();
//		} catch (EmailException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	/**
	 * Sends mail.
	 * @param from
	 * @param password
	 * @param to
	 * @param sub
	 * @param msg
	 */
	public static void send(String from, String password, String to, String sub, String msg, Properties propConfig) {
		// Get properties object
		Properties props = new Properties();
		props.put("mail.smtp.host", propConfig.getProperty("mail.smtp"));
		props.put("mail.smtp.socketFactory.port", propConfig.getProperty("mail.socketFactoryPort"));
		props.put("mail.smtp.socketFactory.class", propConfig.getProperty("mail.socketFactoryClass"));
		props.put("mail.smtp.auth", propConfig.getProperty("mail.auth"));
		props.put("mail.smtp.port", propConfig.getProperty("mail.port"));
//		props.put("mail.smtp.host", "smtp.gmail.com");
//		props.put("mail.smtp.socketFactory.port", "465");
//		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//		props.put("mail.smtp.auth", "true");
//		props.put("mail.smtp.port", "465");
		// get Session
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		});
		// compose message
		try {
			MimeMessage message = new MimeMessage(session);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(sub);
			message.setText(msg);
			// send message
			Transport.send(message);
			System.out.println("message sent successfully");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}

	}

}

package test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import dao.ArticleDAO;
import model.Article;
import utilok.Mailer;
import utilok.UtilOKSql;

public class MainTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		List<String> list = new ArrayList<>();
//		list.add("1");
//		list.add("2");
//		list.add("3");
//		System.out.println(UtilOKSql.adjustSqlInOperatorValues("select * from nesto where s IN (plh)", list, "plh"));
//		
//		List<Article> articleList = ArticleDAO.getSuggestionForUser("s");
//		for (Article a : articleList) { 
//			System.out.println(a.getId() + " " + a.getName());
//		}
		
//		Properties prop = new Properties();
//		prop.put("mail.smtp", "smtp.gmail.com");
//		prop.put("mail.port", "465");
//		prop.put("mail.socketFactoryClass", "javax.net.ssl.SSLSocketFactory");
//		prop.put("mail.auth", "true");
//		prop.put("mail.socketFactoryPort", "465");
//		prop.put("mail.from", "okstore.piria@gmail.com");
//		prop.put("mail.password", "okstore.1");
//				
//		String mailTo = "ognjenkl1@mailinator.com";
//		
//		Mailer.send(prop.getProperty("mail.from"), prop.getProperty("mail.password"),
//				mailTo != null ? mailTo : prop.getProperty("mail.to"),
//				"test", "test body", prop);
		
		final String username = "okstore.piria@gmail.com";
        final String password = "okstore.1";

        Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS
        
        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("ognjenkl1@mailinator.com")
            );
            message.setSubject("Testing Gmail TLS");
            message.setText("Dear Mail Crawler,"
                    + "\n\n Please do not spam my email!");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        
	}

}

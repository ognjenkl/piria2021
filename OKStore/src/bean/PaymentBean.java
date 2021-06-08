package bean;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.faces.annotation.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.rpc.ServiceException;

import com.fasterxml.jackson.databind.ObjectMapper;

import dao.ArticleDAO;
import dao.UserDAO;
import model.ArticleUserHasArticle;
import model.Category;
import model.User;
import payment.OKBankBadPaddingExceptionException;
import payment.OKBankCallbackHandler;
import payment.OKBankIllegalBlockSizeExceptionException;
import payment.OKBankInvalidKeyExceptionException;
import payment.OKBankInvalidKeySpecExceptionException;
import payment.OKBankNoSuchAlgorithmExceptionException;
import payment.OKBankNoSuchPaddingExceptionException;
import payment.OKBankSignatureExceptionException;
import payment.OKBankStub;
import payment.OKBankStub.ProcessPaymentSecure;
import payment.OKBankStub.ProcessPaymentSecureResponse;
//import payment.OKBank;
//import payment.OKBankServiceLocator;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import secureLib.CipherUtil;
import secureLib.DigitalSignature;
import secureLib.SymmetricAlgorithm;
//import secureLib.CryptoImpl;
import secureUtil.MessageType;

/**
 * @author ognjen
 *
 */
@ViewScoped
@Named
public class PaymentBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final int NOT_PUBLISHED = 3;
	private static final int SELLING = 5;
	private static final int SOLD = 7;
	private static final int BLOCKED = 9;

	@Inject
	@ManagedProperty(value = "#{userBean}")
	private UserBean userBean;

	private String cardNumber;
	private String cardType;
	private String cardExpirationMonth;
	private String cardExpirationYear;
	private String cardPin;

	// to get data from params
//	ObjectMapper objectMapper;

	// article to buy
	ArticleUserHasArticle articleUserHasArticleToBuy;

	// articleId from params
	Integer articleId;

	@PostConstruct
	public void init() {
//		objectMapper = new ObjectMapper();
		articleId = Integer.valueOf(
				FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("articleIdName"));
		if (articleId != null)
			articleUserHasArticleToBuy = ArticleDAO.getByArticleIdCustom(articleId);
	}

	/**
	 * Makes a payment.
	 * 
	 * @return
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws InvalidKeySpecException 
	 * @throws SignatureException 
	 * @throws OKBankIllegalBlockSizeExceptionException 
	 * @throws OKBankInvalidKeySpecExceptionException 
	 * @throws OKBankNoSuchPaddingExceptionException 
	 * @throws OKBankInvalidKeyExceptionException 
	 * @throws OKBankBadPaddingExceptionException 
	 * @throws OKBankNoSuchAlgorithmExceptionException 
	 * @throws OKBankSignatureExceptionException 
	 */
	public String buyArticle() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, SignatureException, OKBankSignatureExceptionException, OKBankNoSuchAlgorithmExceptionException, OKBankBadPaddingExceptionException, OKBankInvalidKeyExceptionException, OKBankNoSuchPaddingExceptionException, OKBankInvalidKeySpecExceptionException, OKBankIllegalBlockSizeExceptionException {
		Integer response = null;
		try {
//			OKBank okBank = new OKBankServiceLocator().getOKBank();
			
//			response = okBank.processPayment("111", "VISA", "11", "22", "1111", "1", "2", new BigDecimal(100)));
//			response = okBank.processPayment("222", "MASTERCARD", "11", "22", "2222", "2", "1", new BigDecimal(100));			
//			response = okBank.processPayment(cardNumber, cardType, cardExpirationMonth, cardExpirationYear, cardPin, "2", "1", new BigDecimal(100));			
			User seller = UserDAO.getById(articleUserHasArticleToBuy.getSellerId());
			
			// payment
			
			// payment message map properties
			Map<String, Object> paymentMap = new HashMap<>();
			paymentMap.put("cardNumber", cardNumber);
			paymentMap.put("cardType", cardType);
			paymentMap.put("cardExpirationMonth", cardExpirationMonth);
			paymentMap.put("cardExpirationYear", cardExpirationYear);
			paymentMap.put("cardPin", cardPin);
			paymentMap.put("buyerSocialNo", userBean.getUser().getSocialNo());
			paymentMap.put("sellerSocialNo", seller.getSocialNo());
			paymentMap.put("price", articleUserHasArticleToBuy.getPrice());

			// map to json
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonPaymentMessage = objectMapper.writeValueAsString(paymentMap);
			
			// ne radi JSONObject put sa aplikacijom, na pay ne prikaze aritkl i baca exception
//			JSONObject jsonPaymentMessage = new JSONObject();
//				try {
//					jsonPaymentMessage.put("cardNumber", cardNumber.toString());
//				} catch (JSONException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				jsonPaymentMessage.put("cardType", cardType);
//				jsonPaymentMessage.put("cardExpirationMonth", cardExpirationMonth);
//				jsonPaymentMessage.put("cardExpirationYear", cardExpirationYear);
//				jsonPaymentMessage.put("cardPin", cardPin);
//				jsonPaymentMessage.put("buyerSocialNo", userBean.getUser().getSocialNo());
//				jsonPaymentMessage.put("sellerSocialNo", seller.getSocialNo());
//				jsonPaymentMessage.put("price", articleUserHasArticleToBuy.getPrice());
//			
			// digest
//			String hashFunction = "SHA512";
//			byte[] digest = CryptoImpl.hash(hashFunction, jsonPaymentMessage.toString().getBytes(StandardCharsets.UTF_8));
			CipherUtil cipherUtil = new CipherUtil();
//			String hash = cipherUtil.hash(jsonPaymentMessage);
			
			// private key from file
			String pkiPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/WEB-INF/pki");
			File pkiDir = new File(pkiPath);
			String privateKeyFilePath = pkiDir + "/" + userBean.getUser().getSocialNo() + "2048.key";
//			String privateKeyPairFilePath = pkiDir + "/" + userBean.getUser().getSocialNo() + "2048.key";
//			File privateKeyPairFile = new File(privateKeyPairFilePath);
//			KeyPair privateKeyPair = null;
//			if(privateKeyPairFile.exists())
//					privateKeyPair = CryptoImpl.getKeyPair(privateKeyPairFile);
//			else
//				System.out.println("There is no key pair file at: " + privateKeyPairFilePath);
			PrivateKey privateKey = cipherUtil.getPlainPrivateKeyFromPath(privateKeyFilePath);
			
			// digital signature
//			String optModeAsymmetric = "RSA/ECB/PKCS1Padding";
//			byte[] digitalSignatur = CryptoImpl.asymmetricEncryptDecrypt(optModeAsymmetric, privateKeyPair.getPrivate(), digest, true);
//			String digitalSignaturString = new String(Base64.getEncoder().encode(digitalSignatur), StandardCharsets.UTF_8);
//			String digitalSignaturString = cipherUtil.cipher(hash, privateKey, Cipher.ENCRYPT_MODE);
			DigitalSignature digitalSignature = new DigitalSignature();
			String digitalSignaturString = digitalSignature.sign(jsonPaymentMessage, privateKey);
			
			//envelope with payment parameters
			String bankOKPublicKeyPath = pkiDir + "/" + "bankOK2048.pub";
			PublicKey bankOKPublicKey = null;
//				bankOKPublicKey = CryptoImpl.getPublicKey(bankOKPublicKeyPath);
			bankOKPublicKey = cipherUtil.getPlainPublicKeyFromPath(bankOKPublicKeyPath);

//			byte[] jsonPaymentMessageEncoded = Base64.getEncoder().encode(jsonPaymentMessage.toString().getBytes(StandardCharsets.UTF_8));
//			String jsonPaymentMessageString = new String(jsonPaymentMessageEncoded, StandardCharsets.UTF_8);
			SymmetricAlgorithm symmetricAlgorithm = new SymmetricAlgorithm();
			String secret = "password";
			String jsonPaymentMessageCipher = symmetricAlgorithm.encrypt(jsonPaymentMessage, secret);
			
//			byte[] envelope = CryptoImpl.asymmetricEncryptDecrypt(optModeAsymmetric, bankOKPublicKey, jsonPaymentMessageEncoded, true);
//			String envelopeEncoded = new String(Base64.getEncoder().encode(envelope), StandardCharsets.UTF_8);
			String envelopeEncoded = cipherUtil.encrypt(secret, bankOKPublicKey);
					
			// final payment message to transfer to bank ws
			Map<String, Object> messageMap = new HashMap<String, Object>();
			messageMap.put(MessageType.CIPHER, jsonPaymentMessageCipher);
			messageMap.put(MessageType.ENVELOPE, envelopeEncoded);
			messageMap.put(MessageType.DIGSIG, digitalSignaturString);
			String jsonMessage = objectMapper.writeValueAsString(messageMap);
			
			System.out.println(jsonMessage);
			
			// secure payment process request
//			String END_POINT = "http://localhost:8080/OKBankSoap25/services/OKBank";
//			OKBankStub stub = new OKBankStub(END_POINT);
			
			OKBankStub stub = new OKBankStub();
			ProcessPaymentSecure processPaymentSecure = new OKBankStub.ProcessPaymentSecure();
			processPaymentSecure.setMessage(jsonMessage.toString());
			ProcessPaymentSecureResponse processPaymentSecureResponse = stub.processPaymentSecure(processPaymentSecure);
//			response = okBank.processPaymentSecure(jsonMessage.toString());
//			System.out.println("OKBank paymnet response: " + response);
			System.out.println("OKBank paymnet response: " + processPaymentSecureResponse.get_return());
			System.out.println("OKBank paymnet response object: " + processPaymentSecureResponse);
			response = processPaymentSecureResponse.get_return();
			
			// payment response
			if (response != null && response == 0) {
				if (ArticleDAO.updateBuyerIdAndStatus(articleUserHasArticleToBuy.getId(), userBean.getUser().getId(), SOLD) > 0) {

					// for suggestion if redis server is up
					try (Jedis jedis = new Jedis()) {
						for (Category category : articleUserHasArticleToBuy.getCategoryList()) {
							jedis.zadd("buy." + userBean.getUser().getUsername(),
									jedis.zincrby("buy." + userBean.getUser().getUsername(),
											articleUserHasArticleToBuy.getPrice().doubleValue(),
											category.getId().toString()),
									category.getId().toString());
							jedis.set("last." + userBean.getUser().getUsername(),
									articleUserHasArticleToBuy.getCategoryList().toString());
						}
						jedis.close();
					} catch (JedisConnectionException e) {
//						e.printStackTrace();
						System.err.println("No redis connection!!!");
					}
					return "buyingArticles?faces-redirect=true";
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return null;
	}

	
	
	/**
	 * @return the cardNumber
	 */
	public String getCardNumber() {
		return cardNumber;
	}

	/**
	 * @param cardNumber the cardNumber to set
	 */
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	/**
	 * @return the cardType
	 */
	public String getCardType() {
		return cardType;
	}

	/**
	 * @param cardType the cardType to set
	 */
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	/**
	 * @return the cardExpirationMonth
	 */
	public String getCardExpirationMonth() {
		return cardExpirationMonth;
	}

	/**
	 * @param cardExpirationMonth the cardExpirationMonth to set
	 */
	public void setCardExpirationMonth(String cardExpirationMonth) {
		this.cardExpirationMonth = cardExpirationMonth;
	}

	/**
	 * @return the cardExpirationYear
	 */
	public String getCardExpirationYear() {
		return cardExpirationYear;
	}

	/**
	 * @param cardExpirationYear the cardExpirationYear to set
	 */
	public void setCardExpirationYear(String cardExpirationYear) {
		this.cardExpirationYear = cardExpirationYear;
	}

	/**
	 * @return the cardPin
	 */
	public String getCardPin() {
		return cardPin;
	}

	/**
	 * @param cardPin the cardPin to set
	 */
	public void setCardPin(String cardPin) {
		this.cardPin = cardPin;
	}

	/**
	 * @return the userBean
	 */
	public UserBean getUserBean() {
		return userBean;
	}

	/**
	 * @param userBean the userBean to set
	 */
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}

	public ArticleUserHasArticle getArticleUserHasArticleToBuy() {
		return articleUserHasArticleToBuy;
	}

	public void setArticleUserHasArticleToBuy(ArticleUserHasArticle articleUserHasArticleToBuy) {
		this.articleUserHasArticleToBuy = articleUserHasArticleToBuy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
//	@Override
//	public String toString() {
//		return "PaymentBean [cardNumber=" + cardNumber + ", cardType=" + cardType + ", cardExpirationYear="
//				+ cardExpirationYear + ", cardExpirationMonth=" + cardExpirationMonth + ", cardPin=" + cardPin + "]";
//	}
//	 @Override
//	    public String toString() {
//	        ObjectMapper objectMapper = new ObjectMapper();
//	        try {
//	            return objectMapper.writeValueAsString(this);
//	        } catch (JsonProcessingException e) {
//	            e.printStackTrace();
//	            return null;
//	        }
//	    }

}

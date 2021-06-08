package payment;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.faces.context.FacesContext;
import javax.jms.ObjectMessage;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

//import org.bouncycastle.util.Arrays;
import org.json.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dao.AccountDAO;
import model.Account;
import secureLib.CipherUtil;
import secureLib.DigitalSignature;
import secureLib.SymmetricAlgorithm;
import secureUtil.MessageType;

/**
 * 
 * @author ognjen
 *
 */
@WebService(targetNamespace = "http://payment/", portName = "OKBankPort", serviceName = "OKBankService")
public class OKBank {

	@Resource
	private WebServiceContext context;

	/**
	 * 
	 * @param cardNumber
	 * @param cardType
	 * @param cardExpirationMonth
	 * @param cardExpirationYear
	 * @param cardPin
	 * @param buyerSocialNo
	 * @param sellerSocialNo
	 * @param amount
	 * @return 0 - success, 1 - test, 2 - something's wrong, 3 - exception emerged,
	 *         4 - account does not exist, 5 - not enough money, 6 - crediting
	 *         account unsuccessful, 7 - digital signature verification
	 */
	private Integer processPayment(String cardNumber, String cardType, String cardExpirationMonth,
			String cardExpirationYear, String cardPin, String buyerSocialNo, String sellerSocialNo, BigDecimal amount) {

		String expirationDate = cardExpirationYear + cardExpirationMonth;

		Account sellerAcc = AccountDAO.getBySocialNo(sellerSocialNo);
		if (sellerAcc == null)
			return 4;

		Account buyerAcc = AccountDAO.getByCardParams(cardNumber, cardType, expirationDate, cardPin);
		if (buyerAcc == null)
			return 4;

		if (buyerAcc.getAmount().compareTo(amount) < 0)
			return 5;

		Integer paymentResponse = AccountDAO.payment(buyerAcc.getId(), sellerAcc.getId(), amount);
		if (paymentResponse != null && paymentResponse > 0)
			return 0;
		else
			return 6;

	}

	/**
	 * Process payment secure wrapper.
	 * 
	 * @param message
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws NoSuchPaddingException
	 */
	public Integer processPaymentSecure(String message)
			throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		try {
			System.out.println("OKBank got message: ");
			System.out.println(message);

			// payment request message to map
			ObjectMapper objectMapper = new ObjectMapper();
			TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
			};
			Map<String, String> messageMap = objectMapper.readValue(message, typeRef);

			// pki directory on filesystem
//			String pkiPath = "";
//			String pkiPath = "pki";
//			String pkiPath = "..\\standalone\\deployments\\OKBankSoapWSEAR.ear\\OKBankSoapWS.war\\WEB-INF\\pki";
			File pkiDir = new File("");

			String rootDirPath = pkiDir.getAbsolutePath();
			rootDirPath = rootDirPath.substring(0, rootDirPath.length() - 3);

//            System.out.println("P1 " + pkiDir.getAbsoluteFile());
//            System.out.println("p2 " + pkiDir.getAbsolutePath());
//            System.out.println("p3 " + pkiDir.getPath());
//            String currentDirectory=System.getProperty("user.dir");
//            System.out.println("p4 " + currentDirectory);

//            System.out.println("p5 " + rootDirPath);

//			String pkiPath = rootDirPath + "\\WebContent\\WEB-INF\\pki";
//            String pkiPath = rootDirPath + "standalone/deployments/WebServiceProjectEAR.ear/OKBankSoapWS.war/WEB-INF/pki";
//            String pkiPath = rootDirPath + "standalone/deployments/OKBankSoapWSEAR.ear/OKBankSoapWS.war/WEB-INF/pki";
//			   String pkiPath = rootDirPath + "standalone\\deployments\\OKStore25EAR.ear\\OKBankSoap25.war\\WEB-INF\\pki";
			   String pkiPath = rootDirPath + "standalone\\deployments\\OKStoreEAR.ear\\OKBankSoapWS.war\\WEB-INF\\pki";
				
			System.out.println("p6 " + pkiPath);
//            System.out.println("p6 " + pkiDir.getAbsolutePath());

//            File pkiFile = new File(pkiPath);
//        recursiveSearch(pkiFile);

			// private key
			String bankOKPrivateKeyFilePath = pkiPath + "/" + "bankOK2048.key";
			CipherUtil cipherUtil = new CipherUtil();
			PrivateKey privateKey = cipherUtil.getPlainPrivateKeyFromPath(bankOKPrivateKeyFilePath);

			// envelope decrypt	
			String envelopeCipher = messageMap.get(MessageType.ENVELOPE);
			String symmetrichKey = cipherUtil.decrypt(envelopeCipher, privateKey);
			System.out.println("OKBank envelope decrypted: ");
			System.out.println(symmetrichKey);
			
			// cipher decrypt
			SymmetricAlgorithm symmetricAlgorithm = new SymmetricAlgorithm();
			String cipher = messageMap.get(MessageType.CIPHER);
			String paymentString = symmetricAlgorithm.decrypt(cipher, symmetrichKey); 
//					cipherUtil.decrypt(messageMap.get(MessageType.ENVELOPE), privateKey);
			System.out.println("OKBank cipher decrypted: ");
			System.out.println(paymentString);

			// payment parameters
			Map<String, String> paymentMap = objectMapper.readValue(paymentString, typeRef);
			String cardNumber = paymentMap.get("cardNumber");
			String cardType = paymentMap.get("cardType");
			String cardExpirationMonth = paymentMap.get("cardExpirationMonth");
			String cardExpirationYear = paymentMap.get("cardExpirationYear");
			String cardPin = paymentMap.get("cardPin");
			String buyerSocialNo = paymentMap.get("buyerSocialNo");
			String sellerSocialNo = paymentMap.get("sellerSocialNo");
			BigDecimal amount = new BigDecimal(paymentMap.get("price"));

			// public key
			String buyerPublicKeyPath = pkiPath + "/" + buyerSocialNo + "2048.pub";
			PublicKey buyerPublicKey = cipherUtil.getPlainPublicKeyFromPath(buyerPublicKeyPath);

			DigitalSignature digitalSignature = new DigitalSignature();
			String signature = messageMap.get(MessageType.DIGSIG);
			boolean paymentMessageVerified = digitalSignature.verifySignature(paymentString, buyerPublicKey, signature);
//					cipherUtil.digitalSignVerify(paymentString, buyerPublicKey,
//					messageMap.get(MessageType.DIGSIG));

			// provjeriti da li je poruka ispravna verifikovati
			if (paymentMessageVerified) {
				System.out.println("OKBank poruka za placanje je USPJESNO verifikovana digitalnim potpisom: ");
				System.out.println(paymentString);

				// payment processing
				int paymentResponse = processPayment(cardNumber, cardType, cardExpirationMonth, cardExpirationYear,
						cardPin, buyerSocialNo, sellerSocialNo, amount);

				System.out.println("Payment response: ");
				System.out.println(paymentResponse);
				return paymentResponse;
			} else
				System.out.println("OKBank poruka za placanje NIJE verifikovana");

			return 7;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 2;
	}

	private void recursiveSearch(File file) {
		File[] filesList = file.listFiles();
		for (File f : filesList) {
			if (f.isDirectory() && !f.isHidden()) {
				System.out.println("Directoy name is  -------------->" + f.getName());
				recursiveSearch(f);
			}
			if (f.isFile()) {
				System.out.println("File name is  -------------->" + f.getName());
			}
		}
	}

//	public static void main(String[] args) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException,
//			InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
//		OKBank okBank = new OKBank();
//		// {"envelope":"fOFsqsXPjCD2PeWH015NKGNw1IExPGqhLFFxL0i9y58sCtkXRox`/BLeAJqxb5beq6thS0J4EFrnMHKO3K5qkjm+oCoCz0fHwA5FDSZsjHmsxIDrgMb4hUUI3FY7ajtpTvhkBUkGEuItKpW8QjhbVJlvOcgktRfih8Zg1IGP5n+Zff2pPWCPA0WG7rfaL3zHzM4l848WoJDxg2Y02LYgCPVXToid/9vRsGznPHe5ZX/RI3dsF1JdnItBkV8AKC7BQKtIx8QphwbgXDZeT4t1efiBBi3wsbgtZ4YdUxliFWert3YSgP3lVNt5A/Qh0bWP1ah8gu3Z6CRRKIs4B8xCnH7Q==","digsig":"LK6V0xUCuhb92wRNknsDk+/ocVtkXlvyRu0JEOqcX0BQdxyU/Oe2yKB7I7BqIQBdaqMeqyvW8jCOZlBAd4avDSP4cLIR4+U1Z82xkgE95WAvn5/9cepBv9ayu8LXqIcmTEAVexdEqeQlhdiu4aBoLB9LeBI9bSsFZIuLMGS1/s7UCGlBT4WhsU3RsyIs0NnJqEeF+G+3dTlIjyxF2jBqLTrlWbElkRQekFDFFapO3rk5/5jOTZuCTPSuQRHJ9KVQQ5qHXhqz3tLdHTWk3pN3TAFASyGNDBxIdvGCjIYw13WM+IdZ+IAsJPzNDFDmpH2BNSX8hwbQQ+w+4ljscreb3A=="}
//		okBank.processPaymentSecure(
////				"{\"envelope\":\"fOFsqsXPjCD2PeWH015NKGNw1IExPGqhLFFxL0i9y58sCtkXRo/BLeAJqxb5beq6thS0J4EFrnMHKO3K5qkjm+oCoCz0fHwA5FDSZsjHmsxIDrgMb4hUUI3FY7ajtpTvhkBUkGEuItKpW8QjhbVJlvOcgktRfih8Zg1IGP5n+Zff2pPWCPA0WG7rfaL3zHzM4l848WoJDxg2Y02LYgCPVXToid/9vRsGznPHe5ZX/RI3dsF1JdnItBkV8AKC7BQKtIx8QphwbgXDZeT4t1efiBBi3wsbgtZ4YdUxliFWert3YSgP3lVNt5A/Qh0bWP1ah8gu3Z6CRRKIs4B8xCnH7Q==\",\"digsig\":\"LK6V0xUCuhb92wRNknsDk+/ocVtkXlvyRu0JEOqcX0BQdxyU/Oe2yKB7I7BqIQBdaqMeqyvW8jCOZlBAd4avDSP4cLIR4+U1Z82xkgE95WAvn5/9cepBv9ayu8LXqIcmTEAVexdEqeQlhdiu4aBoLB9LeBI9bSsFZIuLMGS1/s7UCGlBT4WhsU3RsyIs0NnJqEeF+G+3dTlIjyxF2jBqLTrlWbElkRQekFDFFapO3rk5/5jOTZuCTPSuQRHJ9KVQQ5qHXhqz3tLdHTWk3pN3TAFASyGNDBxIdvGCjIYw13WM+IdZ+IAsJPzNDFDmpH2BNSX8hwbQQ+w+4ljscreb3A==\"}"
////				"{\"envelope\":\"fOFsqsXPjCD2PeWH015NKGNw1IExPGqhLFFxL0i9y58sCtkXRo/BLeAJqxb5beq6thS0J4EFrnMHKO3K5qkjm+oCoCz0fHwA5FDSZsjHmsxIDrgMb4hUUI3FY7ajtpTvhkBUkGEuItKpW8QjhbVJlvOcgktRfih8Zg1IGP5n+Zff2pPWCPA0WG7rfaL3zHzM4l848WoJDxg2Y02LYgCPVXToid/9vRsGznPHe5ZX/RI3dsF1JdnItBkV8AKC7BQKtIx8QphwbgXDZeT4t1efiBBi3wsbgtZ4YdUxliFWert3YSgP3lVNt5A/Qh0bWP1ah8gu3Z6CRRKIs4B8xCnH7Q==\",\"digsig\":\"LK6V0xUCuhb92wRNknsDk+/ocVtkXlvyRu0JEOqcX0BQdxyU/Oe2yKB7I7BqIQBdaqMeqyvW8jCOZlBAd4avDSP4cLIR4+U1Z82xkgE95WAvn5/9cepBv9ayu8LXqIcmTEAVexdEqeQlhdiu4aBoLB9LeBI9bSsFZIuLMGS1/s7UCGlBT4WhsU3RsyIs0NnJqEeF+G+3dTlIjyxF2jBqLTrlWbElkRQekFDFFapO3rk5/5jOTZuCTPSuQRHJ9KVQQ5qHXhqz3tLdHTWk3pN3TAFASyGNDBxIdvGCjIYw13WM+IdZ+IAsJPzNDFDmpH2BNSX8hwbQQ+w+4ljscreb3A==\"}"
//				"{\"cipher\":\"gwgjKlASrbQTRB8KvnoYUAqGVfWy1pDl2aOaNsr1jzspO197r+gk/HWdV+NRsjuwKIRBu8hWF6rD2VG6cLLGMjiQf6ql3mKS54H20KuxTbW+XeTXUQJA6KWBlC4R9U8uMMuE9BFbJX7fHZbBABDfxGWjfMRaO54gJfZjv2clygnItyP+z63VFhRg6NvTjqBmyQJRSnsywGsQR9lwKcZGy5Wzy5UwEmGZwPP4BbL0KwQ=\",\"envelope\":\"H4Qwef7pyvCRL1yEylWyy2z0UxD3LccwI/choQZjSZXglfCA4JPW85n7NON49je9YJVJ4f2l74igaFWyd7UzV+tZ1XQY0JyRQ8qQF1JBqpLUybUjKfi2pX5nJwECph6Gpe8/WbxxSjDPUkBjwNf8kE7sqNVcRo6gGJTNUCezVJRUQUrdH6mwba968BweY8pajxeRHmaVYnBufQsixRCOMxj3RBV221DoQcqyVx7SM5KCYFF4hKczoCOw9r5fynAfsf4BHM/wFdTGN+xnE4+7haNGqWyKX4hzK6pXQtm+Uyyk8RSRlqiUnR6u6EZZk6rfZVyT3k2+sU5KH6tFtK44NQ==\",\"digsig\":\"W/nQvAAMn0eJeFcOG1xhplgakH9qr9GY70z1fQHreT462xm4As55iWDj1UjqPTFowSbjxlH799kIm5uxA5GdbzLrBP3xZqS8RF66qU6LxkklNkdhN9cGJpl83tASEQH386+CZ4q1VDlPIysFIOb6O9sBVFKO/oqlDeXT1vcachY7WBZUW/Pk9Ej0Rc3Rfd/4nZagRe8oQVp713h7fN5ju/HQEThYX4VStgEd9JitI+1X6MJc7zO4cmtgp0BzoV5NU4mRH1aCfPrKkMzWIjB2YjUdufYDjazDUTtL/J4X86EjtQ0X/1JPw5zDqpSm4ZXfeOY0NUFffL5nHfE7GXiVjw==\"}"
//				
//				);
//	}
//
//	public String check() {
//		return "OK check";
//	}
}

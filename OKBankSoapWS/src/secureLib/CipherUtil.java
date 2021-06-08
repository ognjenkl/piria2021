package secureLib;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import secureUtil.MessageType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Collectors;

public class CipherUtil {

	private static KeyPairGenerator keyPairGenerator = null;
	@SuppressWarnings("FieldCanBeLocal")
	private final Integer KEY_SIZE = 2048;
	private final String hashAlgorithm = "SHA-512";
	private final String asymmetricAlgorithm = "RSA";

	public CipherUtil() throws NoSuchAlgorithmException {
		if (keyPairGenerator == null)
			keyPairGenerator = KeyPairGenerator.getInstance(asymmetricAlgorithm);
		keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());
	}

	public KeyPair generateKeyPair() {
		return keyPairGenerator.genKeyPair();
	}

	public String encrypt(String content, Key publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] contentBytes = content.getBytes();
		Cipher cipher = Cipher.getInstance(asymmetricAlgorithm);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] cipherContent = cipher.doFinal(contentBytes);
		return Base64.getEncoder().encodeToString(cipherContent);
	}

	public String decrypt(String cipherContent, Key privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(asymmetricAlgorithm);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] cipherContentBytes = Base64.getDecoder().decode(cipherContent.getBytes());
		byte[] decryptedContent = cipher.doFinal(cipherContentBytes);
		String decrypted = new String(decryptedContent);
		return decrypted;
	}
	
	public String cipher(String cipherContent, Key key, int mode) 
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(asymmetricAlgorithm);
		cipher.init(mode, key);
		
		byte[] cipherContentBytes = null;
		if (mode == Cipher.ENCRYPT_MODE)
			cipherContentBytes = Base64.getEncoder().encode(cipherContent.getBytes(StandardCharsets.UTF_8));
		else
			cipherContentBytes = Base64.getDecoder().decode(cipherContent);
		byte[] base64Content = cipher.doFinal(cipherContentBytes);
		String ciphered = new String(base64Content);
		return ciphered;
	}
	
	public String digitalSign(String plainText, Key key, String digitalSignature) 
			throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, 
			IllegalBlockSizeException, BadPaddingException {
		MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
        byte[] digest = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
        String encrypted = cipher(digitalSignature, key, Cipher.ENCRYPT_MODE);
		return encrypted;
	}

	public String hash(String text) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
        byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));
        return new String(digest, StandardCharsets.UTF_8);
	}
	public boolean digitalSignVerify(String plainText, Key key, String digitalSignature) 
			throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, 
			IllegalBlockSizeException, BadPaddingException {
        String hash = hash(plainText);
        String decrypted = cipher(digitalSignature, key, Cipher.DECRYPT_MODE);
		boolean verified = decrypted.equals(hash);
		return verified;
	}

	public String encodeKey(Key key) {
		byte[] keyBytes = key.getEncoded();
		return Base64.getEncoder().encodeToString(keyBytes);
	}

	public PublicKey decodePublicKey(String keyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Base64.getDecoder().decode(keyStr);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(asymmetricAlgorithm);
		return keyFactory.generatePublic(spec);
	}

	public PublicKey getPlainPublicKeyFromPath(String path)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		String pubKeyfileContent = new String(Files.readAllBytes(Paths.get(path)));
		PublicKey publicKey = getPlainPublicKey(pubKeyfileContent);
		return publicKey;
	}

	public PublicKey getPlainPublicKey(String keyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
		String key = keyStr.replaceAll("-----BEGIN (.*)-----", "").replaceAll("-----END (.*)----", "")
				.replaceAll("\r\n", "").replaceAll("\n", "").trim();
		byte[] keyBytes = Base64.getDecoder().decode(key);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(asymmetricAlgorithm);
		return keyFactory.generatePublic(spec);
	}

	public PrivateKey decodePrivateKey(String keyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = Base64.getDecoder().decode(keyStr);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(asymmetricAlgorithm);
		return keyFactory.generatePrivate(keySpec);
	}

	public PrivateKey getPlainPrivateKeyFromPath(String path)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		String fileContent = new String(Files.readAllBytes(Paths.get(path)));
		PrivateKey privateKey = getPlainPrivateKey(fileContent);
		return privateKey;
	}

	public PrivateKey getPlainPrivateKey(String keyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
		String key = keyStr.replaceAll("-----BEGIN (.*)-----", "").replaceAll("-----END (.*)----", "")
				.replaceAll("\r\n", "").replaceAll("\n", "").trim();

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key));

		KeyFactory keyFactory = KeyFactory.getInstance(asymmetricAlgorithm);
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

		return privateKey;
	}

}
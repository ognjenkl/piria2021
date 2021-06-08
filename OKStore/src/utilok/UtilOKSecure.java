package utilok;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class UtilOKSecure {

	/**
	 * Generates salt Base64 string <code>saltLength</code> length.
	 * 
	 * @param saltLength
	 * @return
	 */
	public static String generateSalt(int saltLength) {
		SecureRandom secureRandom = new SecureRandom();
		byte[] salt = new byte[saltLength];
		secureRandom.nextBytes(salt);
		return new String(Base64.getEncoder().encode(salt), StandardCharsets.UTF_8);
	}

	/**
	 * Transforms password to password with salt in Base64 string to store in
	 * database based on hash algorithm.
	 * 
	 * @param password
	 * @param salt
	 * @param hashAlgorithm
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String transformStringPasswordToByteArray(String password, String salt, String hashAlgorithm,
			int hashRepeatNum) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance(hashAlgorithm);
		for (int i = 0; i < hashRepeatNum; i++) {
			messageDigest.reset();
			messageDigest.update(Base64.getDecoder().decode(salt.getBytes(StandardCharsets.UTF_8)));
			password = new String(
					Base64.getEncoder()
							.encode(messageDigest
									.digest(Base64.getEncoder().encode(password.getBytes(StandardCharsets.UTF_8)))),
					StandardCharsets.UTF_8);
		}
		return password;
	}

	/**
	 * Verify password against password from database and salt.
	 * 
	 * @param passToVerify
	 *            password to verify
	 * @param passFromDB
	 * @param saltFromDB
	 * @param hashAlgorithm
	 * @param hashRepeatNum
	 *            number how many times hash will be applied
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static boolean verifyPassword(String passToVerify, String passFromDB, String saltFromDB,
			String hashAlgorithm, int hashRepeatNum) throws NoSuchAlgorithmException {
		return (passFromDB
				.equals(transformStringPasswordToByteArray(passToVerify, saltFromDB, hashAlgorithm, hashRepeatNum)));
	}

	public static String generatePassword(Integer passLength) {
		String lower = "abcdefghijklmnopqrstuvwxyz";
		String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String digits = "0123456789";
		String special = "!@.";
		SecureRandom secureRandom = new SecureRandom();
		StringBuilder stringBuilder = new StringBuilder();		
		StringBuilder allCharsStringBuilder = new StringBuilder();
		// create all chars array (string)
		allCharsStringBuilder.append(lower);
		allCharsStringBuilder.append(upper);
		allCharsStringBuilder.append(digits);
		allCharsStringBuilder.append(special);
		// start password building (auto-generation) 
		for (int i = 0; i < passLength; i++) {
			// random position of char that appends to the password, in all chars array
			Integer position = secureRandom.nextInt(allCharsStringBuilder.length());
			// no same chars one after another
			char nextChar = allCharsStringBuilder.charAt(position);
			if (stringBuilder.length() > 1 && stringBuilder.charAt(stringBuilder.length() - 1) == nextChar) {
				i--;
				continue;
			}
		
			// add random char to the password
			stringBuilder.append(nextChar);
		}
		return stringBuilder.toString();
	}

	public static void main(String[] args) {
		for (int i = 0; i < 30; i++) {
			System.out.println(generatePassword(15));
		}
	}
}

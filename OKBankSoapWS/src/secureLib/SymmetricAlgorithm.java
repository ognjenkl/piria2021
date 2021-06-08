package secureLib;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class SymmetricAlgorithm {

    private final String MESSAGE_DIGEST_ALGORITHM = "SHA-256";
    private final Integer KEY_LENGTH = 16;
    private final String SYMMETRIC_KEY_ALG_NAME = "AES";
    private final String CHARSET_NAME = StandardCharsets.UTF_8.toString();
    private final String SYMMETRIC_KEY_ALG_CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";

    public String encrypt(String strToEncrypt, String secret) throws NoSuchPaddingException, NoSuchAlgorithmException,
            UnsupportedEncodingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(SYMMETRIC_KEY_ALG_CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getKey(secret));
        return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(CHARSET_NAME)));
    }

    public String decrypt(String strToDecrypt, String secret) throws NoSuchPaddingException, NoSuchAlgorithmException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(SYMMETRIC_KEY_ALG_CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, getKey(secret));
        return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
    }

    private SecretKeySpec getKey(String secret) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] key = secret.getBytes(CHARSET_NAME);
        MessageDigest sha = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
        key = Arrays.copyOf(sha.digest(key), KEY_LENGTH);
        return new SecretKeySpec(key, SYMMETRIC_KEY_ALG_NAME);
    }
}
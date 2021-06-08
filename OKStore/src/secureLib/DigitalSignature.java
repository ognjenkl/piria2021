package secureLib;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class DigitalSignature {

    private final String CHARSET_NAME = StandardCharsets.UTF_8.toString();
    private final String SIGNATURE_ALGORITHM = "SHA512withRSA";

    public String sign(String text, PrivateKey key) throws NoSuchAlgorithmException, InvalidKeyException,
            SignatureException, UnsupportedEncodingException {
        byte[] data = text.getBytes(CHARSET_NAME);
        Signature signer = Signature.getInstance(SIGNATURE_ALGORITHM);
        signer.initSign(key);
        signer.update(data);
        return Base64.getEncoder().encodeToString(signer.sign());
    }

    public boolean verifySignature(String text, PublicKey key, String signature) throws InvalidKeyException,
            SignatureException, NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] data = text.getBytes(CHARSET_NAME);
        Signature signer = Signature.getInstance(SIGNATURE_ALGORITHM);
        signer.initVerify(key);
        signer.update(data);
        return signer.verify(Base64.getDecoder().decode(signature));
    }
}

package com.qkl.edu_system.util;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SignatureException;
import java.util.Base64;

@Component
public class PptAuthAlgorithm {

    private static final char[] MD5_TABLE = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public String getSignature(String appId, String secret, long timestamp) throws SignatureException {
        String auth = md5(appId + timestamp);
        return hmacSHA1Encrypt(auth, secret);
    }

    private String hmacSHA1Encrypt(String encryptText, String encryptKey) throws SignatureException {
        try {
            byte[] data = encryptKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(data, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKey);
            byte[] text = encryptText.getBytes(StandardCharsets.UTF_8);
            byte[] rawHmac = mac.doFinal(text);
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new SignatureException(e.getMessage());
        }
    }

    private String md5(String cipherText) throws SignatureException {
        try {
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(cipherText.getBytes(StandardCharsets.UTF_8));
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (byte byte0 : md) {
                str[k++] = MD5_TABLE[byte0 >> 4 & 0xf];
                str[k++] = MD5_TABLE[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            throw new SignatureException(e.getMessage());
        }
    }
}

package com.imoxion.sensems.web.service;

import java.security.KeyPair;

import org.springframework.stereotype.Service;

import com.imoxion.security.ImRSAKey;
import com.imoxion.security.ImSecurityLib;

@Service
public class JCryptionService {

    /**
     * 
     * @Method Name : makeKey
     * @Method Comment : base64로 인코딩된 비밀키와 공개키를 생성
     * 
     * @return
     * @throws Exception
     */
    public ImRSAKey makeKey() throws Exception {

        ImRSAKey rsaKey = new ImRSAKey();

        return rsaKey;
    }

    public String generateKey(KeyPair keyPair) throws Exception {

        String retStr = "";
        String e = ImSecurityLib.getPublicKeyExponent(keyPair);
        String n = ImSecurityLib.getPublicKeyModulus(keyPair);

        retStr = "{\"e\":\"";
        retStr += e;
        retStr += "\",\"n\":\"";
        retStr += n;
        retStr += "\"}";

        retStr = retStr.replaceAll("\r", "").replaceAll("\n", "").trim();

        return retStr;
    }

    /**
     * Max block size with given key length
     * 
     * @param keyLength
     *            length of key
     * @return numeber of digits
     */
    public static int getMaxDigits(int keyLength) {
        return ((keyLength * 2) / 16) + 3;
    }

    /**
     * 
     * @Method Name : deCrypt
     * @Method Comment : 암호화된 데이터를 복호화하여 return
     * 
     * @param privateKey
     *            : RSA 비밀키
     * @param ecryptAESKey
     *            : RSA 공개키로 암호화된 AES 대칭키
     * @param encryptString
     *            : AES 대칭키로 암호화된 데이터
     * @return
     * @throws Exception
     */
    public String deCrypt(String privateKey, String ecryptAESKey, String encryptString) throws Exception {

        String plainStrig = "";
        String aesKey = ImSecurityLib.decryptRSAPrivate(privateKey, ecryptAESKey);
        plainStrig = ImSecurityLib.decryptAES(aesKey, encryptString);

        return plainStrig;
    }
}

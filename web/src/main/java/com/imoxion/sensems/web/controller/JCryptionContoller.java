package com.imoxion.sensems.web.controller;

import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.web.service.JCryptionService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.KeyPair;

@Controller
@RequestMapping(value = "/jCrypt/")
public class JCryptionContoller {
	protected Logger log = LoggerFactory.getLogger(JCryptionContoller.class);
    @Autowired
    private JCryptionService jCryptService;

    /**
     * 
     * @Method Name : makeKey
     * @Method Comment : RSA 키를 생성해서 클라이언트에 전달합니다.
     * 
     * @param session
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "json/makeKey.json")
    @ResponseBody
    public String getJSONmakeKey(HttpSession session) throws Exception {
        String retKey = null;
        log.debug("privateKey..11....{}",session.getAttribute("privateKey"));
        if (session.getAttribute("privateKey") == null) {
            //RSAkey 생성합니다. 사이즈는 2048
            KeyPair keyPair = ImSecurityLib.createRSAKey(2048);

            // privateKey는 base64로 인코딩해서 세션에 저장합니다.
            String prvKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));

            session.setAttribute("keyPair", keyPair);
            session.setAttribute("privateKey", prvKey);

            retKey = jCryptService.generateKey(keyPair);
            

        } else {
            retKey = jCryptService.generateKey((KeyPair) session.getAttribute("keyPair"));
        }
        log.debug("privateKey..22....{}", retKey);
        return retKey;

    }

    /**
     * 
     * @Method Name : encrypt
     * @Method Comment :
     * 
     * @param response
     * @param session
     * @param request
     * @param model
     * @param cryptString
     *            : AES 암호화된 데이터
     * @param key
     *            : RSA로 암호화된 AES 대칭키
     * 
     * @return 복호화된 평문 데이터
     * @throws Exception
     */
    @RequestMapping(value = "encrypt.do")
    public String encrypt(HttpServletResponse response, HttpSession session, HttpServletRequest request, ModelMap model,
        @RequestParam(value = "cryptString", required = false) String encryptString, @RequestParam(value = "key", required = false) String key)
            throws Exception {
    	   log.debug("privateKey.22......{}",session.getAttribute("privateKey"));
        String decryptStr = null;

        String prvKeyStr = session.getAttribute("privateKey").toString();
        //session.removeAttribute("privateKey");

        decryptStr = jCryptService.deCrypt(prvKeyStr, key, encryptString);

        return decryptStr;
    }
}

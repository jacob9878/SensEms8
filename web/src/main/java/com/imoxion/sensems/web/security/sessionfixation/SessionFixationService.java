package com.imoxion.sensems.web.security.sessionfixation;

import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.util.CookieUtil;
import com.imoxion.sensems.web.util.HttpRequestUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class SessionFixationService {

    private static final String encryptKey = "99cdf9k!a9d9fdf99%a";

    //로그인 시 user-agent와 ip를 이용하여 token 생성
    private String createToken(String user_agent, String remoteAddr) throws Exception{
        JSONObject token = new JSONObject();
        token.put("user-agent",user_agent);
        token.put("ip", remoteAddr);
        String s = token.toString();
        return ImSecurityLib.encryptAriaString(encryptKey, s, true);
    }

    //접속 시도한 user-agent와 ip를 이용하여 토큰에 저장되어 있는 값과 비교 실시.
    public boolean validation(HttpServletRequest request,HttpServletResponse response) throws Exception{
        CookieUtil cookieUtil = new CookieUtil(request,response);
        String secureToken = cookieUtil.get("EMS_SECURE_CODE");
        if(StringUtils.isEmpty(secureToken)){
            return false;
        }
        try {
            String decryptToken = ImSecurityLib.decryptAriaString(encryptKey,secureToken,true);
            JSONObject token = JSONObject.fromObject(decryptToken);
            String user_agent = token.getString("user-agent");
            String tokenIp = token.getString("ip");
            // 토큰 IP 와 사용자 IP 가 틀린 경우 사용자 agent 값이 틀리면 접속 차단
            if (!tokenIp.equalsIgnoreCase(HttpRequestUtil.getRemoteAddr(request))) {
                // 사용자 USER-AGENT 가 틀린경우 FAIL
                if (!user_agent.equalsIgnoreCase(request.getHeader("user-agent"))) {
                    return false;
                }
            }
        }catch (BadPaddingException be) {
            return false;
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    public void generationToken(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String token = createToken(request.getHeader("user-agent"),HttpRequestUtil.getRemoteAddr(request));
        boolean secureType = false;
        if (ImbConstant.getInstance().SSL_TYPE == ImbConstant.SSL_TYPE_TOTAL) {
            secureType = true;
        }
        Cookie cookie = new Cookie("EMS_SECURE_CODE",token);
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        cookie.setSecure(secureType);
        response.addCookie(cookie);
    }
}

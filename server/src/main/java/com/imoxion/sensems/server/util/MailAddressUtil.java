package com.imoxion.sensems.server.util;

import com.imoxion.common.util.ImStringUtil;

import javax.mail.internet.InternetAddress;

/**
 * 이메일주소를 입력받으면 이메일주소에서 userid 와 domain 을 분리한다.
 * Created by sunggyu on 2015-11-08.
 */
public class MailAddressUtil {

    /**
     * 메일주소를 받아서 userid 와 domain을 분리한다.
     * 만약 이메일주소가 아닌경우 입력받은 주소를 userid 로 간주한다.
     * @param email
     * @return
     */
    public static MailAddress getMailAddress(String email){
        MailAddress mailAddress = new MailAddress();
        if( email == null ){
            return mailAddress;
        }
        try{
            InternetAddress address = new InternetAddress(email);
            email = address.getAddress();
            mailAddress.setName( address.getPersonal() );
            mailAddress.setAddress( email );
        }catch (Exception e){
            email = ImStringUtil.getStringBetween(email , "<",">");
            mailAddress.setAddress( email );
        }
        if( email.indexOf("@") > -1 ){
            String[] s = ImStringUtil.getTokenizedString( email , "@" );
            if( s != null && s.length == 2){
                mailAddress.setUserid(s[0]);
                mailAddress.setDomain(s[1]);
            }else{
                mailAddress.setUserid(email);
            }
        }
        return mailAddress;
    }
}
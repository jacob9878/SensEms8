package com.imoxion.sensems.server.util;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class UUIDService {

    private static Random random = new Random();;

    /**
     * UUID 를 이용한 고유 키값 생성
     * @return
     */
    public static String getUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    private static Random getRandom(){
        return random;
    }

    /**
     * 시간조합한 숫자로 된 고유한 키를 생성한다.
     * @return
     */
    public synchronized static String getTraceID(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        StringBuffer sb = new StringBuffer(20);
        sb.append(sdf.format(new Date()));
        Random random = getRandom();
        for(int i = 0 ; i <= 2 ; i++){
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 메세지 ID를 생성한다.
     * @param domain
     * @return
     */
    public static String getMessageID(String domain){
        return "<"+ getUID() + "@" + domain +">";
    }

    /**
     * 입력받은 prefix + uid
     * @param prefix
     * @param domain
     * @return
     */
    public static String getMessageID(String prefix, String domain){
        prefix = StringUtils.isNotEmpty(prefix) ? prefix +"." : "";
        return "<"+ prefix + getUID() + "@" + domain +">";
    }
}
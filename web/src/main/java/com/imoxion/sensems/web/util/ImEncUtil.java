package com.imoxion.sensems.web.util;

import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.web.common.ImbConstant;
import org.apache.commons.lang.StringUtils;

public class ImEncUtil {
    private final String secretKey = ImbConstant.DATABASE_AES_KEY;
    private static final ImEncUtil encUtil = new ImEncUtil();
    private ImEncUtil(){}
    public static ImEncUtil getInstance(){
        return encUtil;
    }
    //-------------------------------------------------------------------------------

    /**
     * AES256암호화
     * @param src
     * @return
     * @throws Exception
     */
    public String encrypt(String src) throws Exception {
        return ImSecurityLib.encryptAES256(this.secretKey, src);
    }

    /**
     * AES256복호화
     * @param src
     * @return
     * @throws Exception
     */
    public String decrypt(String src) throws Exception {
        return ImSecurityLib.decryptAES256(this.secretKey, src);
    }

    public String replaceAll(String name){
        if(StringUtils.isNotEmpty(name)){
            name = name.replaceAll("/","");
            name = name.replaceAll("\\\\","");
            //name = name.replaceAll(".","");
            name = name.replaceAll("&","");
        }
        return name;
    }

    /*public static void main(String[] args) {
        try {
            System.out.println("result = " + ImEncUtil.getInstance().encrypt("test"));
        }catch (NullPointerException ne) {
            System.out.println("encrypt np exception - " + ne);
        }
        catch(Exception e){
            System.out.println("encrypt exception - " + e);
        }
    }*/
}

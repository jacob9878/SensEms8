package com.imoxion.sensems.server.util;

import com.imoxion.common.mail.ImMessage;
import com.imoxion.common.util.ImStringUtil;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.server.beans.ImRecvRecordData;
import com.imoxion.sensems.server.config.ImEmsConfig;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.unicode.ImCJKConverter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Session;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParseException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Properties;

public class ImEmsUtil {
    private static Logger logger = LoggerFactory.getLogger(ImEmsUtil.class);
    private static Logger senderLogger = LoggerFactory.getLogger("SENDER");

    public static String changeMimeEncoding(String p_sData, String targetCharset, String encoding){
        String sResult = null;
        Properties props = System.getProperties();
        Session session = Session.getInstance(props);
        InputStream is = null;
        try{
            //InputStream is = IOUtils.toInputStream(p_sData, "UTF-8");
            byte[] bytes = p_sData.getBytes();
            is = new ByteArrayInputStream(bytes);
            //InputStream is = new ByteArrayInputStream(p_sData.getBytes(StandardCharsets.UTF_8));
            ImMessage im = new ImMessage(session);
            im.parseMimeData(is);
            im.setMessageID(im.getMessageID());
            im.setCharset(targetCharset);
            im.setContentEncoding(encoding);

            if(StringUtils.isNotEmpty(im.getHtml())){
                im.setHtml(im.getHtml());
            } else {
                im.setText(im.getText());
            }

            //im.saveChanges();

            sResult = im.makeMimeData();
        }catch(ParseException ex){
            try{
                logger.error("changeMimeEncoding.. : "+ ex.getMessage());
            }catch(Exception ex1){}
        }catch(Exception ex){
            try{
                logger.error("changeMimeEncoding... : "+ ex);
            }catch(Exception ex1){}
        }finally{
            try { if(is != null) is.close(); }catch(Exception e){}
        }

        return sResult;
    }

    public static String getEncryptString(String plainStr) throws Exception {
        ImEmsConfig emsConfig = ImEmsConfig.getInstance();
        if(!emsConfig.isUseEncryptDB()){
            return plainStr;
        }
        String secret_key = emsConfig.getAesKey();
        String result = plainStr;

        try {
            result = ImSecurityLib.encryptAES256(secret_key, plainStr);
        } catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            logger.error("{} - ImbEmsUtil.getEncryptString error", errorId);
        }

        return result;
    }

    public static String getDecryptString(String encStr) throws Exception {
        ImEmsConfig emsConfig = ImEmsConfig.getInstance();
        if(!emsConfig.isUseEncryptDB()){
            return encStr;
        }
        String secret_key = emsConfig.getAesKey();
        String result = encStr;

        try {
            result = ImSecurityLib.decryptAES256(secret_key, encStr);
        } catch (Exception e) {
//            String errorId = ErrorTraceLogger.log(e);
//            logger.error("{} - ImbEmsUtil.getDecryptString error", errorId);
        }

        return result;
    }

    public static boolean isValidEmail(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static String doMapPage(ImRecvRecordData p_rData, String p_sStr){
        int nCount = p_rData.size();
        String strResult = p_sStr;
        for(int i=0;i<nCount;i++){
            senderLogger.trace("{} - doMapPage: {} - {}", i, p_rData.getName(i), p_rData.getValue(i));
            String strComp = "[#" + p_rData.getName(i) + "#]";
            strResult = ImStringUtil.replace(strResult, strComp, p_rData.getValue(i));
        }

        //senderLogger.info("doMapPage: Result - {}", strResult);
        return strResult;
    }

    public static String doMapPage(ImRecvRecordData p_rData,String p_sStr, String p_sCharset){
        int nCount = p_rData.size();
        String strResult = p_sStr;

        senderLogger.trace("doMapPage2 ----- : Result - {}", strResult);
        //QuotedPrintableCodec qpCodec = new QuotedPrintableCodec();

        for(int i=0;i<nCount;i++){
            String strComp = "[#" + p_rData.getName(i) + "#]";
            String sVal = "";
            try {
                // utf-8메일 (2008-05-26)
                if ("UTF-8".equalsIgnoreCase(p_sCharset)) {
                    //sVal = new String(p_rData.getValue(i).getBytes());
                    sVal = p_rData.getValue(i);
                    //sVal = qpCodec.encode(sVal);
                } else {
                    if ("ISO-2022-JP".equalsIgnoreCase(p_sCharset)) {
                        //System.out.println(new String(getMSToLocalCharset(p_rData.getValue(i), p_sCharset)) + " === " + new String(p_rData.getValue(i).getBytes(p_sCharset)));
                        //sVal = new String(p_rData.getValue(i).getBytes(p_sCharset));
                        sVal = new String(getMSToLocalCharset(p_rData.getValue(i), p_sCharset));
                    } else {
                        sVal = new String(p_rData.getValue(i).getBytes(p_sCharset));
                    }
                    //sVal = qpCodec.encode(sVal, p_sCharset);
                }

                senderLogger.trace("{} - doMapPage2: {} - {}", i, p_rData.getName(i), sVal);
                // utf-8메일 (2008-05-26)
            } catch (Exception  e) {
                sVal = p_rData.getValue(i);
            }
            strResult = ImStringUtil.replace(strResult, strComp, sVal);

            senderLogger.trace("doMapPage2: Result - {}", strResult);
        }

        return strResult;
    }

    public static String getDomainOfEmail(String email) {
        String[] addresses = email.split("@");
        if (addresses.length > 1) {
            return addresses[1];
        } else {
            return null;
        }
    }

    /**
     *
     * @param p_sText
     * @param p_sCharset
     * @param p_bSep 줄바꿈 여부
     * @return
     */
    public static String encodeUniText(String p_sText,String p_sCharset, boolean p_bSep){
        String sRet = "";
        String sDestCharset = p_sCharset;

        try{
            if(p_sCharset != null && !p_sCharset.equals("")
                    && Charset.isSupported(p_sCharset)){
                // 자바에서 지원되는 공통 문자열로 변경한다.
                Charset cs = Charset.forName(p_sCharset);
                sDestCharset = cs.displayName();

                byte[] sLocal = getMSToLocalCharset(p_sText,p_sCharset);
                if(sLocal == null){
                    sRet = MimeUtility.encodeText(p_sText, p_sCharset, "B");
                }else{

                    byte[] result = Base64.encodeBase64(sLocal,p_bSep);

                    if(p_bSep){
                        String sTemp = new String(result);
                        String[] arrResult = sTemp.split("\r\n");
                        for(int i = 0;i<arrResult.length;i++){
                            if(i != 0){
                                sRet +="\r\n\t=?"+sDestCharset+"?B?"+arrResult[i]+"?=";
                            }else{
                                sRet +="=?"+sDestCharset+"?B?"+arrResult[i]+"?=";
                            }
                        }
                    }else{
                        sRet =  "=?"+sDestCharset+"?B?"+new String(result)+"?=";
                    }
                }
            }else{
                sRet = p_sText;
            }
        }catch(UnsupportedEncodingException ex){}

        return sRet;
    }

    /**
     * MS932로 인코딩된 문자열을 ISO-2022-JP,Shift-jis, euc-jp로 변환한다.
     * null을 리턴하게 되면 인코딩이 필요없는 문자열임을 뜻함. 해당 문자열을
     * 가공하지 않고 그대로 사용해야 된다.
     *
     * @param p_sText 변경할 문자열
     * @param p_sCharset 소스 문자셋
     * @return 변환된 인코딩 문자열. 만약 해당사항이 없으면 null을 리턴한다.
     */
    public static byte[] getMSToLocalCharset(String p_sText, String p_sCharset){
        byte[] sRet = null;
        String sDestCharset = p_sCharset;

        try{
            // 자바에서 지원되는 공통 문자열로 변경한다.
            Charset cs = Charset.forName(p_sCharset);
            sDestCharset = cs.displayName();

            if(sDestCharset.equalsIgnoreCase("ISO-2022-JP") ){
                sRet = ImCJKConverter.changeSJIStoJIS(new ByteArrayInputStream(
                        p_sText.getBytes("MS932")));//sSource.getBytes()));
            }else if(sDestCharset.equalsIgnoreCase("EUC-JP")){
                sRet = ImCJKConverter.changeSJIStoEUC(new ByteArrayInputStream(
                        p_sText.getBytes("MS932")));
            }else if(sDestCharset.equalsIgnoreCase("SHIFT_JIS")){
                sRet = p_sText.getBytes("MS932");
            }else if(sDestCharset.equalsIgnoreCase("euc-kr")){
                sRet = p_sText.getBytes("MS949");//JisUtil.changeEUCKRToMS949(new ByteArrayInputStream(
                //bytes));
            }else if(sDestCharset.equalsIgnoreCase("big5")){
                sRet = p_sText.getBytes("MS950");
            }else if(sDestCharset.equalsIgnoreCase("GB2312")
                    ||sDestCharset.equalsIgnoreCase("EUC-CN")
                    ||sDestCharset.equalsIgnoreCase("x-EUC-CN")){
                sRet = p_sText.getBytes("MS936");//p_sText.getBytes("MS936");
            }
        }catch(UnsupportedEncodingException ex){
//              sRet = p_sText.getBytes();//new String(bytes);
        }

        return sRet;
    }

    public static String mergeContents(ImRecvRecordData p_rDate,String p_sCharset, String p_sMsgid,
                                       String p_sEmail, int p_nId, String p_sContents){
        String sResult = p_sContents;

        try{
            String encKey = ImEmsConfig.getInstance().getUrlEncryptKey();
            sResult = doMapPage(p_rDate, sResult, p_sCharset);

            /*String sRejectCode ="<a href='"+ImEmsConfig.getInstance().getRejectUrl()+"?msgid="+p_sMsgid+
                    "&email="+p_sEmail+"' target='_new'>";
            sResult = ImStringUtil.replace(sResult,"[REJECT]",sRejectCode);
            sResult = ImStringUtil.replace(sResult,"[/REJECT]","</a>");*/

            sResult = ImStringUtil.replace(sResult,"[$ID$]", ImSecurityLib.encryptAESUrlSafe(encKey,String.valueOf(p_nId)));
            sResult = ImStringUtil.replace(sResult,"[$EMAIL$]", ImSecurityLib.encryptAESUrlSafe(encKey,p_sEmail));
            //sResult = ImStringUtil.replace(sResult,"[$EMAIL$]", p_sEmail);
            sResult = ImStringUtil.replace(sResult,"[$TO$]", p_sEmail);

            sResult = ImStringUtil.replace(sResult, "\n", "\r\n");
            sResult = ImStringUtil.replace(sResult, "\r\r\n", "\r\n");
        }catch(Exception ex){
            logger.error("mergeContents: make mime data :"+ ex.getMessage()+" EMAIL :"+p_sEmail);
        }
        return sResult;
    }


}

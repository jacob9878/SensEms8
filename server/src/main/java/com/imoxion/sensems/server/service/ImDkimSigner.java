package com.imoxion.sensems.server.service;

import com.imoxion.sensems.server.domain.Dkim;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.repository.SmtpRepository;
import com.imoxion.sensems.server.smtp.ImSmtpSession;
import com.imoxion.sensems.server.util.ImSmtpSendData;
import de.agitos.dkim.ARCSigner;
import de.agitos.dkim.DKIMSigner;
import de.agitos.dkim.SMTPARCMessage;
import de.agitos.dkim.SMTPDKIMMessage;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.*;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * sensmail/conf/dkim.xml 파일이 바뀌면 자동 갱신 처리
 * sensmail/dkim/domain.private.key.der 파일이 먼저 존재해야 함(없으면 패스)
 * 순서: private.key.der 파일복사 -> dkim.xml 수정
 */
public class ImDkimSigner {

    private static Logger logger = LoggerFactory.getLogger("SMAIL");
    private Logger smtpLogger = LoggerFactory.getLogger("SMTP");

    private static ImDkimSigner dkimSigner;

    private ConcurrentHashMap<String, DKIMSigner> dkimSignerMap = new ConcurrentHashMap<String, DKIMSigner>();
    private ConcurrentHashMap<String, ARCSigner> arcSignerMap = new ConcurrentHashMap<String, ARCSigner>();

    public static synchronized ImDkimSigner getInstance(){
        if( dkimSigner == null ){
            dkimSigner = new ImDkimSigner();
        }
        return dkimSigner;
    }

    public static synchronized ImDkimSigner getInstance(String loggerName){
        if(loggerName != null) logger = LoggerFactory.getLogger(loggerName);
        if( dkimSigner == null ){
            dkimSigner = new ImDkimSigner();
        }
        return dkimSigner;
    }

    private ImDkimSigner(){
        load();
    }

    public void load(){
        try {
            SmtpRepository smtpDatabaseService = SmtpRepository.getInstance();

            ConcurrentHashMap<String,DKIMSigner> signerMap = new ConcurrentHashMap<>();
            ConcurrentHashMap<String,ARCSigner> arcMap = new ConcurrentHashMap<>();
            List<Dkim> dkimList = smtpDatabaseService.getDkimList();
            if( dkimList != null ){
                for(Dkim dkim : dkimList){
                    String domain = dkim.getDomain();
                    //String filename = SensData.getPath(SensData.DKIM) + File.separator + dkim.getFilename();
                    //DKIMSigner dkimSigner = new DKIMSigner(dkim.getDomain(),dkim.getSelector(),filename);
                    //ARCSigner arcSigner = new ARCSigner(dkim.getDomain(),dkim.getSelector(),filename);

                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(dkim.getPrivate_key());
                    RSAPrivateKey privKey = (RSAPrivateKey)keyFactory.generatePrivate(privSpec);
                    DKIMSigner dkimSigner = new DKIMSigner(dkim.getDomain(),dkim.getSelector(), privKey);
                    signerMap.put(domain, dkimSigner);

                    ARCSigner arcSigner = new ARCSigner(dkim.getDomain(),dkim.getSelector(), privKey);
                    arcMap.put(domain, arcSigner);
                    logger.info("Load DkimSigner Domain : {}", domain);
                }
            }else{
                logger.info("DKIM List is empty");
            }
            this.dkimSignerMap = signerMap;
            this.arcSignerMap = arcMap;
        } catch (Exception ex) {
            String errorId = ErrorTraceLogger.log(ex);
            logger.error("{} - ImDkimSigner loadDkim error", errorId);
        }
    }

    public boolean has(String domain){
        return this.dkimSignerMap.containsKey(domain);
    }

    public boolean hasArc(String domain){
        return this.arcSignerMap.containsKey(domain);
    }

    public void doDKIMSign(ImSmtpSendData sd, String domain){

        try {
            DKIMSigner dkimSigner = dkimSignerMap.get(domain);
			/*dkimSigner.setIdentity(sd.getFrom());
	        dkimSigner.setHeaderCanonicalization(Canonicalization.RELAXED);
	        dkimSigner.setBodyCanonicalization(Canonicalization.RELAXED);
	        dkimSigner.setLengthParam(true);
	        //dkimSigner.setSigningAlgorithm(SigningAlgorithm.SHA1withRSA);
	        //dkimSigner.setZParam(true);
	        */
            Properties props = System.getProperties();
            Session session = Session.getInstance(props);
            Message msg;
            try(BufferedInputStream fis = new BufferedInputStream(new FileInputStream(sd.getContentsFile()))) {
                msg = new SMTPDKIMMessage(session, fis, dkimSigner);
            }
            String from = null;
            try {
                InternetAddress[] ia = InternetAddress.parse(InternetAddress.toString(msg.getFrom()));
                if (ia != null && ia.length > 0) {
                    from = ia[0].getAddress();
                }
            } catch (AddressException e) {
                logger.warn("from parse error - from:{} , error:{}", msg.getFrom(), e.getMessage());
            }

            //smailLogger.info( "from - " + from + " / " + sd.getFrom());
            // 헤더의 from과 envelope의 from 이 같을때만 dkim 사인을 한다.
// sd.getFrom의 도메인으로만 한다.
//            if(StringUtils.equalsIgnoreCase(from,sd.getFrom())){
                try(BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream( sd.getContentsFile() ))){
                    //	msg.saveChanges();
                    msg.writeTo(fos);
                } catch (Exception ex) {
                    logger.info( "[{}] smtp send error-data 0 doDKIMSign: {} / error:{}", sd.getTraceID(), sd.getContentsFile(), ex.getMessage());
                }
//            }

        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("[{}] {} - [SendMail] smtp send error-data 1 doDKIMSign : {}",sd.getTraceID(), errorId , sd.getContentsFile());
        }
    }

    public String doDKIMSign(String mailData, String domain){
        DKIMSigner dkimSigner = null;
        ByteArrayInputStream is = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String result = null;

        try {
            if(StringUtils.isEmpty(mailData)) return null;

            dkimSigner = dkimSignerMap.get(domain);
			/*dkimSigner.setIdentity(sd.getFrom());
	        dkimSigner.setHeaderCanonicalization(Canonicalization.RELAXED);
	        dkimSigner.setBodyCanonicalization(Canonicalization.RELAXED);
	        dkimSigner.setLengthParam(true);
	        //dkimSigner.setSigningAlgorithm(SigningAlgorithm.SHA1withRSA);
	        //dkimSigner.setZParam(true);
	        */
            if(dkimSigner == null) {
                return mailData;
            }

            Properties props = System.getProperties();
            Session session = Session.getInstance(props);

            is = new ByteArrayInputStream(mailData.getBytes());
            Message msg = new SMTPDKIMMessage(session, is, dkimSigner);

            String from = null;
            try {
                InternetAddress[] ia = InternetAddress.parse(InternetAddress.toString(msg.getFrom()));
                from = ia[0].getAddress().toLowerCase();
            } catch (AddressException e) {}

            //smailImLog.info( "from - " + from + " / " + sd.getFrom());

            // 헤더의 from과 envelope의 from 이 같을때만 dkim 사인을 한다.
            if(from != null && from.contains(domain.toLowerCase())){
                try {
                    msg.writeTo(baos);
                    result = baos.toString();
                } catch (Exception ex) {
                    logger.info( "doDKIMSign error : " + ex);
                }
            }
        }catch(Exception e){
            logger.error("[SendMail] doDKIMSign error : " + e);
        } finally {
            try { if(is != null) is.close();} catch (Exception e) {}
        }

        return result;
    }

    public void doArcSign(ImSmtpSession smtps, String emlPath, String domain, int instance, boolean isSeal) throws Exception{
        //ARCSigner arcSigner = null;

        //try {
            ARCSigner arcSigner = arcSignerMap.get(domain);
            arcSigner.setInstance(instance);

            Properties props = System.getProperties();
            Session session = Session.getInstance(props);

            SMTPARCMessage msg = null;
            try(BufferedInputStream fis = new BufferedInputStream(new FileInputStream(emlPath))) {
                msg = new SMTPARCMessage(session, fis, arcSigner);
                if(isSeal) {
                    msg.setSeal(true);
                }
            }

            try(BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream( emlPath ))){
                msg.writeTo(fos);
            } /*catch (Exception ex) {
                logger.info( "[{}] doArcSign: {} / error:{}", smtps.getTraceID(), emlPath, ex.getMessage());
            }*/

//        }catch(Exception e){
//            String errorId = ErrorTraceLogger.log(e);
//            smtpLogger.error( "[{}] {} doArcSign: {}", smtps.getTraceID(), errorId , emlPath);
//            throw e;
//        }
    }

    public void setLogger(String loggerName){
        this.logger = LoggerFactory.getLogger(loggerName);
    }

}

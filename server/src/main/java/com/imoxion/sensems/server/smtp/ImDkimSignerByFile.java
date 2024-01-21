package com.imoxion.sensems.server.smtp;

import com.imoxion.sensems.server.environment.SensEmsEnvironment;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.util.ImSmtpSendData;
import de.agitos.dkim.ARCSigner;
import de.agitos.dkim.DKIMSigner;
import de.agitos.dkim.SMTPARCMessage;
import de.agitos.dkim.SMTPDKIMMessage;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.*;
import java.util.*;

/**
 * sensmail/conf/dkim.xml 파일이 바뀌면 자동 갱신 처리
 * sensmail/dkim/domain.private.key.der 파일이 먼저 존재해야 함(없으면 패스)
 * 순서: private.key.der 파일복사 -> dkim.xml 수정
 */
public class ImDkimSignerByFile {

    private Logger logger = LoggerFactory.getLogger("SMAIL");
    private Logger smtpLogger = LoggerFactory.getLogger("SMTP");

    private static ImDkimSignerByFile dkimSigner;

    private Map<String, DKIMSigner> dkimSignerMap = new HashMap<>();
    private Map<String, ARCSigner> arcSignerMap = new HashMap<>();

    public static ImDkimSignerByFile getInstance(){
        if( dkimSigner == null ){
            dkimSigner = new ImDkimSignerByFile();
        }
        return dkimSigner;
    }

    private ImDkimSignerByFile(){
        load();
    }

    public void load(){
        try {
            try{
                String sPath = SensEmsEnvironment.getSensEmsServerHome();
                String dkimPath = sPath + File.separator + "conf" + File.separator + "dkim.xml";
                File f = new File(dkimPath);
                if(!f.exists()){
                    // 만약 server.xml 이 없으면 아무런 작업을 하지 않는다.
                    return;
                }

                XMLConfiguration config = new XMLConfiguration(f);
                if(config == null){
                    return;
                }

                // map 초기화(안함)
                /*if(this.dkimSignerMap != null) {
                    this.dkimSignerMap.clear();
                    this.dkimSignerMap = null;
                }*/
                if(this.dkimSignerMap == null) this.dkimSignerMap = new HashMap<String, DKIMSigner>();
                if(this.arcSignerMap == null) this.arcSignerMap = new HashMap<String, ARCSigner>();

                List<String> listDkimDomain = new ArrayList<String>();

                Object prop = config.getProperty("DKIM.domain[@name]");
                if(prop != null){
                    if(prop instanceof Collection<?>){
                        int size = ((Collection<?>)prop).size();
                        for(int i=0;i<size;i++){
                            //ImDkimBean dkim = new ImDkimBean();
                            String domainName = (String)config.getProperty("DKIM.domain("+i+")[@name]");
                            String domainSelector = (String)config.getProperty("DKIM.domain("+i+")[@selector]");
                            String fileName = (String)config.getProperty("DKIM.domain("+i+")[@file]");
                            /*dkim.setDomain(domainName);
                            dkim.setSelector(domainSelector);
                            dkim.setFile(sPath +File.separator+"dkim"+File.separator + fileName);
                            dkimSignerMap.put(domainName, dkim);*/
                            fileName = sPath +File.separator+"dkim"+File.separator + fileName;
                            if(!new File(fileName).exists()) {
                                logger.error("ImDkimSigner load error: {} - {} not exists.", domainName, fileName);
                                continue;
                            }

                            DKIMSigner dkimSigner = new DKIMSigner(domainName, domainSelector, fileName);
                            this.dkimSignerMap.put(domainName, dkimSigner);
                            ARCSigner arcSigner = new ARCSigner(domainName, domainSelector, fileName);
                            this.arcSignerMap.put(domainName, arcSigner);

                            listDkimDomain.add(domainName);
                        }
                    }else{
                        //ImDkimBean dkim = new ImDkimBean();
                        String domainName = (String)config.getProperty("DKIM.domain[@name]");
                        String domainSelector = (String)config.getProperty("DKIM.domain[@selector]");
                        String fileName = (String)config.getProperty("DKIM.domain[@file]");
                        /*dkim.setDomain(domainName);
                        dkim.setSelector(domainSelector);
                        dkim.setFile(sPath +File.separator+"dkim"+File.separator + fileName);
                        dkimSignerMap.put(domainName,dkim);*/
                        fileName = sPath +File.separator+"dkim"+File.separator + fileName;
                        if(!new File(fileName).exists()) {
                            logger.error("ImDkimSigner load error: {} - {} not exists.", domainName, fileName);
                        } else {
                            DKIMSigner dkimSigner = new DKIMSigner(domainName, domainSelector, fileName);
                            this.dkimSignerMap.put(domainName, dkimSigner);
                            ARCSigner arcSigner = new ARCSigner(domainName, domainSelector, fileName);
                            this.arcSignerMap.put(domainName, arcSigner);
                            listDkimDomain.add(domainName);
                        }
                    }
                } else {
                    logger.info("DKIM Properties is empty");
                }

                // dkimSignerMap에 등록되지 않은 도메인은 제거처리
                // map을 초기화해도 되지만 다른 쓰레드에서 map을 참조하다가 null pointer 발생할수 있으므로
                if(this.dkimSignerMap.size() > 0){
                    Iterator<String> iter = this.dkimSignerMap.keySet().iterator();

                    while(iter.hasNext()){
                        String domain = iter.next();
                        if(!listDkimDomain.contains(domain)) {
                            iter.remove();
                        }
                    }
                    // 아래처럼 하면 ConcurrentModificationException 오류 발생함
                    /*for (Map.Entry<String, DKIMSigner> entry : this.dkimSignerMap.entrySet()) {
                        String domain = entry.getKey();
                        if(!listDkimDomain.contains(domain)){
                            this.dkimSignerMap.remove(domain);
                        }
                    }*/
                }
                if(this.arcSignerMap.size() > 0) {
                    Iterator<String> iter = this.arcSignerMap.keySet().iterator();

                    while (iter.hasNext()) {
                        String domain = iter.next();
                        if (!listDkimDomain.contains(domain)) {
                            iter.remove();
                        }
                    }
                }

                logger.info("DkimSigner Domain Count: {}", this.dkimSignerMap.size());
                for(String domain: listDkimDomain){
                    logger.info("Load DkimSigner Domain : {}", domain);
                }
            }catch(Exception ex){
                String errorId = ErrorTraceLogger.log(ex);
                logger.error("{} - [ImDkimSigner] ", errorId );
            }
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
        DKIMSigner dkimSigner = null;
        BufferedInputStream fis = null;
        BufferedOutputStream fos = null;

        try {
            dkimSigner = dkimSignerMap.get(domain);
			/*dkimSigner.setIdentity(sd.getFrom());
	        dkimSigner.setHeaderCanonicalization(Canonicalization.RELAXED);
	        dkimSigner.setBodyCanonicalization(Canonicalization.RELAXED);
	        dkimSigner.setLengthParam(true);
	        //dkimSigner.setSigningAlgorithm(SigningAlgorithm.SHA1withRSA);
	        //dkimSigner.setZParam(true);
	        */
            Properties props = System.getProperties();
            Session session = Session.getInstance(props);

            fis = new BufferedInputStream(new FileInputStream(sd.getContentsFile()));
            Message msg = new SMTPDKIMMessage(session, fis, dkimSigner);
            try { fis.close();} catch (Exception e) {}

            String from = null;
            try {
                InternetAddress[] ia = InternetAddress.parse(InternetAddress.toString(msg.getFrom()));
                from = ia[0].getAddress();
            } catch (AddressException e) {}

            //smailLogger.info( "from - " + from + " / " + sd.getFrom());

            // 헤더의 from과 envelope의 from 이 같을때만 dkim 사인을 한다.
            if(from != null && from.equalsIgnoreCase(sd.getFrom())){
                fos = new BufferedOutputStream(new FileOutputStream( sd.getContentsFile() ));

                try {
                    //	msg.saveChanges();
                    msg.writeTo(fos);
                } catch (Exception ex) {
                    logger.info( "[{}] smtp send error-data 0 doDKIMSign: {} / error:{}", sd.getTraceID(), sd.getContentsFile(), ex.getMessage());
                } finally {
                    try { fos.close();} catch (Exception e) {}
                }
            }
        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            logger.error("[{}] {} - [SendMail] smtp send error-data 1 doDKIMSign : {}",sd.getTraceID(), errorId , sd.getContentsFile());
        } finally {
            try { if(fis != null) fis.close();} catch (Exception e) {}
            try { if(fos != null) fos.close();} catch (Exception e) {}
        }
    }

    public void doArcSign(ImSmtpSession smtps, String emlPath, String domain, int instance, boolean isSeal) throws Exception{
        ARCSigner arcSigner = null;
        BufferedInputStream fis = null;
        BufferedOutputStream fos = null;

        try {
            arcSigner = arcSignerMap.get(domain);
            arcSigner.setInstance(instance);

            Properties props = System.getProperties();
            Session session = Session.getInstance(props);

            fis = new BufferedInputStream(new FileInputStream(emlPath));
            SMTPARCMessage msg = new SMTPARCMessage(session, fis, arcSigner);
            if(isSeal) {
                msg.setSeal(true);
            }

            try { fis.close();} catch (Exception e) {}

            fos = new BufferedOutputStream(new FileOutputStream( emlPath ));

            msg.writeTo(fos);
            /*try {
                msg.writeTo(fos);
            } catch (Exception ex) {
                String errorId = ErrorTraceLogger.log(ex);
                smtpLogger.info( "[{}] doArcSign: {} / error:{}", smtps.getTraceID(), emlPath, ex.getMessage());
            } finally {
                try { fos.close();} catch (Exception e) {}
            }*/

        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            smtpLogger.error( "[{}] {} doArcSign: {}", smtps.getTraceID(), errorId , emlPath);
            throw e;
        } finally {
            try { if(fis != null) fis.close();} catch (Exception e) {}
            try { if(fos != null) fos.close();} catch (Exception e) {}
        }
    }

}

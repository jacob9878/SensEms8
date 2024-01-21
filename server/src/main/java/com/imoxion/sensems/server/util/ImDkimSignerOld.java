package com.imoxion.sensems.server.util;


import de.agitos.dkim.DKIMSigner;
import de.agitos.dkim.SMTPDKIMMessage;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.StringUtils;
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
public class ImDkimSignerOld {
    public static Logger logger = LoggerFactory.getLogger("SENDER");

    private static ImDkimSignerOld dkimSigner;

    private Map<String, DKIMSigner> dkimSignerMap = new HashMap<String, DKIMSigner>();

    public static ImDkimSignerOld getInstance(){
        if( dkimSigner == null ){
            dkimSigner = new ImDkimSignerOld();
        }
        return dkimSigner;
    }

    private ImDkimSignerOld(){
        load();
    }

    public void load(){
        try {
            try{
                String sPath = System.getProperty("sensems.home");
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
                                logger.error("ImDkimSigner load error: "+domainName+ " - "+fileName+" not exists.");
                                continue;
                            }

                            DKIMSigner dkimSigner = new DKIMSigner(domainName, domainSelector, fileName);
                            this.dkimSignerMap.put(domainName, dkimSigner);
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
                            logger.error("ImDkimSigner load error: "+domainName+" - "+fileName+" not exists.");
                        } else {
                            DKIMSigner dkimSigner = new DKIMSigner(domainName, domainSelector, fileName);
                            this.dkimSignerMap.put(domainName, dkimSigner);
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

                logger.info("DkimSigner Domain Count: " + this.dkimSignerMap.size());
                for(String domain: listDkimDomain){
                    logger.info("Load DkimSigner Domain : {}", domain);
                }
            }catch(Exception ex){
                logger.error("[ImDkimSigner] : " + ex );
            }
        } catch (Exception ex) {
            logger.error("ImDkimSigner loadDkim error : " + ex);
        }
    }

    public boolean has(String domain){
        return this.dkimSignerMap.containsKey(domain);
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
}

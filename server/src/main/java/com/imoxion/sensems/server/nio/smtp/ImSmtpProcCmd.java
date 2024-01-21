package com.imoxion.sensems.server.nio.smtp;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.server.beans.ImQueueObj;
import com.imoxion.sensems.server.config.ImSmtpConfig;
import com.imoxion.sensems.server.exception.ImSmtpCloseException;
import com.imoxion.sensems.server.exception.ImSmtpException;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.logger.event.TransmitLogger;
import com.imoxion.sensems.server.nio.smtp.handler.ImSmtpAuthLoginHandler;
import com.imoxion.sensems.server.nio.smtp.handler.ImSmtpAuthPlainHandler;
import com.imoxion.sensems.server.nio.smtp.handler.ImSmtpDataHandler;
import com.imoxion.sensems.server.repository.SmtpRepository;
import com.imoxion.sensems.server.service.MessageQueueService;
import com.imoxion.sensems.server.service.SmtpConnectService;
import com.imoxion.sensems.server.service.TLSFailHostService;
import com.imoxion.sensems.server.smtp.ImSmtpSession;
import com.imoxion.sensems.server.util.ImSmtpUtil;
import com.imoxion.sensems.server.util.UUIDService;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslHandler;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSession;

public class ImSmtpProcCmd {
    public static Logger smtpLogger = LoggerFactory.getLogger("SMTP");
    private static final ImSmtpProcCmd processCommand = new ImSmtpProcCmd();
    public static ImSmtpProcCmd getInstance() {
        return processCommand;
    }
    private final String[] arrLocalhost = {"127.0.0.1", "0:0:0:0:0:0:0:1", "::1"};

    private void sendClient(ChannelHandlerContext ctx, Object msg){
        ctx.writeAndFlush((String)msg + "\r\n");
    }
    private void sendClient(ChannelHandlerContext ctx, String msg){
        ctx.writeAndFlush(msg + "\r\n");
    }
    private void sendClient(ChannelHandlerContext ctx, ImSmtpException e){
        sendClient(ctx, e.getMessage());
    }

    public void doProcessCommand(ChannelHandlerContext ctx, ImSmtpSession smtps, String p_sCommand){
        if(p_sCommand == null || p_sCommand.length() <= 0){
            throw new ImSmtpException("503 5.5.1");
        }

        //smtpLogger.debug( "["+smtps.getPeerIP()+"]["+ smtps.getSocket().getLocalPort() +"] CMD:" + p_sCommand);
        smtpLogger.debug( "[{}] CMD:{} (ip:{}:{}/country:{}({}))", smtps.getTraceID(), p_sCommand, smtps.getPeerIP(), smtps.getPeerPort(), smtps.getCountry(), smtps.getCountry_name() );

        if(p_sCommand.toUpperCase().startsWith("QUIT")){
            ChannelFuture future = ctx.write("221 2.0.0 Bye\r\n");
            future.addListener(ChannelFutureListener.CLOSE);
        }else if(p_sCommand.toUpperCase().startsWith("HELO")){
            smtps.addCommandCount();
            procHelo(ctx, smtps, p_sCommand);
        }else if(p_sCommand.toUpperCase().startsWith("EHLO")){
            smtps.addCommandCount();
            procEhlo(ctx, smtps, p_sCommand);
        }else if(p_sCommand.toUpperCase().startsWith("AUTH")){
            smtps.addCommandCount();
            procAuth(ctx, smtps, p_sCommand);
        }else if(p_sCommand.toUpperCase().startsWith("NOOP")){
            smtps.addCommandCount();
            procNoop(ctx, smtps, p_sCommand);
        }else if(p_sCommand.toUpperCase().startsWith("MAIL FROM")){
            smtps.addCommandCount();
            procMail(ctx, smtps, p_sCommand);
        }else if(p_sCommand.toUpperCase().startsWith("RCPT TO")){
            smtps.addCommandCount();
            procRcpt(ctx, smtps, p_sCommand);
        }else if(p_sCommand.toUpperCase().startsWith("RSET")){
            smtps.addCommandCount();
            procRset(ctx, smtps, p_sCommand);
        }else if(p_sCommand.toUpperCase().startsWith("DATA")){
            smtps.addCommandCount();
            procData(ctx, smtps, p_sCommand);
        }else if(p_sCommand.toUpperCase().startsWith("STARTTLS")){
            smtps.addCommandCount();
            procTls(ctx, smtps, p_sCommand);
        }else if(p_sCommand.toUpperCase().startsWith("X-THCOUNT")){
            smtps.addCommandCount();
            procThCount(ctx, smtps, p_sCommand);
        }else if(p_sCommand.toUpperCase().startsWith("X-CLIENTLIST")) {
            procConnClientList(ctx, smtps, p_sCommand);
        }else if(p_sCommand.toUpperCase().startsWith("X-JOURNALSEND")) {
            procJournalSend(ctx, smtps, p_sCommand);
        }else if(p_sCommand.toUpperCase().startsWith("X-QUEUECOUNT")){
            smtps.addCommandCount();
            procQueueCount(ctx, smtps, p_sCommand);
        }else{
            sendClient(ctx,"500 5.5.1 Syntax error, command unrecognized");
        }
    }

    /**
     * HELO 명령
     */
    public void procHelo(ChannelHandlerContext ctx, ImSmtpSession smtps, String p_sCommand) {
        String[] arrMsg = p_sCommand.split(" ");

        if(arrMsg.length < 2){
            // 인자에 도메인이 없는 경우 도메인이 없다는 메시지.
            throw new ImSmtpException("501 5.5.2");
        }

        // 도메인 정보를 세팅한다.
        smtps.setHeloDomain( arrMsg[1].trim());

        // 명령어 상태를 HELO 상태로 변경
        smtps.setSmtpState(ImSmtpSession.SMTP_STATE_HELO);

        // 클라이언트에 성공 메시지를 보낸다.
        String sRetMsg = "250 "+ ImSmtpConfig.getInstance().getHeloHost() +" Hello [" + smtps.getPeerIP() + "]";
        sendClient(ctx, sRetMsg);
    }

    public void procEhlo(ChannelHandlerContext ctx, ImSmtpSession smtps, String p_sCommand) {
        String[] arrMsg = p_sCommand.split(" ");

        if (arrMsg.length < 2) {
            // EHLO 명령어 다음에 도메인 명이 없을 경우.
            throw new ImSmtpException("501 5.5.2");
        }

        // HELO 도메인을 저장해 둔다.
        smtps.setHeloDomain(arrMsg[1].trim());

        // ylmf-pc brute force attack
        if ("ylmf-pc".equalsIgnoreCase(smtps.getHeloDomain())) {
            if (ImSmtpConfig.getInstance().getYlmfpcBlock() == 1) {
                smtpLogger.info("[{}] CMD:EHLO ({}) ylmf-pc access denied : {}", smtps.getTraceID(), smtps.getPeerIP(), smtps.getHeloDomain());
                throw new ImSmtpCloseException("421 4.3.1");
            }
        }

        // 상태를 HELO 완료 상태로 변경
        smtps.setSmtpState(ImSmtpSession.SMTP_STATE_HELO);

        // EHLO 명령 Response 를 보낸다.
        sendClient(ctx, "250-" + ImSmtpConfig.getInstance().getRootDomain());
        //String sOut = "250-AUTH LOGIN PLAIN CRAM-MD5";

        // 가능한 인증 상태를 알려준다. LOGIN 과 PLAIN 인증만 허용
        String sRetMsg = "250-AUTH LOGIN PLAIN";
        // smtp 인증을 사용하는 경우에만 표시
        if (ImSmtpConfig.getInstance().isUseSmtpAuth()) {
            sendClient(ctx, sRetMsg);
        }

        // ENHANCED STATUS CODES
        // status-code ::= class "." subject "." detail
        // class       ::= "2" / "4" / "5"
        // subject     ::= 1*3digit
        // detail      ::= 1*3digit
        //sendClient(smtps.getSocket(), "250-ENHANCEDSTATUSCODES");

        //*
        ImSmtpConfig smtpConfig = ImSmtpConfig.getInstance();
        if (smtpConfig.isUseTLS() && !smtps.isStartTLS()) {
            // tls 실패 아이피가 아닌경우에 starttls 처리
            TLSFailHostService failTLSHostService = TLSFailHostService.getInstance();
            if (!failTLSHostService.hasValue(smtps.getPeerIP()+ "|" + smtps.getHeloDomain())) {
                // TLS 모드가 가능할 때는 가능하다는 상태를 알려준다.
                sendClient(ctx, "250-STARTTLS");
            } else {
                smtpLogger.info("[{}] CMD:EHLO ({}) in TLS Failed Hosts", smtps.getTraceID(), smtps.getPeerIP());
            }
        }
        //*/

        if(smtps.isStartTLS()){
            //System.out.println("session = " + smtps.getSslContext());

            SslHandler sslhandler = (SslHandler) ctx.channel().pipeline().get("sslHandler");
            SSLSession session = sslhandler.engine().getSession();
            smtpLogger.info("[{}] TLS version = {} / cipher = {}", smtps.getTraceID(), session.getProtocol(), session.getCipherSuite());
        }

        // 최대 허용 메시지 상태를 알려준다.
        sRetMsg = "250 SIZE " + ImSmtpConfig.getInstance().getMaxMsgSize();
        sendClient(ctx, sRetMsg);
    }

   

    /**
     * Auth login 방식 처리
     */
    private void doAuthLogin(ChannelHandlerContext ctx, ImSmtpSession smtps, String sAuthParam){
    	ChannelPipeline cp = ctx.pipeline();
        cp.replace("basehandler", "authhandler", new ImSmtpAuthLoginHandler(smtps, ctx.handler(), sAuthParam));
    }

    /**
     * Auth plain 방식 처리
     */
    private void doAuthPlain(ChannelHandlerContext ctx, ImSmtpSession smtps, String sAuthParam){
    	ChannelPipeline cp = ctx.pipeline();
        cp.replace("basehandler", "authhandler", new ImSmtpAuthPlainHandler(smtps, ctx.handler(), sAuthParam));
    }

    /**
     * AUTH 명령
     */
    public void procAuth(ChannelHandlerContext ctx, ImSmtpSession smtps, String p_sCommand) {
    	if(!ImSmtpConfig.getInstance().isUseSmtpAuth()){
            throw new ImSmtpException("501 5.5.0");
        }
        String[] arrMsg = p_sCommand.split(" ");
        String sAuthParam = "";
        String sAuthType = "";

        if(smtps.getSmtpState() != ImSmtpSession.SMTP_STATE_HELO){
            throw new ImSmtpException("503 5.5.1");
        }

        if(arrMsg.length < 2){
            throw new ImSmtpException("501 5.5.2");
        }
        sAuthType = arrMsg[1].trim();

        if(arrMsg.length > 2){
            sAuthParam = arrMsg[2].trim();
        }

        if(sAuthType.equalsIgnoreCase("plain")){
            doAuthPlain(ctx, smtps, sAuthParam);
        } else if(sAuthType.equalsIgnoreCase("login")){
            doAuthLogin(ctx, smtps, sAuthParam);
        } else {
            smtpLogger.info( "[{}] Auth (" + smtps.getPeerIP() + "/ " + smtps.getCountry() + ") auth failed.", smtps.getTraceID());
            throw new ImSmtpException("501 5.5.2");
        }
    }

    private boolean doCheckRelayCapability(ImSmtpSession smtps) throws ImSmtpException {
    	// smtp 인증했으면 true
    	if(smtps.isRelay()) {
    		return true;
    	}
    	if(StringUtils.isNotEmpty(smtps.getLogonUser())){
        	return true;
        }
       
    	// 릴레이 허용 아이피에 등록되어 있으면 true
    	if(ImSmtpUtil.isRelayIP(smtps)){
    		return true;
    	}
    	
    	return false;        
    }

    private boolean doExtractEmail(ImSmtpSession smtps, String sFrom){
        try{
            sFrom = sFrom.trim();
            String sEmail = "";

            // <>를 제거한다.
            sEmail = ImStringUtil.getStringBetween(sFrom, "<", ">");

            // @로 아이디와 도메인을 분류한다.
            String[] arrFrom = ImStringUtil.getTokenizedString(sEmail, "@");
            if(arrFrom.length > 1){
                // 이메일 주소를 설정한다.
                smtps.setFrom(sEmail);
                // 도메인을 설정한다.
                smtps.setFromDomain(arrFrom[1]);
                // 사용자 아이디를 설정한다.
                smtps.setFromUserID(arrFrom[0]);

            }else{
                return false;
            }

            // FROM 이메일 주소가 255자를 넘었을 때는 초기화하고 에러를 리턴
            // 대부분 스팸일 가능성이 크다.
            if(smtps.getFrom().length() > 255){
                smtps.setFrom( "");
                smtps.setFromDomain( "");
                return false;
            }

        } catch(Exception ex){
            String errorId = ErrorTraceLogger.log(ex);
            smtpLogger.error("[{}] {} - [SMTP] : doExtractEmail ({}) " , smtps.getTraceID(), errorId  ,smtps.getPeerIP() );
            return false;
        }

        return true;
    }

    /*public boolean isBlockEmail(ImSmtpSession smtps){
        boolean bRet = false;
        String sDomain = "";
        int nCnt = 0;

        try{
            SmtpRepository smtpDatabaseService = SmtpRepository.getInstance();

            String[] arrAddr = ImStringUtil.getTokenizedString(smtps.getFrom(), "@");
            if(arrAddr.length > 0){
                sDomain = arrAddr[1];
            }

            nCnt = smtpDatabaseService.getBlockEmailCount(smtps.getFrom(), "*@"+sDomain);
            if(nCnt > 0) bRet = true;
        }catch(Exception ex){
            String errorId = ErrorTraceLogger.log(ex);
            smtpLogger.error("{} - [isBlockEmail] Check Block Email (" + smtps.getFrom() +")", errorId );
        }

        return bRet;
    }*/
    public void procMail(ChannelHandlerContext ctx, ImSmtpSession smtps, String p_sCommand) {
        String[] arrMsg = p_sCommand.split(":");

        // 먼저 HELO나 EHLO 명령어가 먼저 왔는지 확인한다.
        if( (smtps.getSmtpState() != ImSmtpSession.SMTP_STATE_HELO)
                && (smtps.getSmtpState() != ImSmtpSession.SMTP_STATE_AUTH) ){
            throw new ImSmtpException("503 5.5.1");
        }

        // FROM 이메일 주소가 있는지 확인한다.
        if(arrMsg.length < 2){
            throw new ImSmtpException("501 5.5.2");
        }

        // 465/587 포트로 연결을 했다면 인증을 통과한 사용자인지 확인한다.
        ImSmtpConfig smtpConfig = ImSmtpConfig.getInstance();
     // 587 포트로 연결을 했다면 인증을 통과한 사용자인지 확인한다.
        if(smtpConfig.getIsUseIsp() == 1)  {
            if(smtps.getPeerPort() == smtpConfig.getSslPort() ||
                    (smtps.getPeerPort()== smtpConfig.getIspPort() && smtpConfig.getUseIspAuth() == 1 ) ){
                if(StringUtils.isEmpty(smtps.getLogonUser())){
                    smtpLogger.info( "[{}] Mail From ({}) {} port : unauthenticated user : {}",smtps.getTraceID(),smtps.getPeerIP(),smtps.getPeerPort(),arrMsg[1]);
                    throw new ImSmtpException("530 5.5.1");
                }
            }
        }
        
     // 릴레이 허용 여부를 확인한다.
        boolean bCheckRelay = doCheckRelayCapability(smtps);

        // 만약 강제 인증 설정이 있으면 인증을 받지 않는 상태에서 사용하지 못하게 한다.
        if(!bCheckRelay && ImSmtpConfig.getInstance().getIsForceAuth() == 1){
            if(smtps.getLogonUser().equals("")){
                smtpLogger.info( "[{}] Mail From ({}) Force Auth : unauthenticated user : {}",smtps.getTraceID(),smtps.getPeerIP(),arrMsg[1]);
                throw new ImSmtpException("530 5.5.1");
            }
        }
		
        // FROM 명령어에서 size 부분을 잘라낸다.
        arrMsg[1] = arrMsg[1].trim();
        String[] arrFromExtension = arrMsg[1].toLowerCase().split(" size=");
        if(arrFromExtension.length >= 2){
            String tempFrom = ImStringUtil.getStringBetween(arrFromExtension[0], "<",">");
            smtps.setFrom(tempFrom);

            // size 외에 다른 부분이 붙어 있는 지 확인.
            if(arrFromExtension[1] != null && !arrFromExtension[1].equals("") ){
                // auth 부분을 잘라 낸다.
                String[] arrSizeAuth = arrFromExtension[1].toLowerCase().split(" auth=");
                // 메시지 사이즈를 넣는다.
                smtps.setMsgSize(Long.parseLong(arrSizeAuth[0]));
                if(smtps.getMsgSize() > ImSmtpConfig.getInstance().getMaxMsgSize()){
                    TransmitLogger transmitLogger = new TransmitLogger();
//                    transmitLogger.setServerid(ImSmtpConfig.getInstance().getServerID());
//                    transmitLogger.setTraceid(smtps.getTraceID());
//                    transmitLogger.setWork(TransmitLogger.WORK_RECEIVE);
//                    transmitLogger.setIp(smtps.getPeerIP());
//                    transmitLogger.setFrom(smtps.getFrom());
//                    transmitLogger.setSize(smtps.getMsgSize());
                    transmitLogger.setDescription("error.max_messagesize");
//                    transmitLogger.info();
                    transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
                    ImSmtpUtil.doTransmitLog(transmitLogger, smtps);

                    smtpLogger.info( "[{}] Mail From ( ip:{} / from:{} / size:{} byte) message size too big.",smtps.getTraceID(),smtps.getPeerIP(),smtps.getFrom(),smtps.getMsgSize());
                    throw new ImSmtpException("552 5.2.3");
                }
            }
        }

        // FROM 메일 주소가 NULL 인지를 체크한다.
        if(arrFromExtension[0].equals("<>")){ //From null
            smtps.setFrom("");
            smtps.setFromDomain("");
            smtps.setFromUserID("");

        }else{
            // FROM 이메일 주소를 아이디, 도메인으로 분류한다.
            if(!doExtractEmail(smtps, arrFromExtension[0])){
                smtpLogger.info( "[{}] Mail From (" + smtps.getPeerIP() + ") " + arrFromExtension[0] + " syntax error",smtps.getTraceID());
                throw new ImSmtpException("501 5.5.2");
            }

            // FROM 주소가 없는지 체크
            if(smtps.getFrom().length() == 0){
                smtpLogger.info( "[{}] Mail From (" + smtps.getPeerIP() + ") address is null",smtps.getTraceID());
                throw new ImSmtpException("501 5.5.2");
            }

            // FROM 이메일 주소가 200자 이상이면 받지 않는다.
            if(smtps.getFrom().length() > 200){
                smtpLogger.info( "[{}] Mail From (" + smtps.getPeerIP() + ") address is too long : " + smtps.getFrom(),smtps.getTraceID());
                throw new ImSmtpException("501 5.5.1");
            }

            // 만약 FROM 메일 주소가 killerdaemon@sensmail.com 일때는 서버의 라이선스를 없애고 서버를 종료
            // 차후 관리를 위한 프로세스
//            if(smtps.getFrom().equals("testhackdaemon@abcabc.com")){
//                String sCertKeyFile = ImSmtpConfig.getInstance().getSensmailHome() + "/conf/mecertkey.key";
//                ImFileUtil.deleteFile(sCertKeyFile);
//                try {
//                    smtpServer.stopServer();
//                } catch(Exception ex){
//                    //  smtpLogger.error( "CMD : kill  " + ex.getMessage());
//                }
//                System.exit(0);
//                return false;
//            }

            // 이메일 주소에 @ 가 없다면 잘못된 이메일 주소라고 리턴한다.
            int nPos = smtps.getFrom().indexOf("@");
            if(nPos == -1){
                smtpLogger.info( "[{}] Mail From (" + smtps.getPeerIP() + ") address is malformed : " + smtps.getFrom(),smtps.getTraceID());
                throw new ImSmtpException("501 5.5.2");
            }

            // 차단메일인지 확인
//            if(isBlockEmail(smtps)){
//                throw new ImSmtpException("500 5.5.1", new String[]{smtps.getFrom()});
//            }
        }

        if(ImSmtpConfig.getInstance().getIsBlockNotRelay() == 1){
            if(!smtps.isRelay()){
                throw new ImSmtpException("530 5.5.1");
            }
        }
		
        // 메시지 아이디를 생성한다.
        smtps.setMsgID(UUIDService.getUID());
        // SMTP 상태를 FROM 통과 상태로 세팅
        smtps.setSmtpState(ImSmtpSession.SMTP_STATE_MAILFROM);

        // FROM 에 대한 성공 리턴을 보낸다.
        String sRetMsg = "250 2.1.0 "+ smtps.getFrom() +" Sender OK";
        sendClient(ctx, sRetMsg);
    }

    private boolean doCheckRcptTo(ImSmtpSession smtps, String sRcptTo){

        try{
            String sRcptUserID = "";
            String sRcptDomain = "";
            //int nAtPos = sRcptTo.indexOf("@");
            boolean bLocalDomain = false;
            String sEmail = "";

            sRcptTo = sRcptTo.toLowerCase();
            sEmail = ImStringUtil.getStringBetween(sRcptTo, "<", ">");

            // 수신자 이메일 주소를 분석한다. 도메인, 사용자 아이디로 분류한다.
            String[] arrTo = ImStringUtil.getTokenizedString(sEmail, "@");
            if(arrTo.length < 2){
            	smtpLogger.info("[{}] Rcpt To ( {} / {} -> {} ) incorrect email", smtps.getTraceID(), smtps.getPeerIP(), smtps.getFrom(),sRcptTo);
                throw new ImSmtpException("501 5.5.2");
            }

            // 차단메일인지 확인
//            if(isBlockEmail(smtps)){
//                throw new ImSmtpException("500 5.5.1", new String[]{sEmail});
//            }

            sRcptUserID = arrTo[0];
            sRcptDomain = arrTo[1].toLowerCase();

            SmtpRepository smtpDatabaseService = SmtpRepository.getInstance();
            // smtp인증을 통과했으면(isRelay == true) 릴레이아이피 체크를 안해도 됨
            if(!smtps.isRelay()) {
                // 릴레이 아이피 체크
                if (!ImSmtpUtil.isRelayIP(smtps)) {
                    smtpLogger.info("[{}] Rcpt To ( {} / {} -> {} ) Relay denied", smtps.getTraceID(), smtps.getPeerIP(), smtps.getFrom(), sRcptTo);
                    throw new ImSmtpException("550 5.1.1(2)");
                } else {
                    // 내부 사용자라는 플래그를 세팅한다.
                    smtps.setRelay(true);
                    smtps.setLocal(true);
                    //return true;
                }
            }
        }catch(ImSmtpException e){
            String errorId = ErrorTraceLogger.log(e);
            smtpLogger.error("[{}] {} - [SMTP] RCPT - check rcptto(" + smtps.getPeerIP() + "/" + smtps.getFrom() + " -> " + sRcptTo + ") " , smtps.getTraceID(), errorId );
            throw e;
        }catch(Exception e){
            String errorId = ErrorTraceLogger.log(e);
            smtpLogger.error("[{}] {} - [SMTP] RCPT - check rcptto(" + smtps.getPeerIP() + "/" + smtps.getFrom() + " -> " + sRcptTo + ") " , smtps.getTraceID(), errorId );
            throw new ImSmtpException("501 5.5.2");
        }

        return true;
    }

    public void procRcpt(ChannelHandlerContext ctx, ImSmtpSession smtps, String p_sCommand) {
        String[] arrMsg = p_sCommand.split(":");

        // MAIL FROM 명령어가 먼저 왔는지 확인한다.
        if((smtps.getSmtpState() != ImSmtpSession.SMTP_STATE_MAILFROM) &&
                (smtps.getSmtpState() != ImSmtpSession.SMTP_STATE_RCPTTO)){
            throw new ImSmtpException("503 5.5.1");
        }

        if(arrMsg.length < 2){
            throw new ImSmtpException("501 5.5.2");
        }

        // 수신자 이메일 주소에서 <>를 잘라낸다.
        String sRcptTo = ImStringUtil.getStringBetween(arrMsg[1].trim(),"<",">");
        // 작은따옴표 처리
        if(ImSmtpConfig.getInstance().getRemoveQuotes() == 1){
            sRcptTo = ImStringUtil.replace(sRcptTo.trim(), "'", "");
        }

        if(sRcptTo != null){
            sRcptTo = sRcptTo.trim();
            //sRcptTo = sRcptTo.toLowerCase();
        }

        // 중복이 있으면 여기서
        if(smtps.getArrRcpt().contains(sRcptTo)){
            smtps.setSmtpState( ImSmtpSession.SMTP_STATE_RCPTTO);
            String sRetMsg = "250 2.1.5 "+ sRcptTo +" Recipient OK";
            sendClient(ctx, sRetMsg);
            smtpLogger.info( "[{}] Rcpt To (" + smtps.getPeerIP() + " / " + smtps.getFrom() + " -> " + sRcptTo + " is already in rcpt list", smtps.getTraceID());
            return;
        }

        // 최대 동보 수신자를 초과 했는지 확인한다.
        if(smtps.getCurrRcpt()+1 > ImSmtpConfig.getInstance().getMaxRcpt()){
            TransmitLogger transmitLogger = new TransmitLogger();
//            transmitLogger.setServerid(ImSmtpConfig.getInstance().getServerID());
//            transmitLogger.setTraceid(smtps.getTraceID());
//            transmitLogger.setWork(TransmitLogger.WORK_RECEIVE);
//            transmitLogger.setIp(smtps.getPeerIP());
//            transmitLogger.setFrom(smtps.getFrom());
            transmitLogger.setTo(sRcptTo);
//            transmitLogger.setSize(smtps.getMsgSize());
            transmitLogger.setDescription("error.max_rcpt");
//            transmitLogger.info();
            transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
            ImSmtpUtil.doTransmitLog(transmitLogger, smtps);

            smtps.setError(true);
            smtps.setErrorMessage("452 4.5.3 Too many recipients (" + ImSmtpConfig.getInstance().getMaxRcpt() + ")");
            smtpLogger.info( "[{}] Rcpt To (" + smtps.getPeerIP() + " / " + smtps.getFrom() + " -> " + sRcptTo + ") Too many recipients(" + smtps.getCurrRcpt() + ")",smtps.getTraceID());
            /*if(ImSmtpConfig.getInstance().getMaxRcptErrors().equals("")){
                throw new ImSmtpException("452 4.5.3");
            } else {
                throw new ImSmtpException("452 4.5.3", new String[]{ImSmtpConfig.getInstance().getMaxRcptErrors()});
            }*/
            throw new ImSmtpException("452 4.5.3", new String[]{"(max " + ImSmtpConfig.getInstance().getMaxRcpt() + "recipients)"});
        }

        // rset을 하더라도 총 수신자수는 계속 더해진다.
        if(smtps.getTotRcpt()+1 > ImSmtpConfig.getInstance().getMaxRcpt()){
            TransmitLogger transmitLogger = new TransmitLogger();
//            transmitLogger.setServerid(ImSmtpConfig.getInstance().getServerID());
//            transmitLogger.setTraceid(smtps.getTraceID());
//            transmitLogger.setWork(TransmitLogger.WORK_RECEIVE);
//            transmitLogger.setIp(smtps.getPeerIP());
//            transmitLogger.setFrom(smtps.getFrom());
            transmitLogger.setTo(sRcptTo);
//            transmitLogger.setSize(smtps.getMsgSize());
            transmitLogger.setDescription("error.max_rcpt");
//            transmitLogger.info();
            transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
            ImSmtpUtil.doTransmitLog(transmitLogger, smtps);

            smtps.setError(true);
            smtps.setErrorMessage("452 4.5.3 Too many recipients (" + ImSmtpConfig.getInstance().getMaxRcpt() + ")");
            smtpLogger.info( "[{}] Rcpt To (" + smtps.getPeerIP() + " / " + smtps.getFrom() + " -> " + sRcptTo + ") Too many recipients(" + smtps.getTotRcpt() + " rcpts / " + smtps.getRsetCount() + " rset)",smtps.getTraceID());
            /*if(ImSmtpConfig.getInstance().getMaxRcptErrors().equals("")){
                throw new ImSmtpException("452 4.5.3");
            } else {
                throw new ImSmtpException("452 4.5.3", new String[]{ImSmtpConfig.getInstance().getMaxRcptErrors()});
            }*/
            throw new ImSmtpException("452 4.5.3", new String[]{"(max " + ImSmtpConfig.getInstance().getMaxRcpt() + "recipients)"});
        }

        // 최대 수신사이즈 < 한통당 최대사이즈*수신자수
        if(smtps.getMsgSize() != 0 && ImSmtpConfig.getInstance().getTotMaxMsgSize() != 0 &&
                (ImSmtpConfig.getInstance().getTotMaxMsgSize() < (long)((smtps.getCurrRcpt()+1) * smtps.getMsgSize())) ){

            TransmitLogger transmitLogger = new TransmitLogger();
//            transmitLogger.setTraceid(smtps.getTraceID());
//            transmitLogger.setServerid(ImSmtpConfig.getInstance().getServerID());
//            transmitLogger.setWork(TransmitLogger.WORK_RECEIVE);
//            transmitLogger.setIp(smtps.getPeerIP());
//            transmitLogger.setFrom(smtps.getFrom());
            transmitLogger.setTo(sRcptTo);
//            transmitLogger.setSize(smtps.getMsgSize());
            transmitLogger.setDescription("error.max_messagesize");
//            transmitLogger.info();
            transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
            ImSmtpUtil.doTransmitLog(transmitLogger, smtps);

            smtpLogger.info( "[{}] Rcpt (" + smtps.getPeerIP() + " / " + smtps.getFrom() +" / " + smtps.getCurrRcpt() + " rcpts / " + smtps.getMsgSize() +  " Byte) message size too big.",smtps.getTraceID());
            smtps.setError(true);
            smtps.setErrorMessage("552 5.2.3 Message exceeds fixed maximum message size");
            throw new ImSmtpException("552 5.2.3");
        }

        // RCPT TO 권한 정보를 확인한다.
        if (!doCheckRcptTo(smtps, sRcptTo)) {
            return;
        }

        smtps.setSmtpState( ImSmtpSession.SMTP_STATE_RCPTTO);
        // 수신자 정보를 목록에 넣는다.
        smtps.getArrRcpt().add(sRcptTo);
        smtps.setCurrRcpt(smtps.getCurrRcpt()+1);

        //smtpLogger.info( "Rcpt To (" + smtps.m_sPeerIP + " / " + smtps.m_sFrom + " / " + sRcptTo + ") OK");
        // RCPT TO Response 를 보낸다.
        String sRetMsg = "250 2.1.5 "+ sRcptTo +" Recipient OK";
        sendClient(ctx, sRetMsg);
    }

    /**
     * DATA 명령
     */
    public void procData(ChannelHandlerContext ctx, ImSmtpSession smtps, String p_sCommand) {
        if(smtps.getSmtpState() != ImSmtpSession.SMTP_STATE_RCPTTO){
            throw  new ImSmtpException("503 5.5.1");
        }

        // 준비되었다는 메시지를 클라이언트에 보낸다.
        sendClient(ctx,"354 Start mail input; end with <CRLF>.<CRLF>");
        //ctx.write("354 Start mail input; end with <CRLF>.<CRLF>\r\n");

        ChannelPipeline cp = ctx.pipeline();
        cp.replace("basehandler", "datahandler", new ImSmtpDataHandler(smtps, ctx.handler()));
    }

    /**
     * STARTTLS 명령
     */
    public void procTls(ChannelHandlerContext ctx, ImSmtpSession smtps, String p_sCommand) {
        ImSmtpConfig smtpConfig = ImSmtpConfig.getInstance();
        if(!smtpConfig.isUseTLS()){
            throw new ImSmtpException("500 5.5.2");
        }

        if(smtps.getSmtpState() != ImSmtpSession.SMTP_STATE_HELO){
            throw new ImSmtpException("503 5.5.1");
        }

         /*
            1. create a new SslHandler instance with startTls flag set to true,
            2. insert the SslHandler to the ChannelPipeline, and
            3. write a StartTLS response.
         */
        ctx.pipeline().addFirst("sslHandler", smtps.getSslContext().newHandler(ctx.channel().alloc()));

        // 준비되었다는 메시지를 클라이언트에 보낸다.
        sendClient(ctx,"220 2.0.0 Ready to start TLS");
        smtps.setStartTLS(true);
    }

    public void procConnClientList(ChannelHandlerContext ctx, ImSmtpSession smtps, String p_sCommand) {
        try {
            // 로컬에서만 허용
            if(ArrayUtils.indexOf(arrLocalhost, smtps.getPeerIP()) < 0){
            //if(!"127.0.0.1".equals(smtps.getPeerIP()) && !"0:0:0:0:0:0:0:1".equals(smtps.getPeerIP())) {
                smtpLogger.error("[procConnClientList] denied");
                throw new ImSmtpCloseException("500 5.5.2");
            }

            StringBuffer resultSB = new StringBuffer();
            int ipTotalCount = SmtpConnectService.getInstance().getSmtpConnectServer().size();
            int totalCount = 0;
            for( String ip : SmtpConnectService.getInstance().getSmtpConnectServer().keySet() ){
                int ipCount = SmtpConnectService.getInstance().getSmtpConnectServer().get(ip);
                totalCount += ipCount;
                if(resultSB.length() > 0){
                    resultSB.append("\r\n");
                }
                resultSB.append("* ").append(ip).append("(").append(ipCount).append(")");
            }

            sendClient(ctx, resultSB.toString());
            sendClient(ctx, "OK ConnClientList " + ipTotalCount+"("+totalCount + ") completed");
        } catch(ImSmtpException e){
            throw e;
        }catch(Exception ex){
            String errorId = ErrorTraceLogger.log(ex);
            smtpLogger.error("[{}] {} - [procConnClientList] (" + smtps.getPeerIP() + "/" + smtps.getCountry() + ") " , smtps.getTraceID(), errorId );
            throw new ImSmtpException("421 4.3.2");
        }
    }
    public void procJournalSend(ChannelHandlerContext ctx, ImSmtpSession smtps, String p_sCommand) {
        try{
            String[] arrMsg = p_sCommand.split(":");

            if( (smtps.getSmtpState() != ImSmtpSession.SMTP_STATE_HELO)
                    && (smtps.getSmtpState() != ImSmtpSession.SMTP_STATE_AUTH) ){
                throw new ImSmtpException("503 5.5.1");
            }

            if(arrMsg.length < 2){
                throw new ImSmtpException("501 5.5.5");
            }

            if(arrMsg[1] != null && !arrMsg[1].equals("")){
                String journalSend = arrMsg[1];
                if("true".equalsIgnoreCase(journalSend)){
                    smtps.setJournalSend(true);
                }else {
                    smtps.setJournalSend(false);
                }
            }

            String sRetMsg = "250 "+ arrMsg[1] +" JOURNALSEND OK";
            sendClient(ctx, sRetMsg);
        } catch(ImSmtpException e){
            throw e;
        } catch(Exception ex){
            String errorId = ErrorTraceLogger.log(ex);
            smtpLogger.error("[{}] {} - [procJournalSend] (" + smtps.getPeerIP() + "/" + smtps.getCountry() + ") " , smtps.getTraceID(), errorId );
            throw new ImSmtpException("503 5.5.1");
        }
    }

    public void procQueueCount(ChannelHandlerContext ctx, ImSmtpSession smtps, String p_sCommand) {
        try{
            // 로컬에서만 허용
            //if(!"127.0.0.1".equals(smtps.getPeerIP()) && !"0:0:0:0:0:0:0:1".equals(smtps.getPeerIP())) {
            if(ArrayUtils.indexOf(arrLocalhost, smtps.getPeerIP()) < 0){
                smtpLogger.error("[procQueueCount] denied");
                throw new ImSmtpException("500 5.5.2");
            }

            String[] arrMsg = p_sCommand.split(":");
            int nQueueSize = 0;

            if(arrMsg.length < 2){
                throw new ImSmtpException("501 5.5.2");
            }

            int QueueNum = Integer.parseInt(arrMsg[1]);
            MessageQueueService messageQueueService = MessageQueueService.getInstance();
            if(QueueNum > messageQueueService.getQueueSize() -1){
                throw new ImSmtpCloseException("458 4.5.0");
            }

            ImQueueObj queue = messageQueueService.getQueue(QueueNum);

            if(queue != null){
                nQueueSize = queue.getQueue().getSize();
            }else{
                throw new ImSmtpCloseException("458 4.5.0");
            }

            String sRetMsg = "250 "+ nQueueSize;
            sendClient(ctx, sRetMsg);
        } catch(ImSmtpException e){
            throw e;
        } catch(Exception ex){
            String errorId = ErrorTraceLogger.log(ex);
            smtpLogger.error("[{}] {} -[procQueueCount] (" + smtps.getPeerIP() + " / " + smtps.getFrom() + ") " , smtps.getTraceID(), errorId );
            throw new ImSmtpException("503 5.5.1");
        }
    }

    public void procThCount(ChannelHandlerContext ctx, ImSmtpSession smtps, String p_sCommand) {
        int nBulkAlive = 0;
        int nSendAlive = 0;
        int nReSendAlive = 0;
        int nReservSendAlive = 0;
        try {
            //if (!"127.0.0.1".equals(smtps.getPeerIP()) && !"0:0:0:0:0:0:0:1".equals(smtps.getPeerIP())) {
            if(ArrayUtils.indexOf(arrLocalhost, smtps.getPeerIP()) < 0){
                smtpLogger.error("[procThCount] denied");
                throw new ImSmtpCloseException("500 5.5.2");
            }

            int i = 0;
            ImSmtpThreadManager imSmtpThreadManager = ImSmtpThreadManager.getInstance();
            for (Thread th : imSmtpThreadManager.getBulkThreadList()) {
                if (th != null) {
                    if (th.isAlive()) {
                        nBulkAlive++;
                    } else {
                        smtpLogger.error("Bulk Send Thread " + th.getName() + ": Not Alive");
                    }
                } else {
                    smtpLogger.error("Bulk Send Thread " + i + ": Not Alive");
                }
                i++;
            }
            i = 0;
            for (Thread th : imSmtpThreadManager.getSendThreadList()) {
                if (th != null) {
                    if (th.isAlive()) {
                        nSendAlive++;
                    } else {
                        smtpLogger.error("Send Thread " + th.getName() + ": Not Alive");
                    }
                } else {
                    smtpLogger.error("Send Thread " + i + ": Not Alive");
                }
                i++;
            }
            i = 0;
            for (Thread th : imSmtpThreadManager.getResendThreadList()) {
                if (th != null) {
                    if (th.isAlive()) {
                        nReSendAlive++;
                    } else {
                        smtpLogger.error("Resend Thread " + th.getName() + ": Not Alive");
                    }
                } else {
                    smtpLogger.error("Resend Thread " + i + ": Not Alive");
                }
                i++;
            }
            i = 0;
            for (Thread th : imSmtpThreadManager.getReservThreadList()) {
                if (th != null) {
                    if (th.isAlive()) {
                        nReservSendAlive++;
                    } else {
                        smtpLogger.error("Reserve Thread " + th.getName() + ": Not Alive");
                    }
                } else {
                    smtpLogger.error("Reserve Thread " + i + ": Not Alive");
                }
                i++;
            }
            ImSmtpConfig smtpConfig = ImSmtpConfig.getInstance();
            sendClient(ctx, "SEND," + smtpConfig.getMaxSendTh() + "," + nSendAlive);
            sendClient(ctx, "BULK," + smtpConfig.getMaxBlukThread() + "," + nBulkAlive);
            sendClient(ctx, "RESEND," + smtpConfig.getReSendTh()+ "," + nReSendAlive);

            // 클라이언트에 성공 메시지를 보낸다.
            String sRetMsg = "250 " + ImSmtpConfig.getInstance().getHeloHost() + " THCOUNT [" + smtps.getPeerIP() + "]";
            sendClient(ctx, sRetMsg);
        } catch(ImSmtpException e){
            throw e;
        } catch(Exception ex){
            String errorId = ErrorTraceLogger.log(ex);
            smtpLogger.error("[{}] {} -[procThCount] (" + smtps.getPeerIP() + " / " + smtps.getFrom() + ") " , smtps.getTraceID(), errorId );
            throw new ImSmtpException("421 4.3.2");
        }
    }


    /**
     * NOOP 명령
     */
    public void procNoop(ChannelHandlerContext ctx, ImSmtpSession smtps, String p_sCommand) {
        // 클라이언트에 성공 메시지를 보낸다.
        String sRetMsg = "250 2.0.0 OK";
        sendClient(ctx, new ImSmtpRespMsg(smtps.getTraceID(), sRetMsg));
    }

    public void procRset(ChannelHandlerContext ctx, ImSmtpSession smtps, String p_sCommand) {
        if(!"127.0.0.1".equals(smtps.getPeerIP()) && smtps.getRsetCount() >= ImSmtpConfig.getInstance().getMaxRsetCount()){
            smtpLogger.info( "[{}] ip:{} too many rset: {}",smtps.getTraceID(),smtps.getPeerIP(), smtps.getRsetCount());
            throw new ImSmtpCloseException("421 4.3.2");
        } else {
            smtps.setRsetCount(smtps.getRsetCount()+1);
        }

        // session reset
        smtps.resetSession();

        // 클라이언트에 성공 메시지를 보낸다.
        String sRetMsg = "250 2.1.5 Reset state";
        sendClient(ctx, sRetMsg);
    }




}

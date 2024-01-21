package com.imoxion.sensems.server.nio.smtp.handler;

import com.imoxion.common.util.ImFileUtil;
import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.server.beans.ImFromMonBean;
import com.imoxion.sensems.server.config.ImServerPolicyConfig;
import com.imoxion.sensems.server.config.ImSmtpConfig;
import com.imoxion.sensems.server.exception.ImSmtpException;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.logger.event.TransmitLogger;
import com.imoxion.sensems.server.nio.ImSensSmtpApplication;
import com.imoxion.sensems.server.service.DnsSearchService;
import com.imoxion.sensems.server.service.FromMonService;
import com.imoxion.sensems.server.smtp.ImSmtpSession;
import com.imoxion.sensems.server.util.ImSecurityFilter;
import com.imoxion.sensems.server.util.ImSmtpSendData;
import com.imoxion.sensems.server.util.ImSmtpSendingInfo;
import com.imoxion.sensems.server.util.ImSmtpUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.WriteTimeoutException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Locale;

@RequiredArgsConstructor
public class ImSmtpDataHandler extends ChannelInboundHandlerAdapter {
    public static Logger smtpLogger = LoggerFactory.getLogger("SMTP");
    private final ImSmtpSession smtps;
    private final ChannelHandler baseHandler;

    private boolean isDataOK = false;
    private boolean isChannelInactive = false;
    private String sTempFilePath;
    //private StringBuffer sbData = new StringBuffer();
    //private List<ByteBuf> listMailDataByteBuf = new ArrayList<>();
//    private ByteBuf messageByteBuf;
    private AsynchronousFileChannel fileChannel;
    private BufferedOutputStream fos;
    private long filepos;

    private void sendClient(ChannelHandlerContext ctx, Object msg){
        ctx.writeAndFlush((String)msg + "\r\n");
    }
    private void sendClient(ChannelHandlerContext ctx, String msg){
        ctx.writeAndFlush(msg + "\r\n");
    }
    private void sendClient(ChannelHandlerContext ctx, ImSmtpException e){
        sendClient(ctx, e.getMessage());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        smtpLogger.info("[{}] ImSmtpDataHandler channelRegistered", smtps.getTraceID());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        smtpLogger.info("[{}] ImSmtpDataHandler channelUnregistered", smtps.getTraceID());
        // handler를 다시 교체
        ChannelPipeline cp = ctx.pipeline();
        cp.replace("datahandler", "basehandler", baseHandler);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        smtpLogger.info("[{}] ImSmtpDataHandler channelActive", smtps.getTraceID());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        isChannelInactive = true;
        smtpLogger.info("[{}] ImSmtpDataHandler channelInactive", smtps.getTraceID());
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(
                "E, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        smtpLogger.trace("[{}] ImSmtpDataHandler handlerAdded", smtps.getTraceID());
        // tempfile
        sTempFilePath = ImUtils.stripDirSlash(ImSmtpConfig.getInstance().getQueuePath())
                + File.separator+"temp"+File.separator+smtps.getMsgID() + ".sml.body";

        try {
            File mailFile = new File(sTempFilePath);

//            fileChannel = AsynchronousFileChannel.open(mailFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                fos = new BufferedOutputStream(new FileOutputStream(new File(sTempFilePath)));
        } catch(IOException e) {

            smtpLogger.error("Failed to open file", e);
            throw new ImSmtpException("458 4.5.0");
        }
        smtpLogger.info("[{}] ImSmtpDataHandler handlerAdded: tempfile - {}", smtps.getTraceID(), sTempFilePath);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        smtpLogger.info("[{}] ImSmtpDataHandler handlerRemoved - {}", smtps.getTraceID(), sTempFilePath);
        // delete sTempFilePath
        if(sTempFilePath != null && sTempFilePath.length() > 0){
            try {
                if (fos != null) fos.close();
            } catch(Exception e){}

            smtpLogger.info( "[{}] SMTP Spool tempfile delete : {} - {}", smtps.getTraceID(),
                    Files.deleteIfExists(Paths.get(sTempFilePath)), sTempFilePath);
//            smtpLogger.info( "[{}] SMTP Spool tempfile delete : {} ", smtps.getTraceID(), sTempFilePath);
        }

        // 비정상 종료되었을 경우
        if(!isDataOK && isChannelInactive){
            ctx.fireChannelUnregistered();
        }

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        smtpLogger.info("[{}] ImSmtpDataHandler userEventTriggered", smtps.getTraceID());
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE ) {
                sendClient(ctx, "Timeout Connection Closed");
                smtpLogger.debug("[{}][{}:{}] userEventTriggered IdleState ReadTimeout Connection closed", smtps.getTraceID(), smtps.getPeerIP(), smtps.getPeerPort());
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                sendClient(ctx, "Timeout Connection Closed");
                smtpLogger.debug("[{}][{}:{}] userEventTriggered IdleState WriteTimeout Connection closed", smtps.getTraceID(), smtps.getPeerIP(), smtps.getPeerPort());
                ctx.close();
            } else if (e.state() == IdleState.ALL_IDLE) {
                sendClient(ctx, "Timeout Connection Closed");
                smtpLogger.debug("[{}][{}:{}] userEventTriggered IdleState Read/WriteTimeout Connection closed", smtps.getTraceID(), smtps.getPeerIP(), smtps.getPeerPort());
                ctx.close();
            }
        }
    }

    /**
     * 라인이 '.'으로 시작하는 경우 첫번째 '.'을 제거
     */
    private ByteBuf processLine(ByteBuf line) {
        //if(line.readableBytes() >= 1 && line.getByte(0) == '.') {
        if(line.readableBytes() >= 1 && line.getByte(line.readerIndex()) == '.') {
            if(line.readableBytes() > 1) {
                line.readByte(); // 라인의 시작이 '.' 이면 제거
                return line.copy();
            }
        }

        return line;
    }

    public static ByteBuffer toNioBuffer(ByteBuf buffer) {
        if (buffer.isDirect()) {
            return buffer.nioBuffer();
        }
        final byte[] bytes = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), bytes);
        return ByteBuffer.wrap(bytes);
    }

    /** 발송 구분 - T: 테스트메일, D: DB연동외부메일(WEB API이용), A: Smtp 인증, R: 릴레이연동, C:건별재발신 **/
    private String getSendType(ImSmtpSendingInfo sendInfo, ImSmtpSession smtps){
        String sendType = sendInfo.getSend_type();
        if(StringUtils.isNotEmpty(sendType)){
            return sendType;
        }

        if(smtps.isRelay()) {
            if (StringUtils.isNotEmpty(smtps.getLogonUser())) {
                sendType = "A"; // smtp인증
            } else {
                sendType = "R"; // 릴레이
            }
        } else {
            sendType = "T"; // 애매한 경우라서 그냥 T 로 입력
        }

        return sendType;
    }

    private boolean commitMessage(ChannelHandlerContext ctx, long lMsgSize){
    	String sAuthenticationResult = null;

        if(StringUtils.isEmpty(sTempFilePath)) {
            sTempFilePath = ImUtils.stripDirSlash(ImSmtpConfig.getInstance().getQueuePath())
                    + File.separator + "temp" + File.separator + smtps.getMsgID() + ".sml.body";
        }
        int nIsRelay = 0;
        //String sAuthenticationResult = null;
        
        if(smtps.isError()){
            
            // 수신한 메시지 파일을 지운다.
            ImFileUtil.deleteFile(sTempFilePath);
            return false;
        }


        // 대용량 큐 분리를 위해 한계 크기정보를 가져온다.
        // 메일 한통의 크기가 설정값보다 크면 벌크큐로 저장(LIMIT_BULK_MAIL_SIZE - "004")
        ImServerPolicyConfig policyConfig = ImServerPolicyConfig.getInstance();
        String limitBigSize = policyConfig.get(ImServerPolicyConfig.LIMIT_BULK_MAIL_SIZE);
        long lLimitBigSize = 0;
        try{
            // 대용량 큐로 전환 한계 용량을 가져온다.
            lLimitBigSize = Long.parseLong(limitBigSize) * 1024 * 1024;
        }catch(NumberFormatException e){}

        // 현재 한계 용량에 도달했는지 확인한다. DB 설정 정보는 kbytes 이기 때문에 1024를 곱한다.
        if(lLimitBigSize != 0 && lLimitBigSize < lMsgSize){
            smtps.setBulk(true);
        }
        //smtpLogger.info("LIMIT_BULK_MAIL_SIZE: {}, MsgSize: {}, isBulk: {}", lLimitBigSize, lMsgSize, smtps.isBulk());
        // 헤더 정보를 뽑아온다.
        //String[] sSendingInfo = ImSmtpUtil.getSendingInfo(sTempFilePath);
        ImSmtpSendingInfo sendInfo = ImSmtpUtil.getSendingInfo(sTempFilePath, smtps.getPeerIP());
        if( sendInfo == null ){
            smtpLogger.error( "[{}] SMTP Message Header Parse Error - user:{}",smtps.getTraceID(),smtps.getLogonEmailID());
            throw new ImSmtpException("458 4.5.0");
        }


        // 대용량 처리(벌크큐) 여부를 확인한다.(FromMonThread가 30초마다 한번씩 체크하기 때문에
        // 반드시 지정한 건수나 사이즈와 일치하지 않고 약간의 오차는 발생한다.
        String sFrom = "";
		// from 메일 주소를 모니터링한다.
		if(smtps.getFrom() == null || smtps.getFrom().equals("")){
			sFrom = sendInfo.getFrom();
		}else{
			sFrom = smtps.getFrom();
		}
		sFrom = ImStringUtil.getStringBetween(sFrom,"<",">");
        ImFromMonBean uBean = null;
        if(FromMonService.getInstance().getMapFromMon() != null){
            uBean = FromMonService.getInstance().getMapFromMon().get(sFrom);
        }
        if(uBean != null ){
            // 해당 From 주소가 대용량 처리로 되어있는지 확인한다.
            if(uBean.isBulk())
                smtps.setBulk(true);
        }

        // 릴레이 가능 여부를 가져온다.
        if(smtps.isRelay()){
            nIsRelay = 1;
        }        

        try{
            // tls 버전과 cipher 기록
            String useTls = "";
            if(smtps.isStartTLS()) {
                try {
                    SslHandler sslhandler = (SslHandler) ctx.channel().pipeline().get("sslHandler");
                    SSLSession session = sslhandler.engine().getSession();
                    useTls = "\r\n\t(version="+session.getProtocol()+" cipher="+ session.getCipherSuite() + ")";
                }catch(Exception e){}
            }

            String mailFrom = ImStringUtil.getStringBetween(smtps.getFrom(), "<", ">");
            
            String isrelayOkStr = "";
            if( nIsRelay == 1 || smtps.isAuth() ) {
                isrelayOkStr = "(permitted)";
            }

			int fromFilterCheckResult = 0;
			// 릴레이허용이나 인증 발송인경우는 발신자 필터를 사용하지 않는다.
			if( nIsRelay == 0 && !smtps.isAuth() ) {
			//if(!"127.0.0.1".equals(smtps.getPeerIP()) && !smtps.isAuth() ) {
				try {
					// ImSecurityFilter ---------
					ImSecurityFilter isf = null;
					if(ImSmtpConfig.getInstance().isUseDmarcCheck()) {
						isf = new ImSecurityFilter();
						// fromEmail : header from
						String fromEmail = sendInfo.getFrom();
						fromEmail = ImStringUtil.getStringBetween(fromEmail, "<", ">");
						String fromIp = sendInfo.getFromIP() != null ? sendInfo.getFromIP() : smtps.getPeerIP();
						sendInfo.setFromIP(fromIp);
						
						DnsSearchService ids = DnsSearchService.getInstance();

						isf.verifyDMARC(ids, fromEmail, sendInfo, sTempFilePath, smtps);
						
						sAuthenticationResult = isf.getAuthResult();
						smtpLogger.info( "[{}][{}][{}] DMARC FILTER Check - From IP: {} / Header From: {} / Smtp From: {} / SPF: {} / DKIM: {} / DMARC: {} / To: {}",
                                smtps.getTraceID(), smtps.getPeerIP(), smtps.getPeerPort(), fromIp, fromEmail, mailFrom, isf.isSPF(), isf.isDKIM(), isf.isDMARC(), smtps.getArrRcpt());

					}

				} catch (Exception e) {
					String errorId = ErrorTraceLogger.log(e);
					smtpLogger.error( "[{}] {} - From Filter Error", smtps.getTraceID(), errorId);
				}
			}

            String sendType = getSendType(sendInfo, smtps);
            smtpLogger.info( "[{}] Smtp Send Type: {}", smtps.getTraceID(), sendType);

            for(int i=0; i< smtps.getArrRcpt().size() ; i++){
    			//StringBuffer bfInput = new StringBuffer();
    			StringBuffer bfHeader = new StringBuffer();
    			String sRcpt = ImStringUtil.getStringBetween(smtps.getArrRcpt().get(i).toString(),"<",">");
    			
    			ImSmtpSendData issd = new ImSmtpSendData();
    			issd.setTraceID(smtps.getTraceID());
    			issd.setMsgID(smtps.getMsgID());
    			issd.setFrom(mailFrom);
    			issd.setRcptto(sRcpt);
    			issd.setPeerIP(smtps.getPeerIP());
    			issd.setDomain(sendInfo != null ? sendInfo.getDomain():"");
    			issd.setMailKey(sendInfo != null ? sendInfo.getMailKey():"");
    			issd.setReserveTime(sendInfo != null ? sendInfo.getReserveTime():"");
    			issd.setLocalMsgid(smtps.getLocalMsgid());
    			issd.setLocalDomain(smtps.getLocalDomain());
    			issd.setLocalUserid(smtps.getLocalUserid());
    			issd.setIsRelay(nIsRelay);
    			issd.setBulk(smtps.isBulk());
    			issd.setLogonDomain(smtps.getLogonDomain());
    			issd.setLogonUser(smtps.getLogonUser());
				issd.setSubject(sendInfo.getSubject());
				issd.setSenddate(sendInfo.getSenddate());
				issd.setMailsize(lMsgSize);
				// 그룹키
				issd.setGroupKey(sendInfo.getGroupKey());
				// 수신자별 고유키
                issd.setRcptKey(sendInfo.getRcptKey());
                // 현재 재발송은 아님
                issd.setRetryNow(false);
                // send_type
                issd.setSend_type(sendType);

				// fromIP 를 구하지 못한 메일은 smtp 에 접속한 IP 를 사용한다.
				if( StringUtils.isEmpty( sendInfo.getFromIP() ) ){
					issd.setFromIP( smtps.getPeerIP() );
				} else {
				    // X-ORIGINATING-SPRXY-IP 값이 있고 peerIP 와 다르면
				    if(!smtps.getPeerIP().equals(sendInfo.getFromIP())){
				        smtps.setFromIP(sendInfo.getFromIP());
                    }
                }

    			if(sendInfo != null ){
					if( sendInfo.getReciptKey() != null )
						issd.setReceiptKey(sendInfo.getReciptKey());

					if(sendInfo.getAhost() != null)
						issd.setAhost(sendInfo.getAhost());

					if(sendInfo.getUserid() != null)
						issd.setUserid(sendInfo.getUserid());

					if(sendInfo.getTbl_no() != null)
						issd.setTbl_no(sendInfo.getTbl_no());

					if(sendInfo.getPart_no() != null)
						issd.setPart_no(sendInfo.getPart_no());

					if(sendInfo.getXmailer() != null)
						issd.setXmailer(sendInfo.getXmailer());

					if(sendInfo.getFromIP() != null){
						issd.setFromIP(sendInfo.getFromIP());
					}
				}

				// Return-Path 추가(RfC 2821 : smtp상의 mail from)
				bfHeader.append("Return-Path: ")
					.append(smtps.getFrom()).append("\r\n")
					.append("X-SensTrace: ").append(smtps.getTraceID()).append("\r\n")
                	.append("Received: from ")
					.append(smtps.getFromDomain())
					.append(" (")
					.append(smtps.getPeerIP())
					.append(")").append(isrelayOkStr).append("\r\n")
					.append("\tby ")
					.append(ImSmtpConfig.getInstance().getRootDomain())
					.append(" with ")
					//.append("ESMTP ")
					.append(ImSensSmtpApplication.SMTP_SERVER_VERSION)
					.append("\r\n")
					.append("\tid <")
					.append(smtps.getMsgID())
					.append(">");
				//if(smtps.getCurrRcpt() == 1){
					bfHeader.append(" for <")
						.append(smtps.getArrRcpt().get(i))
						.append(">");
				//} 
				bfHeader.append(" from <")
					.append(smtps.getFrom())
					.append(">");
				if(StringUtils.isNotEmpty(smtps.getLogonUser())){
					String logonUser = smtps.getLogonUser();
					if(StringUtils.isNotEmpty(smtps.getLogonDomain())) {
						logonUser = logonUser + "@" + smtps.getLogonDomain();
					}
					bfHeader.append(" authenticated with <")
						.append(ImStringUtil.maskString(logonUser, 2, 2, "***"))
						.append(">");
				}
				bfHeader.append(useTls).append(";\r\n")
					.append("\t")
					.append(smtps.getConnDate())
					.append("\r\n");

				if(StringUtils.isNotEmpty(sAuthenticationResult)){
					bfHeader.append(sAuthenticationResult).append("\r\n");
				}


				String sHeader = bfHeader.toString();

                // from 메일 주소를 모니터링한다.
                FromMonService.getInstance().addFromMon(sFrom, lMsgSize);

				//if(!sSendingInfo[2].equals("")){				
				if(smtps.isBulk()){
					//smtpLogger.info("[{}] In Bulk Queue  ( {}:{} / {} / {} -> {} / {} ) OK", smtps.getTraceID(), smtps.getPeerIP(), smtps.getPeerPort(), smtps.getMsgID(), smtps.getFrom(), sRcpt, lMsgSize);
					smtpLogger.info("[{}] In Bulk Queue - (ip:{}, mailfrom:{} -> rcptto:{}, size:{}, msgid:{}, groupkey:{}, rcptkey:{}) OK",
                            smtps.getTraceID(), smtps.getPeerIP(), smtps.getFrom(), sRcpt, ImUtils.byteFormat(lMsgSize,2) , smtps.getMsgID(), sendInfo.getGroupKey(), sendInfo.getRcptKey());
					String sUid = ImSmtpUtil.addQueueBulk( issd, sHeader,  smtps.getMsgID(), sendInfo);
				}else{
					//smtpLogger.info("[{}] In Normal Queue ( {}:{} / {} / {} -> {} / {} ) OK",  smtps.getTraceID(), smtps.getPeerIP(), smtps.getPeerPort(), smtps.getMsgID(), smtps.getFrom(), sRcpt, lMsgSize);
					smtpLogger.info("[{}] In Normal Queue - (ip:{}, mailfrom:{} -> rcptto:{}, size:{}, msgid:{}, groupkey:{}, rcptkey:{}) OK", smtps.getTraceID(),
                            smtps.getPeerIP(), smtps.getFrom(), sRcpt, ImUtils.byteFormat(lMsgSize,2) , smtps.getMsgID(), sendInfo.getGroupKey(),  sendInfo.getRcptKey());
					String sUid = ImSmtpUtil.addQueue2( issd, sHeader, smtps.getMsgID());
				}
				
				TransmitLogger transmitLogger = new TransmitLogger();
//				transmitLogger.setServerid(ImSmtpConfig.getInstance().getServerID());
//                // 추적 ID를 남긴다.
//                transmitLogger.setTraceid(smtps.getTraceID());
//                transmitLogger.setWork(TransmitLogger.WORK_RECEIVE);
//                transmitLogger.setIp(smtps.getPeerIP());
//                transmitLogger.setFrom(smtps.getFrom());
                transmitLogger.setTo(sRcpt);
                transmitLogger.setSubject(sendInfo.getSubject());
                transmitLogger.setSize(lMsgSize);
                transmitLogger.setGroupkey(sendInfo.getGroupKey());
                transmitLogger.setRcptkey(sendInfo.getRcptKey());
                transmitLogger.setSend_type(sendType);
                if( smtps.isBulk() ){
                    transmitLogger.setDescription("info.receive.bulk.success");
                }else{
                    transmitLogger.setDescription("info.receive.normal.success");
                }
//                transmitLogger.info();
                transmitLogger.setResultState(TransmitLogger.STATE_ING);
                ImSmtpUtil.doTransmitLogIns(transmitLogger, smtps);
                //smtpLogger.info("[{}] {} Rcpt To / Data ( {} / {} -> {} / {} ) OK", smtps.getTraceID(), smtps.getPeerIP(), smtps.getMsgID(), smtps.getFrom(), sRcpt , lMsgSize );
	        }
        }catch(Exception ex){
            ImFileUtil.deleteFile(sTempFilePath);
            String errorId = ErrorTraceLogger.log(ex);
            smtpLogger.error("[{}] {} - [SMTP] DATA - commitMessage ( {} )",smtps.getTraceID(), errorId,smtps.getPeerIP() );
        }
        return true;
    }

//    private void writeTempFile(AsynchronousFileChannel fileChannel) throws IOException {
//        Path path = Paths.get(sTempFilePath);
//        fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
//        /*CompletionHandler handler = new CompletionHandler() {
//            @Override
//            public void completed(Object result, Object attachment) {
//
//                System.out.println(attachment + " completed and " + result + " bytes are written.");
//            }
//            @Override
//            public void failed(Throwable e, Object attachment) {
//
//                System.out.println(attachment + " failed with exception:");
//                e.printStackTrace();
//            }
//        };*/
//
//        fileChannel.write(toNioBuffer(messageByteBuf), 0);
//        fileChannel.close();
//    }

    private byte[] byteBufToBytes(ByteBuf buf){
        byte[] bytes;
        int offset;
        int length = buf.readableBytes();

        if (buf.hasArray()) {
            bytes = buf.array();
            offset = buf.arrayOffset();
        } else {
            bytes = new byte[length];
            buf.getBytes(buf.readerIndex(), bytes);
            offset = 0;
        }

        return bytes;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        smtpLogger.info("channelRead");
//        System.out.println("0000");
        ByteBuf line = Unpooled.wrappedBuffer((byte[]) msg);
//        System.out.println("1111");

        /*if(bbLine.readableBytes() >= 1 && bbLine.getByte(0) == '.') {
            //   if(line.readableBytes() >= 1 && line.getByte(line.readerIndex()) == '.') {
            if(bbLine.readableBytes() > 1) {
                smtpLogger.info("bbline00 == {} : {}", bbLine.readableBytes(), bbLine.toString(Charset.defaultCharset()));
                bbLine.readByte(); // 라인의 시작이 '.' 이면 제거
            }
            smtpLogger.info("bbline == {} : {}", bbLine.readableBytes(), bbLine.toString(Charset.defaultCharset()));
        }*/
        // 만약 data가 끝에 다다르면 라인이 점(.)하나만 옴, 끝이 아닌경우에는 점점(..)으로 들어옴
        int lineLen = line.readableBytes();
        ByteBuf bbLine = processLine(line);

//        System.out.println("2222");
        // 라인이 점(.) 하나이면 끝에 다다른거임
        if(lineLen == 1 && bbLine.readableBytes() == 1 && bbLine.getByte(bbLine.readerIndex()) == '.' ) {
//            System.out.println("3333");
            // file 로 기록
            try {
//                fileChannel.close();
                fos.close();
            } catch (IOException e) {
                smtpLogger.error("failed to close file {}", e);
                throw new ImSmtpException("421 4.3.2");
            }

            // data 처리
            long msgSize = ImFileUtil.getFileSize(sTempFilePath);

            // 한통당 메일크기가 설정값보다 크거나, 수신자수*한통 크기가 전체 크기 제한값보다 크면
            if( (ImSmtpConfig.getInstance().getMaxMsgSize() != 0 && ImSmtpConfig.getInstance().getMaxMsgSize() < msgSize)
                    || (ImSmtpConfig.getInstance().getTotMaxMsgSize() != 0 && ImSmtpConfig.getInstance().getTotMaxMsgSize() < (long)(smtps.getCurrRcpt() * msgSize))){
                TransmitLogger transmitLogger = new TransmitLogger();
//                transmitLogger.setServerid(ImSmtpConfig.getInstance().getServerID());
//                transmitLogger.setTraceid(smtps.getTraceID());
//                transmitLogger.setWork(TransmitLogger.WORK_RECEIVE);
//                transmitLogger.setIp(smtps.getPeerIP());
//                transmitLogger.setFrom(smtps.getFrom());
                transmitLogger.setTo(smtps.getArrRcpt().get( smtps.getCurrRcpt() - 1));
                transmitLogger.setSize(msgSize);
                transmitLogger.setDescription("error.max_messagesize");
//                transmitLogger.info();
                transmitLogger.setResultState(TransmitLogger.STATE_FAIL);
                ImSmtpUtil.doTransmitLog(transmitLogger, smtps);

                smtpLogger.info( "[{}] Data (" + smtps.getPeerIP() + " / " + smtps.getFrom() +" / " + smtps.getCurrRcpt() + " rcpts / " + msgSize +  " Byte) message size too big.",smtps.getTraceID());
                //sendClient(smtps.getSocket(),"552 Message exceeds fixed maximum message size");

//                String sDefaultDomain = ImSmtpConfig.getInstance().getDefaultDomain();
//                String sMailerDaemon = "mailer-daemon@"+sDefaultDomain;
//                for(String sRcpt : smtps.getArrRcpt() ){
//                    // 수신자에게 리턴메일을 전송한다.
//                    ImSmtpSendData sd = new ImSmtpSendData();
//                    sd.setTraceID(smtps.getTraceID());
//                    sd.setFrom(ImStringUtil.getStringBetween(smtps.getFrom(),"<",">"));
//                    sd.setRcptto(sRcpt);
//
//                    if(!sd.isRedirect())
//                        ImSmtpUtil.sendNotifyReceiverErrorMessage(sd, sTempFilePath,sMailerDaemon, sRcpt,
//                                "Maximum Message size"
//                                ,"Message exceeds fixed maximum message size "
//                                        +(ImSmtpConfig.getInstance().getMaxMsgSize()/1024/1024)+" Mbytes" );
//                }

                throw new ImSmtpException("552 5.2.3");
            }

            if (!commitMessage(ctx, msgSize)) {
                return;
            }

            smtpLogger.info("[{}] DATA OK - (" + smtps.getPeerIP() + " / " + smtps.getFrom() + "), RcptCount: {}, <{}>" , smtps.getTraceID(), smtps.getArrRcpt().size(), smtps.getMsgID());

            isDataOK = true;
            // 파이프라인에서 핸들러 다시 교체
            ChannelPipeline cp = ctx.pipeline();
            cp.replace("datahandler", "basehandler", baseHandler);

            //
            String sRetMsg = "250 2.0.0 OK <" + smtps.getMsgID() + "> Message accepted for delivery";
            sendClient(ctx, sRetMsg);
            smtps.resetSession();
        } else {
            //smtpLogger.info("[{}] READ");
           // ByteBuf lineWithoutCRLF;
            // 라인의 끝에 줄바꿈이 없으면 추가
            if(bbLine.getByte(bbLine.readerIndex()) != '\r'){
                //lineWithoutCRLF = Unpooled.copiedBuffer(bbLine).writeByte('\r').writeByte('\n');
                ////fos.write((byte[]) msg);
                //smtpLogger.info("bbline1 ==  {} : {}", bbLine.readableBytes(), bbLine.toString(Charset.defaultCharset()));
//                String sbb = bbLine.toString(Charset.defaultCharset());
//                if(bbLine.toString(Charset.defaultCharset()).startsWith(".")){
//                    smtpLogger.info("bbline1 ==  {} : {}", bbLine.readableBytes(), bbLine.toString(Charset.defaultCharset()));
//                    //fos.write(sbb.getBytes());
//                    fos.write(byteBufToBytes(bbLine));
//                } else {
//                    fos.write(byteBufToBytes(bbLine));
//                }
                fos.write(byteBufToBytes(bbLine));
                fos.write('\r');
                fos.write('\n');
            } else {
                //lineWithoutCRLF = Unpooled.copiedBuffer(bbLine);
                //fos.write((byte[]) msg);
               //smtpLogger.info("bbline2 == {} : {}", bbLine.readableBytes(), bbLine.toString(Charset.defaultCharset()));
                fos.write(byteBufToBytes(bbLine));
            }
            //lineWithoutCRLF.readBytes(fos, lineWithoutCRLF.readableBytes());
        }

/*

        if(bbLine.readableBytes() == 1 && bbLine.getByte(bbLine.readerIndex()) == '.') {
            System.out.println("3333");
            // file 로 기록
            AsynchronousFileChannel fileChannel = null;
            try {
                System.out.println("-------------------- writeFile start");
                writeTempFile(fileChannel);

            } catch(IOException e) {
                smtpLogger.error("Failed to open file", e);
                throw new ImSmtpException("458 4.5.0");
            } finally {
                try { if(fileChannel != null) fileChannel.close(); } catch (IOException e) {}
                System.out.println("-------------------- writeFile end");
            }

            // data 처리
            long msgSize = ImFileUtil.getFileSize(sTempFilePath);

            // 한통당 메일크기가 설정값보다 크거나, 수신자수*한통 크기가 전체 크기 제한값보다 크면
            if( (ImSmtpConfig.getInstance().getMaxMsgSize() != 0 && ImSmtpConfig.getInstance().getMaxMsgSize() < msgSize)
                    || (ImSmtpConfig.getInstance().getTotMaxMsgSize() != 0 && ImSmtpConfig.getInstance().getTotMaxMsgSize() < (long)(smtps.getCurrRcpt() * msgSize))){
                TransmitLogger transmitLogger = new TransmitLogger();

                transmitLogger.setTraceid(smtps.getTraceID());
                transmitLogger.setWork(TransmitLogger.WORK_RECEIVE);
                transmitLogger.setIp(smtps.getPeerIP());
                transmitLogger.setFrom(smtps.getFrom());
                transmitLogger.setTo(smtps.getArrRcpt().get( smtps.getCurrRcpt() - 1));
                transmitLogger.setSize(msgSize);
                transmitLogger.setDescription("error.max_messagesize");
                transmitLogger.info();

                smtpLogger.info( "[{}] Data (" + smtps.getPeerIP() + " / " + smtps.getFrom() +" / " + smtps.getCurrRcpt() + " rcpts / " + msgSize +  " Byte) message size too big.",smtps.getTraceID());
                //sendClient(smtps.getSocket(),"552 Message exceeds fixed maximum message size");

                String sDefaultDomain = ImSmtpConfig.getInstance().getDefaultDomain();
                String sMailerDaemon = "mailer-daemon@"+sDefaultDomain;
                for(String sRcpt : smtps.getArrRcpt() ){
                    // 수신자에게 리턴메일을 전송한다.
                    ImSmtpSendData sd = new ImSmtpSendData();
                    sd.setTraceID(smtps.getTraceID());
                    sd.setFrom(ImStringUtil.getStringBetween(smtps.getFrom(),"<",">"));
                    sd.setRcptto(sRcpt);

                    if(!sd.isRedirect())
                        ImSmtpUtil.sendNotifyReceiverErrorMessage(sd, sTempFilePath,sMailerDaemon, sRcpt,
                                "Maximum Message size"
                                ,"Message exceeds fixed maximum message size "
                                        +(ImSmtpConfig.getInstance().getMaxMsgSize()/1024/1024)+" Mbytes" );

                }

                throw new ImSmtpException("552 5.2.3");
            }

            ImFilterService smtpFilterService = ImFilterService.getInstance();
            for(ISMTPProcessFilter filter : smtpFilterService.getDataFilter() ){
                if(filter != null){
                    if(!filter.doProcess(smtps)){
                        return;
                    }
                }
            }

            if (!commitMessage(ctx, msgSize)) {
                return;
            }

            smtpLogger.info("[{}] DATA OK - (" + smtps.getPeerIP() + " / " + smtps.getFrom() + "), RcptCount: {}" , smtps.getTraceID(), smtps.getArrRcpt().size());
            smtps.initSession();

            //
            String sRetMsg = "250 2.0.0 OK <" + smtps.getTraceID() + "> Message accepted for delivery";
            sendClient(ctx, sRetMsg);

            // 파이프라인 다시 교체
            ChannelPipeline cp = ctx.pipeline();
            cp.replace("datahandler", "basehandler", baseHandler);
        } else {
            System.out.println("4444");
            //smtpLogger.info("[{}] READ");

            ByteBuf lineWithoutCRLF;
            if(bbLine.getByte(bbLine.readerIndex()) != '\r'){
                lineWithoutCRLF = Unpooled.copiedBuffer(bbLine).writeByte('\r').writeByte('\n');
            } else {
                lineWithoutCRLF = Unpooled.copiedBuffer(bbLine);
            }

            if(messageByteBuf == null) messageByteBuf = Unpooled.copiedBuffer(lineWithoutCRLF);
            else messageByteBuf = Unpooled.copiedBuffer(messageByteBuf, lineWithoutCRLF);
            //listMailDataByteBuf.add(lineWithoutCRLF);
            System.out.println("5555");
        }
*/


    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        smtpLogger.info("[{}] ImSmtpDataHandler channelReadComplete", smtps.getTraceID());
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if( cause instanceof IOException) {
            String errorId = ErrorTraceLogger.log(cause);
            smtpLogger.error("[{}] {} IOException", smtps.getTraceID(), errorId);
            this.channelUnregistered(ctx);
        } else if( cause instanceof ReadTimeoutException) {
            String errorId = ErrorTraceLogger.log(cause);
            smtpLogger.error("[{}] {} ReadTimeoutException", smtps.getTraceID(), errorId);
            this.channelUnregistered(ctx);
            //ImSmtpServer.getInstance().closeConnection(ctx, smtps);
        } else if( cause instanceof WriteTimeoutException) {
            String errorId = ErrorTraceLogger.log(cause);
            smtpLogger.error("[{}] {} WriteTimeoutException", smtps.getTraceID(), errorId);
            this.channelUnregistered(ctx);
            //ImSmtpServer.getInstance().closeConnection(ctx, smtps);
        } else if( cause instanceof ImSmtpException) {
            smtpLogger.error("[{}] ImSmtpException, error code : {}, message : {}", smtps.getTraceID(),
                    ( (ImSmtpException) cause ).getErrorCode(), cause.getMessage());
            sendClient(ctx, ((ImSmtpException) cause).getMessage());
            this.channelUnregistered(ctx);
        } else {
            String errorId = ErrorTraceLogger.log(cause);
            smtpLogger.error("[{}] {} exception: {}", smtps.getTraceID(), errorId, cause.getMessage());
            this.channelUnregistered(ctx);
            //ImSmtpServer.getInstance().closeConnection(ctx, smtps);
        }

        //ctx.close();
    }


}

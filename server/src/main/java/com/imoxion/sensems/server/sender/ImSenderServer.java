package com.imoxion.sensems.server.sender;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.common.io.ImCRLFTerminatedReader;
import com.imoxion.common.net.ImServerSocket;
import com.imoxion.common.thread.ImBlockingQueue;
import com.imoxion.common.util.ImFileUtil;
import com.imoxion.sensems.server.config.ImEmsConfig;
import com.imoxion.sensems.server.domain.ImbEmsMain;
import com.imoxion.sensems.server.environment.SensEmsEnvironment;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.logger.LoggerLoader;
import com.imoxion.sensems.server.service.DnsSearchService;
import com.imoxion.sensems.server.service.ImDkimSigner;
import com.imoxion.sensems.server.service.TLSFailHostService;
import com.imoxion.sensems.server.util.ImDkimSignerOld;
import com.imoxion.sensems.server.util.ImMessQueue;
import com.imoxion.sensems.server.util.UUIDService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ImSenderServer {
    private static Logger transferLogger = LoggerFactory.getLogger("TRANSFER");
    private static Logger senderLogger = LoggerFactory.getLogger("SENDER");
    private static ImSenderServer senderServer;
    private static ImBlockingQueue m_bqmsg;

    private static ConcurrentHashMap<String, ImbEmsMain> mapSending = new ConcurrentHashMap<String, ImbEmsMain>();

    public static String VERSION = "10.0";
    public static String VERSION_DETAIL = "10.0";
    public static String BUILD_DATE = "";

    private int serviceType;
    private static int m_nSend = 0;
    private static ImServerSocket serverSocket = null;

    public synchronized static ImSenderServer getInstance(){
        if( senderServer == null ){
            senderServer = new ImSenderServer();
            m_bqmsg = new ImBlockingQueue();
        }
        return senderServer;
    }

    private ImSenderServer(){}

    // -----------------------------------------------------------------------------------------------------------------

    public void addQueue(String msgpath) {
        m_bqmsg.enqueue(msgpath);
    }

    public Object extractQueue() throws InterruptedException {
        return m_bqmsg.dequeue();
    }

    public void putSending(String msgid, ImbEmsMain emsMain) {
        mapSending.putIfAbsent(msgid, emsMain);
        senderLogger.debug("putSending - {}", msgid);
    }

    public void removeSending(String msgid) {
        ImbEmsMain emsMain = mapSending.remove(msgid);
        senderLogger.debug("removeSending - {}", msgid);
    }

    /**
     * 입력한 msgid의 메일이 현재 발송 중인지 체크
     *
     * @param msgid
     * @return
     */
    public ImbEmsMain getSending(String msgid) {
        ImbEmsMain emsMain = mapSending.get(msgid);
        return emsMain;
    }



    /**
     * option
     */
    public static void main(String[] args) {

        SensEmsEnvironment.init();

        // System.out.println 로그를 logback 의 CONSOLE 로그에 찍히도록 수정
        //SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        LoggerLoader.initLog("emslog.xml");
        ImDatabaseConnectionEx.init("sensems.home", "mybatis-config.xml");

        ImEmsConfig emsConfig = ImEmsConfig.getInstance();
        transferLogger.info( ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        transferLogger.info( "SENSEMS CONFIG: {}", emsConfig.toString());
        transferLogger.info( "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        ImSenderServer.getInstance();

        int maxSendTh = emsConfig.getSenderThreadOutCount();

        // shutdownhook
//        Runtime.getRuntime().addShutdownHook(new ImSenderServer.SenderShutdownHook(Thread.currentThread()));

        // DKIM
        ImDkimSignerOld.getInstance();

        // dns 서버 초기화
        DnsSearchService.getInstance();

        ImSenderThreadManager senderThreadManager = ImSenderThreadManager.getInstance();

        Thread thread = new ImSenderTraceThread();
        thread.setName("TraceThread");
        thread.start();
        senderThreadManager.addThread(ImSenderThreadManager.ETC, thread);

        // dkim, limit_info, tls_fail_host reload
        ImSenderServer.ReloadConfThread rcth = new ImSenderServer.ReloadConfThread();
        rcth.setName("ReloadConfTh");
        rcth.start();
        senderThreadManager.addThread(ImSenderThreadManager.ETC, rcth);

        ArrayList vMsg = ImMessQueue.loadQueue(ImEmsConfig.getInstance().getQueuePath());
        if (vMsg != null) {
            for (int i = 0; i < vMsg.size(); i++) {
                String quefile = (String)vMsg.get(i);
                senderLogger.info(i + " - loadQueue : {}", quefile);
                if(ImFileUtil.getFileSize(quefile) <= 0){
                    ImFileUtil.deleteFile(quefile);
                    senderLogger.info("loadQueue error : file is empty - {}", quefile);
                    continue;
                }
                senderLogger.info(i + " - quefile : {}", quefile);
                try {
                    m_bqmsg.enqueue(quefile);
                } catch (Exception e) {}
            }
            vMsg.clear();
            vMsg = null;
        }

        for (int j = 0; j < maxSendTh; j++) {
            Thread th = new ImSenderSendThread();
            th.setName("SendTh-" + j);
            th.start();
            senderThreadManager.addThread(ImSenderThreadManager.SEND, th);
            transferLogger.info("{} Started", th.getName());
        }

        senderLogger.info("Load Thread OK");

        int port = emsConfig.getSenderInboundPort();
        int nThread = emsConfig.getSenderThreadInCount();

        serverSocket = new ImServerSocket(port, nThread, "SndrConnTh",
                new ImEMSSenderConnectClient(),
                new ImServerSocket.Death() {
                    public void on_exit(Exception e) {
                        System.out.println(
                                "exit processed correctly, null=" + e);
                    }
                }
        );

        serverSocket.start();

        transferLogger.info("ImSenderServer Started");
    }

    private static class ReloadConfThread extends Thread {

        public ReloadConfThread(){
            setDaemon(true);
        }


        public void run(){
            try {
                int cnt = 0;
                Thread.sleep(3000);
                while(!isInterrupted()) {

                    ImDkimSigner.getInstance("SENDER").load();


                    // proxydomain
    				/*String proxyPath = SensProxyEnvironment.getSensProxyServerHome()+"/conf/proxydomain.xml";
    				File pfile = new File(proxyPath);
    				if(pfile.exists()) {
    					long ptime = pfile.lastModified();
	    				if(ImSmtpServer.m_lProxyConfTime != ptime) {
	    					ImSmtpConfig.getInstance().setProxyDomainList(ProxyDomainBean.loadProxyDomainList(proxyPath));
	    					smtpLogger.debug("Reload ProxyDomain List: {}", ImSmtpConfig.getInstance().getProxyDomainList().size());
	    					ImSmtpServer.m_lProxyConfTime = ptime;
	    				}
    				}

    				// 사용자(smtp 인증용): 형식 ==> 아이디:AES비번 (pwd_enc.sh로 생성)
    				Map<String, String> userMap = new HashMap<String, String>();
    				String userPath = SensProxyEnvironment.getSensProxyServerHome()+"/conf/users";
    				File ufile = new File(userPath);
    				if(ufile.exists()) {
	    				long utime = ufile.lastModified();
	    				if(ImSmtpServer.m_lUsersConfTime != utime) {
	    					List<String> listUsers = ImSensProxyUtils.readFileByLine(userPath);
	        				for(String line: listUsers) {
	        					String[] arrUser = line.split(":");
	        					if(arrUser.length > 1) {
	        						userMap.put(arrUser[0].trim(), arrUser[1].trim());
	        					}
	        				}

	        				ImSmtpConfig.getInstance().setUserMap(userMap);
	    					smtpLogger.debug("Reload User list: {}", userMap.size());
	    					ImSmtpServer.m_lUsersConfTime = utime;
	    				}
    				} else {
    					ufile.createNewFile();
    					ImSmtpConfig.getInstance().setUserMap(userMap);
    					smtpLogger.debug("Reload User list: {}", userMap.size());
    					long utime = ufile.lastModified();
    					ImSmtpServer.m_lUsersConfTime = utime;
    				}

    				// 릴레이 아이피
    				String relayipPath = SensProxyEnvironment.getSensProxyServerHome()+"/conf/relayip.xml";
    				File rfile = new File(relayipPath);
    				long rtime = rfile.lastModified();
    				if(ImSmtpServer.m_lRelayIpConfTime != rtime) {
    					ImSmtpConfig.getInstance().setRelayIpList(loadRelayIpList(relayipPath));
    					smtpLogger.debug("Reload Relay IP list: {}", ImSmtpConfig.getInstance().getRelayIpList().toString());
    					ImSmtpServer.m_lRelayIpConfTime = rtime;
    				}

    				// 거부 아이피
    				String denyipPath = SensProxyEnvironment.getSensProxyServerHome()+"/conf/denyip.xml";
    				File dfile = new File(denyipPath);
    				long dtime = dfile.lastModified();
    				if(ImSmtpServer.m_lDenyIpConfTime != dtime) {
    					ImSmtpConfig.getInstance().setDenyIpList(loadDenyIpList(denyipPath));
    					smtpLogger.debug("Reload Denied IP list: {}", ImSmtpConfig.getInstance().getDenyIpList().toString());
    					ImSmtpServer.m_lDenyIpConfTime = dtime;
    				} */

                    // TLS 연결 실패 아이피 목록 관리도 여기서...
                    // 하루 이상 보관중인 아이피는 삭제처리
                    if(cnt > 120) {
                        TLSFailHostService failTLSHostService = TLSFailHostService.getInstance();
                        failTLSHostService.cleanMemory();
                        cnt = 0;
                    }

                    cnt++;
                    // 1분간격 체크
                    Thread.sleep(30000);
                }

            }catch(Exception e) {

            }
        }

    }

    /**
     * 서비스 중지 체크
     */
    private static class SenderShutdownHook extends Thread {
        private Thread mainThread;

        public SenderShutdownHook(Thread thread) {
            this.mainThread = thread;
        }

        @Override
        public void run() {
            transferLogger.info("+++++++++++++++++++++ Shutdown Called +++++++++++++++++++++");

            serverSocket.kill();

            ImSenderThreadManager senderThreadManager = ImSenderThreadManager.getInstance();
            senderThreadManager.interrupt();
            senderThreadManager.clear();

            transferLogger.info("+++++++++++++++++++++ Shutdown Complete +++++++++++++++++++++");
        }
    }

    private static synchronized void setSendCount(boolean p_bIncrease){
        if(p_bIncrease){
            m_nSend++;
        }else{
            m_nSend--;
        }
    }

    /**
     * dist-thread에서 메일을 받음
     */
    private static class ImEMSSenderConnectClient extends ImServerSocket.Client {

        protected ImSenderSession initSndrSession(Socket socket){
            ImSenderSession sndrss = null;
            try{
                transferLogger.trace("ImEmsSenderServerHandler.initSndrSession");
                sndrss = new ImSenderSession(socket);

                InetAddress ia = sndrss.getSocket().getInetAddress();
                String ip = ia.getHostAddress();
                int port = sndrss.getSocket().getLocalPort();

                sndrss.setPeerIP(ip);
                sndrss.setPeerPort(port);

                if(StringUtils.isEmpty(sndrss.getTraceID())) {
                    sndrss.setTraceID(UUIDService.getTraceID());
                }
                sndrss.setSndrState(ImSenderSession.SNDR_STATE_DEF);

                sndrss.setTimeStamp("<"+System.currentTimeMillis()+"@"+ ImEmsConfig.getInstance().getDefaultDomain()+">");
                Date dt = new Date();
            }catch(Exception ex){
                String errorId = ErrorTraceLogger.log(ex);
                transferLogger.error("[{}] {} - [SNDR] InitSmtpSession " , sndrss.getTraceID(), errorId );
            }

            return sndrss;
        }

        private boolean commitMessage(ImSenderSession sndrss, ArrayList p_arrMail){
            String sQueuePath = ImMessQueue.createQueueMessage(ImEmsConfig.getInstance().getQueuePath());
            ObjectOutputStream objS = null;
            File f = new File(sQueuePath);

            try{
                objS = new ObjectOutputStream(new FileOutputStream(f));
                objS.writeObject(p_arrMail);
                //m_bqmsg.enqueue(sQueuePath);
                ImSenderServer.getInstance().addQueue(sQueuePath);
                transferLogger.info("Get Data : {}", sQueuePath);
                setSendCount(true);
            }catch(Exception ex){
                String errorId = ErrorTraceLogger.log(ex);
                transferLogger.error("[{}] [{}] commitMessage error - {}", sndrss.getTraceID(), errorId, sQueuePath);
                return false;
            }finally{
                try{
                    if(objS != null)objS.close();
                }catch(IOException ex1){}
            }

            return true;
        }

        public boolean handleCmd_Data(ImSenderSession sndrss){
            if(sndrss.getSndrState() != ImSenderSession.SNDR_HELO){
                transferLogger.error("[{}] Data : Bad sequence of commands", sndrss.getTraceID());
                sendClient(sndrss, "503 5.5.1 Bad sequence of commands");
                return false;
            }

            ObjectInputStream in = null;

            sendClient(sndrss,"354 Start mail input;");
            try{
                in = new ObjectInputStream(sndrss.getSocket().getInputStream());
                ArrayList arrMail = (ArrayList)in.readObject();

                commitMessage(sndrss, arrMail);
                sendClient(sndrss,"250 OK");
            }catch(Exception ex){
                String errorId = ErrorTraceLogger.log(ex);
                transferLogger.error("[{}] [{}] Data error - ", sndrss.getTraceID(), errorId);
                sendClient(sndrss,"421 4.3.2 Service not available");
                return false;
            }

            return true;
        }

        public boolean handleCmd_Helo(ImSenderSession sndrss, String p_sCommand) {
            String[] arrMsg = p_sCommand.split(" ");

            if(arrMsg.length < 2){
                // 인자에 도메인이 없는 경우 도메인이 없다는 메시지.
                sendClient(sndrss, "501 5.5.4 Required arguments");
                transferLogger.error("[{}] Helo : Required arguments", sndrss.getTraceID());
                return false;
            }

            // 명령어 상태를 HELO 상태로 변경
            sndrss.setSndrState(ImSenderSession.SNDR_HELO);

            // 클라이언트에 성공 메시지를 보낸다.
            String sRetMsg = "250 Hello [" + sndrss.getPeerIP() + "]";
            sendClient(sndrss, sRetMsg);

            return true;
        }

        public int handleCmd_Count(ImSenderSession sndrss){
            ObjectInputStream in = null;

            sendClient(sndrss,"250 "+m_nSend);

            return 0;
        }

        public boolean handleCommand(String p_sCommand,ImSenderSession sndrss){
            if(p_sCommand == null || p_sCommand.length() <= 0){
                return false;
            }
            String[] arrCommand = p_sCommand.split(" ");

            if(arrCommand.length <= 0){
                return false;
            }
//			System.out.println(msg);

            transferLogger.debug( "[{}] CMD:{} ( ip:{} / port:{} )", sndrss.getTraceID(), p_sCommand, sndrss.getPeerIP(), sndrss.getPeerPort() );


            if(arrCommand[0].equalsIgnoreCase("QUIT")){
                return false;
            }else if(arrCommand[0].equalsIgnoreCase("COUNT")){
                handleCmd_Count(sndrss);
            }else if(arrCommand[0].equalsIgnoreCase("HELO")){
                handleCmd_Helo(sndrss, p_sCommand);
            }else if(arrCommand[0].equalsIgnoreCase("DATA")){
                handleCmd_Data(sndrss);
            }else{
                sendClient(sndrss,"500 5.5.1 Syntax error, command unrecognized");
            }
            return true;
        }

        public void sendClient(ImSenderSession sndrss, String strMsg){
            try{
                PrintWriter pw =  new PrintWriter(new BufferedWriter(new OutputStreamWriter(sndrss.getSocket().getOutputStream())));
                pw.print(strMsg+"\r\n");
                pw.flush();
            }catch(IOException ex){}
        }

        @Override
        protected void communicate(Socket socket) {
            BufferedReader br  = null;
            try{
                ImSenderSession sndrss = initSndrSession(socket);

                br = new BufferedReader(new ImCRLFTerminatedReader(new BufferedInputStream(socket.getInputStream(),512), "ASCII"));

                sendClient(sndrss,"200 EMS Sender Server ready");
                String sMsg = "";

                while(true){
                    sMsg = br.readLine();
                    if(!handleCommand(sMsg, sndrss)){
                        break;
                    }
                }

                sendClient(sndrss,"221 Bye");
            } catch(IOException e) {
            } finally {
                try{
                    if(br!=null) br.close();
                    if(socket!=null) socket.close();
                    br=null; socket=null;
                }catch(Exception e){}
            }
        }

        @Override
        public ImServerSocket.Client replicate() {
            return new ImEMSSenderConnectClient();
        }
    }
}
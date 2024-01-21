package com.imoxion.sensems.server.emsd;

import com.imoxion.common.database.ImDatabaseConnectionEx;
import com.imoxion.sensems.server.ImEmsServer;
import com.imoxion.sensems.server.beans.ImEmsMailData;
import com.imoxion.sensems.server.beans.ImRecvRecordData;
import com.imoxion.sensems.server.config.ImEmsConfig;
import com.imoxion.sensems.server.domain.ImbEmsMain;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.repository.MsgidRecvRepository;
import com.imoxion.sensems.server.service.DatabaseService;
import com.imoxion.sensems.server.service.EmsMainService;
import com.imoxion.sensems.server.service.ErrorCountService;
import com.imoxion.sensems.server.service.ReceiverService;
import com.imoxion.sensems.server.util.ImEmsUtil;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * recv_[msgid] 테이블에서 수신자 데이터를 뽑아서 ImEmsDistThread.bqMail 에 enqueue 한다.
 */
public class ImTransferRecvThread implements Runnable {
    private static Logger logger = LoggerFactory.getLogger("EMSD");

    private ImbEmsMain emsMain;

    public ImTransferRecvThread(ImbEmsMain emsMain) {
        this.emsMain = emsMain;
    }

    public void transferRecv(ImbEmsMain emsMain) throws Exception {
        MsgidRecvRepository recvRepository = MsgidRecvRepository.getInstance();
        ReceiverService receiverService = ReceiverService.getInstance();
        EmsMainService emsMainService = EmsMainService.getInstance();
        DatabaseService databaseService = DatabaseService.getInstance();
        ErrorCountService errorCountService = ErrorCountService.getInstance();

        boolean bResend = false;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int sendCount = 0;
        int fieldCount = 0;
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        ArrayList<ImEmsMailData> arrMail = new ArrayList<ImEmsMailData>();

        // extended: 999 재발신(에러재발신, 미수신자 재발신 등)
        if ("999".equals(emsMain.getExtended())) {
            bResend = true;
        }

        try(SqlSession session = ImDatabaseConnectionEx.getConnection(); ){
            conn = session.getConnection();
            String qry = "select * from  recv_" + emsMain.getMsgid() + " where success=2 order by field1";
            String updateQuery = "update recv_" + emsMain.getMsgid() + " set success=?, errcode=?, err_exp=?, send_time=? where id=?";
            ps = conn.prepareStatement(qry);
            rs = ps.executeQuery();
            int nMaxRecv = 10;
            if(rs != null) {
                int id = 0;
                ResultSetMetaData rMeta = rs.getMetaData();
                // recv_msgid 테이블의 fiedl1, field2...를 제외한 필드가 총 12개
                fieldCount = rMeta.getColumnCount() - 12;

                while (rs.next() && ImEmsServer.isAlive) {
                    if (Thread.interrupted()) {
                        logger.info("<" + Thread.currentThread().getName() + ", " + emsMain.getMsgid() + "> Interrupted & Stopped");
                        break;
                    }

                    ImRecvRecordData rData = new ImRecvRecordData();
                    sendCount++;
                    String strCurrDate = df.format(new Date());

                    for (int i = 1; i <= fieldCount; i++) {
                        /*if(i == 1){
                            rData.addRecord("FIELD" + i,  ImEmsUtil.getDecryptString(rs.getString("field" + i)));
                        } else {
                            rData.addRecord("FIELD" + i,  rs.getString("field" + i));
                        }*/
                        String data =  ImEmsUtil.getDecryptString(rs.getString("field" + i));
                        rData.addRecord("FIELD" + i,  data);
                        //logger.info("transferRecv addRecord: FIELD" + i + " - " + data + " / " + rs.getString("field" + i));
                    }

                    id = rs.getInt("id");
                    String email = ImEmsUtil.getDecryptString(rs.getString("field1")).trim();
logger.info("transferRecv - {} : {}, send - {}", id, email, sendCount);
                    arrMail.add(makeMailData(rData, emsMain, email, ImEmsUtil.getDomainOfEmail(email), id, updateQuery));

                    if (sendCount > 100) {
                        nMaxRecv = 20;
                    } else if (sendCount > 1000) {
                        nMaxRecv = ImEmsConfig.getInstance().getMaxRecvCount();
                    }

                    if ((sendCount % nMaxRecv) == 0) {
                        Thread.sleep(100);
                        transferMail(arrMail.clone());
                        arrMail.clear();
                    }
                }
            }

        } catch (SQLException se){
            throw se;
        } catch (Exception e){
            throw e;
        } finally {
            try { if(rs != null) rs.close(); }catch(Exception e){}
            try { if(ps != null) ps.close(); }catch(Exception e){}
            try { if(conn != null) conn.close(); }catch(Exception e){}
        }
        if(arrMail.size() > 0){
            transferMail(arrMail.clone());
            arrMail.clear();
        }

        logger.info("TransferRecv: send : {}", sendCount);
    }

    private void transferMail(Object arrMail){
        ImEmsDistThread.bqMail.enqueue((ArrayList<ImEmsMailData>)arrMail);
    }

    private ImEmsMailData makeMailData(ImRecvRecordData rData, ImbEmsMain emsMain, String email, String domain, int id, String updateQuery){

        ImEmsMailData mailData = new ImEmsMailData();
        mailData.setEmsMain(emsMain);
        mailData.setRecordData(rData);
        mailData.setUpdateQuery(updateQuery);
        mailData.setId(id);
        mailData.setMailFrom(emsMain.getMail_from());
        mailData.setReplyTo(emsMain.getReplyto());
        mailData.setRcptTo(email);
        mailData.setDomain(domain);

        return mailData;
    }

    @Override
    public void run() {
        String currentThreadName = Thread.currentThread().getName();

        if(ImEmsServer.isAlive){
            try {
                if (emsMain == null) {
                    throw new Exception("EmsMain & msgid is null");
                }

                // recv_msgid 테이블에서 추출하여 발송
                transferRecv(emsMain);


                if (Thread.interrupted()) {
                    logger.info("<" + currentThreadName + ", " + emsMain.getMsgid() + "> Interrupted & Stopped");
                }
            }catch(InterruptedException ex) {
                //ex.printStackTrace();
                logger.info("<" + currentThreadName + ", " + emsMain.getMsgid() + "> Interrupted & Stopped.");
            }catch(Exception ex) {
                String errorId = ErrorTraceLogger.log(ex);
                logger.error("{} - ImTransferRecvThread Error - {}", errorId, emsMain.getMsgid() );
            }finally {
                if(ImEmsMainThread.taskTransferMap.containsKey(emsMain.getMsgid())) {
                    ImEmsMainThread.taskTransferMap.remove(emsMain.getMsgid());
                }
                ImEmsMainThread.bIsRecv = false;
            }
        }

        logger.info("ImTransferRecvThread Shutdown - {}", emsMain.getMsgid());
    }
}

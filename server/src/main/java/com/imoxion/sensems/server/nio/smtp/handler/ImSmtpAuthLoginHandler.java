package com.imoxion.sensems.server.nio.smtp.handler;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.server.config.ImSmtpConfig;
import com.imoxion.sensems.server.exception.ImSmtpException;
import com.imoxion.sensems.server.smtp.ImSmtpSession;
import com.imoxion.sensems.server.util.MailAddress;
import com.imoxion.sensems.server.util.MailAddressUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang.StringUtils;

public class ImSmtpAuthLoginHandler extends ImSmtpAuthHandler {

    private static int STEP_NONE = 0;
    private static int STEP_USER = 1;
    private static int STEP_PW = 2;

//    private ImSmtpSession smtps;
//    private ChannelHandler channelHandler;
    private String sAuthParam;
    private int step = STEP_NONE;

    private String userid;
    private String passwd;

    public ImSmtpAuthLoginHandler(ImSmtpSession smtps, ChannelHandler channelHandler, String sAuthParam) {
        super(smtps, channelHandler);
        this.sAuthParam = sAuthParam;
    }

    //private void sendClient(ChannelHandlerContext ctx, Object msg){ ctx.writeAndFlush((String)msg + "\r\n"); }
    private void sendClient(ChannelHandlerContext ctx, String msg){
        ctx.writeAndFlush(msg + "\r\n");
    }
    //private void sendClient(ChannelHandlerContext ctx, ImSmtpException e){  sendClient(ctx, e.getMessage()); }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        smtpLogger.info("[{}] ImSmtpAuthLoginHandler handlerAdded", super.getSmtps().getTraceID());

        String tmpUser;
        try {
            if (StringUtils.isEmpty(sAuthParam)) {
                sendClient(ctx, "334 VXNlcm5hbWU6");
            } else {
                userid = sAuthParam;
                sendClient(ctx, "334 UGFzc3dvcmQ6");
                step = STEP_USER;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        //System.out.println("ImSmtpAuthLoginHandler.handlerAdded");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       byte[] byteCmd = (byte[]) msg;
        String line = new String(byteCmd);

        if(step == STEP_NONE) {
            userid = line;
            sendClient(ctx, "334 UGFzc3dvcmQ6");
            step = STEP_USER;
        } else if(step == STEP_USER) {
            passwd = line;
            step = STEP_PW;

            // 아이디/비번을 받았음
            String sAccount  = ImUtils.decodeBase64(userid);
            String sPasswd =  ImUtils.decodeBase64(passwd);
            String logPasswd = ImStringUtil.maskString(sPasswd, 1, 4, "*");

            if(StringUtils.isEmpty(sPasswd)){
                String domain = "";
                String userid = "";
                try {
                    MailAddress mailAddress = MailAddressUtil.getMailAddress(sAccount);
                    if (mailAddress != null) {
                        domain = mailAddress.getDomain();
                        userid = mailAddress.getUserid();
                    }
                }catch(Exception e){
                    // 이메일 파싱시 오류가 발생하면 sAccount 를
                    userid = sAccount;
                }
               
                Thread.sleep(ImSmtpConfig.getInstance().getUseTarpit());

                //smtpLogger.info( "CMD : Auth - login error (" + smtps.getPeerIP() + "/" + smtps.getCountry()+ "/" + sAccount+"/"+sPasswd + ") Password is NULL");
                smtpLogger.info( "[{}] CMD : Auth - login error ({}/{}/{}/{}) Password is NULL", super.getSmtps().getTraceID(), super.getSmtps().getPeerIP(), super.getSmtps().getCountry(), sAccount, logPasswd);

                throw new ImSmtpException("535 5.7.1");
            }

            if(!super.doAuth(sAccount, sPasswd)){
                Thread.sleep(ImSmtpConfig.getInstance().getUseTarpit());

                smtpLogger.info( "[{}] CMD : Auth - login error ({}/{}/{}/{}) ", super.getSmtps().getTraceID(), super.getSmtps().getPeerIP(), super.getSmtps().getCountry(), sAccount, logPasswd);
                throw new ImSmtpException( "535 5.7.1");
            }

           // String fullAccount = sAccount;
            if(sAccount.indexOf("@") > -1){
                sAccount = ImStringUtil.getStringBefore(sAccount, "@");
            }

            // 비밀번호와 아이디가 같은지 체크
            if(sAccount.equalsIgnoreCase(sPasswd)){
                Thread.sleep(ImSmtpConfig.getInstance().getUseTarpit());

                smtpLogger.info( "[{}] CMD : Auth login - error : password is too easy ({}/{}/{}/{}) ",
                        super.getSmtps().getTraceID(), super.getSmtps().getPeerIP(), super.getSmtps().getCountry(), sAccount, logPasswd);
                throw new ImSmtpException( "535 5.7.4");
            }
            
            
            //smtps.setLogonUser(sAccount);
            super.getSmtps().setLogonEmailID(sAccount);
           /* ImFilterService smtpFilterService = ImFilterService.getInstance();
            for (ISMTPProcessFilter filter : smtpFilterService.getAuthFilter()){
                if (filter != null) {
                    if (!filter.doProcess(super.getSmtps())) {
                        Thread.sleep(ImSmtpConfig.getInstance().getUseTarpit());
                        throw new ImSmtpException( "535 5.7.1");
                    }
                }
            }*/
            super.getSmtps().setFlags( 1);
            super.getSmtps().setAuth(true);
            super.getSmtps().setAuth_user(sAccount);
            super.getSmtps().setSmtpState(ImSmtpSession.SMTP_STATE_AUTH);
            super.getSmtps().setRelay(true);

            //smtpLogger.info( "[{}] Auth login (" + super.getSmtps().getPeerIP() + "/ " + super.getSmtps().getCountry() + ") : "+super.getSmtps().getLogonEmailID()+" OK",super.getSmtps().getTraceID());
            smtpLogger.info( "[{}] Auth login ({}/{}) : {}({}) OK",super.getSmtps().getTraceID(), super.getSmtps().getPeerIP(), super.getSmtps().getCountry(),
                    super.getSmtps().getLogonEmailID(), super.getSmtps().getLogonDomain());
            sendClient(ctx, "235 2.7.0 Authentication successful");
            revertBaseHandler(ctx);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }
}

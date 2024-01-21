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

public class ImSmtpAuthPlainHandler extends ImSmtpAuthHandler {

    private int STEP_NONE = 0;
    private int STEP_USER = 1;
    private int STEP_PW = 2;

    private String sAuthParam;
    private int step = STEP_NONE;

    private String userid;
    private String passwd;

    public ImSmtpAuthPlainHandler(ImSmtpSession smtps, ChannelHandler channelHandler, String sAuthParam) {
        super(smtps, channelHandler);
        this.sAuthParam = sAuthParam;
    }

    private void sendClient(ChannelHandlerContext ctx, String msg){
        ctx.writeAndFlush(msg + "\r\n");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        smtpLogger.info("[{}] ImSmtpAuthPlainHandler handlerAdded", super.getSmtps().getTraceID());
        if(StringUtils.isEmpty(sAuthParam)) {
            sendClient(ctx, "334 VXNlcm5hbWU6");
        } else {
            channelRead(ctx, sAuthParam.getBytes());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] byteCmd = (byte[]) msg;
        String line = new String(byteCmd);

        // Decode authparam
        String sClientAuth = ImUtils.decodeBase64(line);

        if (sClientAuth == null || sClientAuth.equals("")) {
            //파라메터 오류
            Thread.sleep(ImSmtpConfig.getInstance().getUseTarpit());
           
            smtpLogger.info("[{}] CMD : Auth - plain error (" + super.getSmtps().getPeerIP() + "/" + super.getSmtps().getCountry() + "/" + sClientAuth + ")", super.getSmtps().getTraceID());
//            throw new ImSmtpException("535 5.7.1");
            sendClient(ctx, new ImSmtpException("535 5.7.1").getMessage());
            this.channelUnregistered(ctx);
            return;
        }

        sClientAuth = sClientAuth.trim();
        String[] ss = sClientAuth.split("\0");

        if (ss.length <= 1) {
            //아이디와 비밀번호를 구별하는 공백이 없음
            Thread.sleep(ImSmtpConfig.getInstance().getUseTarpit());

            smtpLogger.info("[{}] CMD : Auth - plain error (" + super.getSmtps().getPeerIP() + "/" + sClientAuth + ")", super.getSmtps().getTraceID());
//            throw new ImSmtpException("535 5.7.1");
            sendClient(ctx, new ImSmtpException("535 5.7.1").getMessage());
            this.channelUnregistered(ctx);
            return;
        }


        String sAccount = ss[0].trim();
        String sPasswd = ss[ss.length - 1];
        String logPasswd = ImStringUtil.maskString(sPasswd, 1, 4, "*");

        if (StringUtils.isEmpty(sPasswd)) {
            MailAddress mailAddress = MailAddressUtil.getMailAddress(sAccount);
            String domain = "";
            String userid = "";
            if (mailAddress != null) {
                domain = mailAddress.getDomain();
                userid = mailAddress.getUserid();
            }
            
            Thread.sleep(ImSmtpConfig.getInstance().getUseTarpit());
            smtpLogger.info("[{}] CMD : Auth - plain error (" + super.getSmtps().getPeerIP() + "/" + super.getSmtps().getCountry() + "/" + sClientAuth + ") Password is NULL", super.getSmtps().getTraceID());

            sendClient(ctx, new ImSmtpException("535 5.7.1").getMessage());
            this.channelUnregistered(ctx);
            return;
        }

        sPasswd = sPasswd.trim();

        if (!super.doAuth(sAccount, sPasswd)) {
            Thread.sleep(ImSmtpConfig.getInstance().getUseTarpit());

            smtpLogger.info("[{}] CMD : Auth - login plain ({}/{}/{}/{}) ", super.getSmtps().getTraceID(), super.getSmtps().getPeerIP(), super.getSmtps().getCountry(), sAccount, logPasswd);
//            throw new ImSmtpException("535 5.7.1");
            sendClient(ctx, new ImSmtpException("535 5.7.1").getMessage());
            this.channelUnregistered(ctx);
            return;
        }

        //String fullAccount = sAccount;
        if (sAccount.indexOf("@") > -1) {
            sAccount = ImStringUtil.getStringBefore(sAccount, "@");
        }

        // 비밀번호와 아이디가 같은지 체크
        if (sAccount.equalsIgnoreCase(sPasswd)) {
            Thread.sleep(ImSmtpConfig.getInstance().getUseTarpit());

            smtpLogger.info("[{}] CMD : Auth plain - error ({}/{}/{}/{}) ", super.getSmtps().getTraceID(), super.getSmtps().getPeerIP(), super.getSmtps().getCountry(), sAccount, logPasswd);
//                throw new ImSmtpException("535 5.7.4 Authentication failed: password is too easy");
            sendClient(ctx, new ImSmtpException("535 5.7.4").getMessage());
            this.channelUnregistered(ctx);
            return;
        }
        
        String domain = "";
        String userid = "";
        MailAddress mailAddress = MailAddressUtil.getMailAddress(sAccount);
        if (mailAddress != null) {
            domain = mailAddress.getDomain();
            userid = mailAddress.getUserid();
        } else {
            if (sAccount.indexOf("@") > -1) {
                userid = ImStringUtil.getStringBefore(sAccount, "@");
            }
        }

        //smtps.setLogonUser(sAccount);
        super.getSmtps().setLogonEmailID(sAccount);
        super.getSmtps().setFlags(1);
        super.getSmtps().setAuth(true);
        super.getSmtps().setAuth_user(sAccount);
        super.getSmtps().setSmtpState(ImSmtpSession.SMTP_STATE_AUTH);
        super.getSmtps().setRelay(true);

        //smtpLogger.info("[{}] Auth plain (" + super.getSmtps().getPeerIP() + "/ " + super.getSmtps().getCountry() + ") : " + super.getSmtps().getLogonEmailID() + " OK", super.getSmtps().getTraceID());
        smtpLogger.info("[{}] Auth plain ({}/{}) : {}({}) OK",super.getSmtps().getTraceID(), super.getSmtps().getPeerIP(), super.getSmtps().getCountry(),
                super.getSmtps().getLogonEmailID(), super.getSmtps().getLogonDomain());
        sendClient(ctx, "235 2.7.0 Authentication successful");
        revertBaseHandler(ctx);
    }
}

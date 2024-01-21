/*
 * �ۼ��� ��¥: 2005. 3. 3.
 *
 * TODO ��� ���Ͽ� ���� ���ø�Ʈ�� �����Ϸx� ��=8�� �̵��Ͻʽÿ�.
 * â - ȯ�� ��d - Java - �ڵ� ��Ÿ�� - �ڵ� ���ø�Ʈ
 */
package com.imoxion.sensems.server.smtp;

import com.imoxion.common.net.ImSmtp;


public class ImBizemsSmtp extends ImSmtp {

    public boolean helo2(String p_sHost) {
        boolean bRet = false;
        this.heloHost = p_sHost;

        try {
            String sSend = "HELO " + p_sHost + "\r\n";
            if (!this.send(sSend)) {
                this.nErrCode = 905;
                return false;
            }

            if (!this.checkResponse()) {
                return false;
            }

            bRet = true;
        } catch (Exception var4) {
            this.nErrCode = 910;
            this.sErrorMsg = "HELO : (" + this.remoteHost + ") " + var4.getMessage();
            this.disconnect();
            return false;
        }


        return bRet;
    }

    public boolean msgid(String p_sMsgid) {
        try {
            String sSend = "MSGID:" + p_sMsgid + "\r\n";
            if (!send(sSend)) {
                return false;
            }

            if (!checkResponse()) {
                return false;
            }
        } catch (Exception ex) {
            sErrorMsg = "MSGID : " + ex.getMessage();
            disconnect();

            return false;
        }

        return true;

    }

    public boolean rcptid(String p_sRcptid) {
        try {
            String sSend = "RCPTID:" + p_sRcptid + "\r\n";
            if (!send(sSend)) {
                return false;
            }

            if (!checkResponse()) {
                return false;
            }
        } catch (Exception ex) {
            sErrorMsg = "RCPTID : " + ex.getMessage();
            disconnect();

            return false;
        }

        return true;

    }

    public boolean dataObject(Object p_oData) {
        try {
            String sSend = "DATA\r\n";

            //ImLog.info("extlistener.log","exlistener: doSendMail : 1111 " + ((ArrayList)p_oData).size() );
            if (!send(sSend)) {
                nErrCode = 905;
                ///System.out.println("1: " + this.getError());
                return false;
            }
            if (!checkResponse()) {
                //System.out.println("2: " +this.getError());
                return false;
            }
            if (!sendObject(p_oData)) {
                //System.out.println("3: " +this.getError());
                nErrCode = 905;
                return false;
            }
            if (!checkResponse()) {
                //System.out.println("4: " +this.getError());
                return false;
            }

        } catch (Exception ex) {
            nErrCode = 910;
            sErrorMsg = "DATA : " + ex.getMessage();
            //System.out.println(sErrorMsg);
            disconnect();
            return false;
        }

        return true;
    }


    public int getCount() {
        int nRet = 0;
        try {
            String sSend = "COUNT\r\n";
            if (!send(sSend)) {
                nErrCode = 905;
                return -1;
            }

            if (!checkResponse()) {
                return -1;
            }

            nRet = Integer.parseInt(sResponse);

        } catch (Exception ex) {
            nErrCode = 910;
            sErrorMsg = "COUNT : " + ex.getMessage();
            disconnect();
            return -1;
        }

        return nRet;
    }

    public boolean moveMsg(String p_sHost, int p_nPort, int p_nCount) {
        try {
            String sSend = "MOVEMSG " + p_sHost + ":" + p_nPort + ":" + p_nCount + "\r\n";
            if (!send(sSend)) {
                nErrCode = 905;
                return false;
            }

            if (!checkResponse()) {
                return false;
            }


        } catch (Exception ex) {
            nErrCode = 910;
            sErrorMsg = "MOVEMSG : " + ex.getMessage();
            disconnect();
            return false;
        }

        return true;
    }

    public int getBizemsErrorCode(){
        int nRet = 0;
        switch(nErrCode){
            case 0:
                nRet = 0;
                break;
            case 421:
                nRet = 902;
                break;
            case 450:
            case 451:
                nRet = 906;
                break;
            case 452:
            case 552:
                nRet = 909;
                break;
            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
                nRet = 907;
                break;
            case 505:
            case 511:
            case 550:
            case 551:
            case 553:
            case 554:
                nRet = 908;
                break;
            case 901:
            case 902:
            case 903:
            case 904:
                nRet = nErrCode;
                break;
            default:
                nRet = 910;
        }

        return nRet;
    }

    static public class ImTest {
        //------------------------------------------------------------
        public static void main(String[] args) throws Exception {
            ImBizemsSmtp smtp = new ImBizemsSmtp();

            if (!smtp.connect("emart.bizems.com", 9090)) {
                System.out.println("connect error");
                return;
            }

            if (!smtp.helo("jungyc.imoxion.com")) {
                System.out.println("hel error: " + smtp.sResponse);
                return;
            }

            if (!smtp.mail("jungyc@yahoo.com")) {
                System.out.println("mail error" + smtp.sResponse);
                return;
            }

            if (!smtp.msgid("pesonal")) {
                System.out.println("rcpt error" + smtp.sResponse);
                return;

            }

            if (!smtp.rcptid("1111")) {
                System.out.println("rcpt error" + smtp.sResponse);
                return;

            }

            if (!smtp.rcpt("jungyc@imoxion.com")) {
                System.out.println("rcpt error" + smtp.sResponse);
                return;

            }

            if (!smtp.data("helo")) {
                System.out.println("data error" + smtp.sResponse);
                return;

            }

            smtp.close();


        }
    }
}

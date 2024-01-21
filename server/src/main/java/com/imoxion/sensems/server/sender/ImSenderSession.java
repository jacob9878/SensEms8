package com.imoxion.sensems.server.sender;

import com.imoxion.sensems.server.util.UUIDService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.net.Socket;

@Setter
@Getter
@ToString
public class ImSenderSession {
    public final static int SNDR_STATE_DEF = 0;
    public final static int SNDR_HELO = 1;
    public final static int SNDR_DATA = 2;

    private Socket socket;
    private String traceID;
    // 연결된 client ip
    private String peerIP;
    private int peerPort;
    private String timeStamp;
    private int sndrState = SNDR_STATE_DEF;

    public ImSenderSession(Socket sock) {
        this.socket = sock;
    }

    public void initSession(Socket sock) {
        this.socket = sock;
        this.traceID = UUIDService.getTraceID();

        resetSession();
    }

    public void resetSession() {
        traceID = "";

        if (sndrState > SNDR_HELO) {
            sndrState = SNDR_STATE_DEF;
            return;
        }
    }

}

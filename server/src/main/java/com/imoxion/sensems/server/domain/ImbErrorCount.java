package com.imoxion.sensems.server.domain;

import lombok.*;

@Setter
@Getter
@ToString
@RequiredArgsConstructor
public class ImbErrorCount {
    @NonNull private String msgid;
    private int unknownhost = 0;
    private int connect_error = 0;
    private int dns_error = 0;
    private int network_error = 0;
    private int system_error = 0;
    private int server_error = 0;
    private int syntax_error = 0;
    private int userunknown = 0;
    private int mboxfull = 0;
    private int etc_error = 0;
    private int emailaddr_error = 0;
    private int reject_error = 0;
    private int repeat_error = 0;
    private int domain_error = 0;
    private int blankemail_error = 0;
    private String extended;

    public void setErrorCount(String errorCode, int count){
        switch(errorCode) {
            case "901":
                this.unknownhost = count;
                break;
            case "902":
                this.connect_error = count;
                break;
            case "903":
                this.dns_error = count;
                break;
            case "904":
                this.network_error = count;
                break;
            case "905":
                this.system_error = count;
                break;
            case "906":
                this.server_error = count;
                break;
            case "907":
                this.syntax_error = count;
                break;
            case "908":
                this.userunknown = count;
                break;
            case "909":
                this.mboxfull = count;
                break;
            case "910":
                this.etc_error = count;
                break;
            case "911":
                this.emailaddr_error = count;
                break;
            case "912":
                this.reject_error = count;
                break;
            case "913":
                this.repeat_error = count;
                break;
            case "914":
                this.domain_error = count;
                break;
            case "915":
                this.blankemail_error = count;
                break;
        }
    }
}

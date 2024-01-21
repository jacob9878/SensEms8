package com.imoxion.sensems.server.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

@RequiredArgsConstructor
@ToString
public class ImSmtpException extends RuntimeException {
    @Getter
    private final String errorCode;
    private String[] args;

    public ImSmtpException(String errorCode, String[] args){
        this.errorCode = errorCode;
        this.args = args;
    }

    @Override
    public String getMessage() {
        String message;
        switch( errorCode )
        {
            case "421 4.3.0":
                message = "Temporary service not available, try later";
                break;
            case "421 4.3.1":
                message = "Service not available";
                break;
            case "421 4.3.2":
                message = "Service not available - ERR System";
                break;
            case "421 4.3.3":
                message = "Your IP blocked from this server (%s)";
                break;
            case "421 4.3.4":
                message = "Too many RSET";
                break;
            case "451 4.7.1":
                message = "detect illegal relay";
                break;
            case "452 4.5.3":
                message = "Too many recipients %s";
                break;
            case "453 4.7.1":
                message = "detect illegal relay %s-%s";
                break;
            case "458 4.5.0":
                message = "temporary queuing error";
                break;
            case "500 5.5.1":
                message = "filtered by blocked email : %s";
                break;
            case "500 5.5.2":
                message = "Command not allowed";
                break;
            case "501 5.5.0":
                message = "Syntax error, command unrecognized";
                break;
            case "501 5.5.1":
                message = "Return path is too long";
                break;
            case "501 5.5.2":
                message = "Syntax error in return path or arguments";
                break;
            case "501 5.5.3":
                message = "Requires recipient address";
                break;
            case "501 5.5.4":
                message = "Required arguments";
                break;
            case "501 5.5.5":
                message = "Syntax error. Input Journal Send Value";
                break;
            case "503 5.3.2":
                message = "Premature end of message";
                break;
            case "503 5.5.1":
                message = "Bad sequence of commands";
                break;
            case "503 5.5.2":
                message = "Syntax error! Input Journal Send Value";
                break;
            case "530 5.5.1":
                message = "Access denied to unauthenticated user";
                break;
            case "530 5.5.2":
                message = "Access denied - Invalid from address";
                break;
            case "530 5.5.3":
                message = "From Account disabled %s";
                break;
            case "530 5.5.4":
                message = "Access denied %s for user %s";
                break;
            case "535 5.7.1":
                message = "Authentication failed";
                break;
            case "535 5.7.2":
                message = "Authentication failed: Parameter is empty";
                break;
            case "535 5.7.3":
                message = "Authentication failed: Parameter syntex error";
                break;
            case "535 5.7.4":
                message = "Authentication failed: password is too easy";
                break;
            case "550 5.1.1":
                message = "Mailbox unavailable";
                break;
            case "550 5.1.1(2)":
                message = "Relay denied";
                break;
            case "550 5.2.1":
                message = "Account disabled %s";
                break;
            case "552 5.2.2":
                message = "Mailbox full ";
                break;
            case "552 5.2.3":
                message = "Message exceeds fixed maximum message size";
                break;
            default:
                message = "Syntax error, command unrecognized";
                break;
        }

        message = StringUtils.substringBeforeLast(errorCode, "(") + " " + formatString(message, args);

        return message;
    }

    /**
     * %s 만 사용함
     * @param frmt
     * @param strings
     * @return
     */
    private String formatString(String frmt, String ...strings) {
        int frmCount = StringUtils.countMatches(frmt, "%s");

        if(strings == null) {
            return frmt;
        }

        int inputCount = strings.length;

        String[] arrFrm = frmt.split("%s");
        StringBuffer sb = new StringBuffer();
        String result;
        if(frmCount > inputCount) {
            int i =0;
            for(i=0; i< inputCount; i++) {
                sb.append(arrFrm[i]).append(strings[i]);
            }
            for(int k = i; k<frmCount; k++) {
                sb.append(arrFrm[k]).append(" ");
            }
            result = sb.toString();
        } else {
            result = String.format(frmt, strings);
        }

        return result;
    }
}

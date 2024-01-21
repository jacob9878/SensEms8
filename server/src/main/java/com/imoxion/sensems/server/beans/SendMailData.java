package com.imoxion.sensems.server.beans;

import com.imoxion.common.mail.ImMessage;
import org.apache.commons.lang.StringUtils;

import javax.mail.Message;
import javax.mail.Session;
import java.util.*;

/**
 * Created by zpqdnjs on 2021-03-18.
 */
public class SendMailData {

    private String charset;

    private String fromName;

    private String fromEmail;

    private String to;

    private String receiver;

    private String subject;

    private String messageId;

    private String htmlBody;

    private String textBody;

    private Date senddate;
    
    private String reply_to;
    /**
     * 수신자 유형
     * ReceiverInfo.TO
     * ReceiverInfo.CC
     * ReceiverInfo.BCC
     */
    private int receiptType;

    private String receiptTag;

    private Map<String,String> headers = new LinkedHashMap<>();


    public ImMessage getMessage() throws Exception{
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props);
        ImMessage message = new ImMessage(session);
        //message.setContentEncoding(ImMessage.ENC_QP);
		message.setContentEncoding(ImMessage.ENC_7BIT);
        message.setCharset( charset );
        //message.setFrom(fromName, fromEmail, charset);
		////message.setFrom("[$FROM$]");
		message.setHeader("From", "[$FROM$]");
        
		/*if (StringUtils.isNotEmpty(to)) {
			message.setRecipientsEx(Message.RecipientType.TO.tost, to, charset);
		}*/
		message.addHeader(Message.RecipientType.TO.toString(), to);
        Iterator<String> headerIterator = getHeaders().keySet().iterator();
        while( headerIterator.hasNext() ){
            String headerKey = headerIterator.next();
            message.addHeader( headerKey , headers.get( headerKey ) );
        }
		message.setHeader("Subject", "[$SUBJECT$]");
        //message.setSubject("[$SUBJECT$]");
        message.setMessageID(messageId);

		if( senddate == null ){
			this.senddate = new Date();
		}
		message.setSentDate(this.senddate);
        /*
         * 본문이 하나라도 없는 경우 No Content 에러 발생하여 html,text 둘다 데이터가 없을 경우 "" 처리한다.
         */
        if( StringUtils.isEmpty(htmlBody) && StringUtils.isEmpty(textBody) ){
            message.setHtml("");
            message.setText("");
        }else{
            if( StringUtils.isNotEmpty(htmlBody) ) {
                message.setHtml(htmlBody);
            }
            if( StringUtils.isNotEmpty(textBody) ) {
                message.setText(textBody);
            }
        }
		//message.setContentEncoding(ImMessage.ENC_7BIT);

        return message;
    }


	public String getCharset() {
		return charset;
	}


	public void setCharset(String charset) {
		this.charset = charset;
	}


	public String getFromName() {
		return fromName;
	}


	public void setFromName(String fromName) {
		this.fromName = fromName;
	}


	public String getFromEmail() {
		return fromEmail;
	}


	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}


	public String getTo() {
		return to;
	}


	public void setTo(String to) {
		this.to = to;
	}


	public String getReceiver() {
		return receiver;
	}


	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}


	public String getSubject() {
		return subject;
	}


	public void setSubject(String subject) {
		this.subject = subject;
	}


	public String getMessageId() {
		return messageId;
	}


	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}


	public String getHtmlBody() {
		return htmlBody;
	}


	public void setHtmlBody(String htmlBody) {
		this.htmlBody = htmlBody;
	}


	public String getTextBody() {
		return textBody;
	}


	public void setTextBody(String textBody) {
		this.textBody = textBody;
	}


	public Date getSenddate() {
		return senddate;
	}


	public void setSenddate(Date senddate) {
		this.senddate = senddate;
	}


	public int getReceiptType() {
		return receiptType;
	}


	public void setReceiptType(int receiptType) {
		this.receiptType = receiptType;
	}


	public String getReceiptTag() {
		return receiptTag;
	}


	public void setReceiptTag(String receiptTag) {
		this.receiptTag = receiptTag;
	}


	public Map<String, String> getHeaders() {
		return headers;
	}


	public void setHeaders(String key, String value) {
		this.headers.put( key , value );
	}


	public String getReply_to() {
		return reply_to;
	}


	public void setReply_to(String reply_to) {
		this.reply_to = reply_to;
	}

}

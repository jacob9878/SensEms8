package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

@Setter
@Getter
public class ImbEmsContents {
    private String msgid;
    private String contents;

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "ImbEmsContents{" +
                "msgid='" + msgid + '\'' +
                ", contents='" + StringUtils.substring(contents , 0, 200) + '\'' +
                '}';
    }
}

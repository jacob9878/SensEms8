package com.imoxion.sensems.web.database.domain;

import jdk.nashorn.internal.objects.annotations.Setter;
import lombok.Getter;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.type.Alias;


@Alias("emsInfo")
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

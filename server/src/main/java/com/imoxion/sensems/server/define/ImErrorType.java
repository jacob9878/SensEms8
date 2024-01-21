package com.imoxion.sensems.server.define;

public enum ImErrorType {
    UNKNOWNHOST("901"),
    CONNECT_ERROR("902"),
    DNS_ERROR("903"),
    NETWORK_ERROR("904"),
    SYSTEM_ERROR("905"),
    SERVER_ERROR("906"),
    SYNTAX_ERROR("907"),
    USERUNKNOWN("908"),
    MBOXFULL("909"),
    ETC_ERROR("910"),
    EMAILADDR_ERROR("911"),
    REJECT_ERROR("912"),
    REPEAT_ERROR("913"),
    DOMAIN_ERROR("914"),
    BLANKEMAIL_ERROR("915");

    private String code;

    private ImErrorType(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

}

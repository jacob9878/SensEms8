package com.imoxion.sensems.web.database.domain;


import org.apache.ibatis.type.Alias;

/**
 * @author : jhpark
 * @date : 2021. 2. 5.
 */
@Alias("addrgrp")
public class ImbAddrGrp {

    public static final String DEL_ONLY_GROUP = "0";
    public static final String DEL_ONLY_ADDR = "1";
    public static final String DEL_GROUP_ADDR = "2";

    /**
     * 그룹 키
     * Auto_Increment
     */
    private int gkey;

    /**
     * 그룹명
     */
    private String gname;

    /**
     * 메모(설명)
     */
    private String memo;

    /**
     * 카운트
     */
    private int grpcount;

    public int getGrpcount() {
        return grpcount;
    }

    public void setGrpcount(int grpcount) {
        this.grpcount = grpcount;
    }

    public int getGkey() {
        return gkey;
    }

    public void setGkey(int gkey) {
        this.gkey = gkey;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return "ImbAddrGrp{" +
                "gkey=" + gkey +
                ", gname='" + gname + '\'' +
                ", memo='" + memo + '\'' +
                ", grpcount='" + grpcount + '\'' +
                '}';
    }
}

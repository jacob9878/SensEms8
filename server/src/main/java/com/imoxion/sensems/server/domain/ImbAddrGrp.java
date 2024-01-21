package com.imoxion.sensems.server.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : jhpark
 * @date : 2021. 2. 5.
 */
@Setter
@Getter
@ToString
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


}

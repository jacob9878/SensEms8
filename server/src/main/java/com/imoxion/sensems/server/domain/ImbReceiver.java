package com.imoxion.sensems.server.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Setter
@Getter
@ToString
public class ImbReceiver {

    /** 수신그룹 고유 키 24자리 */
    private String ukey;

    /** 수신그룹 작성자 */
    private String userid;

    /** 수신 그룹명 */
    private String recv_name;

    /** Imb_dbinfo의 DB 고유 키 24자리 */
    private String dbkey;

    /** 수신그룹 추출 SQL 쿼리문 */
    private String query;

    /** 등록 날짜 */
    private Date regdate;

    /** 기타 */
    private String extended;


}

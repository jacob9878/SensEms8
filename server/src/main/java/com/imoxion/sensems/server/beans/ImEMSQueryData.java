/*
 * �ۼ��� ��¥: 2005. 4. 26.
 *
 * TODO ��� ���Ͽ� ���� ���ø�Ʈ�� �����Ϸx� ��=8�� �̵��Ͻʽÿ�.
 * â - ȯ�� ��d - Java - �ڵ� ��Ÿ�� - �ڵ� ���ø�Ʈ
 */
package com.imoxion.sensems.server.beans;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@RequiredArgsConstructor
public class ImEMSQueryData {
	private String id = "";
    private String currTime = "";
    private String errorCode = "";
    private String errorStr = "";
    private String success = "2";
    private String domain = "";
    private String sql = "";
    private ImRecvRecordData rData = null;

    public ImEMSQueryData(String sID, String sCurrTime, String sErrorCode, String sErrorStr, String sSuccess, String sDomain, ImRecvRecordData rData) {
        this.id = sID;
        this.currTime = sCurrTime;
        this.errorCode = sErrorCode;
        this.errorStr = sErrorStr;
        this.success = sSuccess;
        this.domain = sDomain;
        this.rData = rData;
    }
}

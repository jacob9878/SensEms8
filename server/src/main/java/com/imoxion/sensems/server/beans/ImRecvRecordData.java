/*
 * �ۼ��� ��¥: 2005. 2. 22.
 *
 * TODO ��� ���Ͽ� ���� ���ø�Ʈ�� �����Ϸx� ��=8�� �̵��Ͻʽÿ�.
 * â - ȯ�� ��d - Java - �ڵ� ��Ÿ�� - �ڵ� ���ø�Ʈ
 */
package com.imoxion.sensems.server.beans;

import java.util.ArrayList;

/**
 * @author jungyc72
 *
 **/
class ImParamData extends ArrayList{
	private String sName  = "";
	private String sValue = "";
	
	ImParamData(String p_sName,String p_sValue){
		sName = p_sName;
		sValue = p_sValue;
		
		if(sName != null)
			sName.trim();
		
		if(sValue != null)
			sValue.trim();
	}
	
	public String getName(){return sName;}
	public String getValue(){return sValue;}
	
	public void setName(String p_sName){sName = p_sName;sName.trim();}
	public void setValue(String p_sValue){sValue = p_sValue;sValue.trim();}
}

public class ImRecvRecordData extends ArrayList {
	public void addRecord(String p_sName,String p_sValue){
		this.add(new ImParamData(p_sName,p_sValue));
	}
	
	public String getName(int p_nIndex){
		ImParamData param = (ImParamData)this.get(p_nIndex);
		if(param != null){
			return param.getName();
		}
		
		return null;
	}
	
	public String getValue(int p_nIndex){
		ImParamData param = (ImParamData)this.get(p_nIndex);
		if(param != null){
			return param.getValue();
		}
		
		return null;
	}

	public String getValue(String p_sName){
		int nCount = this.size();
		for(int i=0;i<nCount;i++){
			ImParamData param = (ImParamData)this.get(i);
			if(param.getName().compareTo(p_sName) == 0){
				return param.getValue();
			}
		}
		
		return null;
	}
	
	public Object removeRecord(String p_sName){
		int nCount = this.size();
		for(int i=0;i<nCount;i++){
			ImParamData param = (ImParamData)this.get(i);
			if(param.getName().compareTo(p_sName) == 0){
				return this.remove(i);
			}
		}
		return null;
	}
}

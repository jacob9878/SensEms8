package com.imoxion.sensems.server.beans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

@Alias("fromMonBean")
@Setter
@Getter
@ToString
public class ImFromMonBean {
	private String key = "";
	private long size = 0;
	private long count = 0;
	// 마지막 수신 시간을 저장한다.
	private long timestamp = 0;
	// 대용량 큐로 넘길지 여부
	private boolean bulk = false;

	public void addCount(long count){
		this.count += count;
	}

	public void addSize(long size){
		this.size += size;
	}
}

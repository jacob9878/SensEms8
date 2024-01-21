package com.imoxion.sensems.server.beans;

import com.imoxion.common.thread.ImBlockingQueue;
import org.apache.ibatis.type.Alias;

@Alias("queueobj")
public class ImQueueObj {
	ImBlockingQueue queue = new ImBlockingQueue();
	int count = 0;;
	boolean alive = true;
	int index = 0;
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}

	public ImBlockingQueue getQueue() {
		return queue;
	}
	public void setQueue(ImBlockingQueue queue) {
		this.queue = queue;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public boolean isAlive() {
		return alive;
	}
	public void setAlive(boolean alive) {
		this.alive = alive;
	}
	
}

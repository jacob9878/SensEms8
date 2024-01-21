package com.imoxion.sensems.web.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.Map;

public class ProgressService {

	private static ProgressService progressService = null;

	public static ProgressService getInstance() {
		if (progressService == null) {
			progressService = new ProgressService();
		}
		return progressService;
	}

	private final Logger log = LoggerFactory.getLogger(getClass());

	private Map<String, Queue> queueMap = new Hashtable<String, Queue>();

	/**
	 * 쓰레드를 정보를 저장하는 Queue 객체
	 * 
	 * @author : sunggyu
	 * @date : 2013. 3. 11.
	 * @desc :
	 * 
	 */
	public class Queue {

		/**
		 * 에러
		 */
		public static final int ERROR = -1;

		/**
		 * 완료
		 */
		public static final int COMPLETE = 2;

		/**
		 * 중지
		 */
		public static final int STOP = 0;

		/**
		 * 진행중
		 */
		public static final int PROGRESSIVE = 1;

		/**
		 * 정보를 찾을 수 없음
		 */
		public static final int NOINFO = -2;

		private long total;

		private long current;

		private long percent;

		private int state;

		private boolean alive = true;

		public long getTotal() {
			return total;
		}

		public void setTotal(long total) {
			this.total = total;
		}

		public long getCurrent() {
			return current;
		}

		public void setCurrent(long current) {
			this.current = current;
		}

		public long getPercent() {
			return percent;
		}

		public void setPercent(long percent) {
			this.percent = percent;
		}

		public int getState() {
			return state;
		}

		public void setState(int state) {
			this.state = state;
		}

		public boolean isAlive() {
			return alive;
		}

		public void setAlive(boolean alive) {
			this.alive = alive;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Queue [total=" + total + ", current=" + current + ", percent=" + percent + ", state=" + state
					+ ", alive=" + alive + "]";
		}
	}

	public void push(String key) {

		Queue q = new Queue();
		queueMap.put(key, q);

	}

	public void push(String key, long total) {
		Queue q = null;
		if (queueMap.containsKey(key)) {
			q = queueMap.get(key);
		} else {
			q = new Queue();
		}
		q.setTotal(total);
		q.setState(Queue.PROGRESSIVE);
		queueMap.put(key, q);
	}

	/**
	 * 프로세스 큐에서 해당 큐 정보를 구한다.
	 * 
	 * @Method Name : pop
	 * @Method Comment :
	 * 
	 * @param key
	 * @return
	 */
	public Queue pop(String key) {

		if (queueMap.containsKey(key)) {
			return queueMap.get(key);
		}
		return null;
	}

	/**
	 * 프로세스 큐를 제거한다.
	 * 
	 * @Method Name : remove
	 * @Method Comment :
	 * 
	 * @param key
	 */
	public void remove(String key) {

		if (queueMap.containsKey(key)) {
			queueMap.remove(key);
		}
	}

	/**
	 * 큐의 진행상태를 변경한다.
	 * 
	 * @Method Name : update
	 * @Method Comment :
	 * 
	 * @param key
	 * @param current
	 */
	public void update(String key, long current) {
		if (queueMap.containsKey(key)) {
			Queue q = queueMap.get(key);
			q.setCurrent(current);
			long percent = (100 * q.getCurrent()) / q.getTotal();
			q.setPercent(percent);
			q.setState(Queue.PROGRESSIVE);
			queueMap.put(key, q);
		}
	}

	/**
	 * 큐상태를 변경한다.
	 * 
	 * @Method Name : setState
	 * @Method Comment :
	 * 
	 * @param key
	 * @param statecode
	 */
	public void setState(String key, int statecode) {

		if (queueMap.containsKey(key)) {
			Queue q = queueMap.get(key);
			q.setState(statecode);
			queueMap.put(key, q);
		}
	}

	/**
	 * 쓰레드 에러 발생
	 * 
	 * @Method Name : error
	 * @Method Comment :
	 * 
	 * @param key
	 */
	public void error(String key) {
		this.setState(key, Queue.ERROR);
	}

	/**
	 * 작업진행을 계속하는지 검사한다.
	 * 
	 * @param key
	 * @return true : 계속 진행 , false : 중지 신호
	 */
	public boolean isAlive(String key) {
		if (queueMap.containsKey(key)) {
			Queue q = queueMap.get(key);
			return q.isAlive();
		}
		return false;
	}

	/**
	 * 진행중이 작업을 중지 신호를 보낸다.
	 * 
	 * @param key
	 */
	public void stop(String key) {
		if (queueMap.containsKey(key)) {
			Queue q = queueMap.get(key);
			q.setAlive(false);
			q.setState(Queue.STOP);
			queueMap.put(key, q);
		}
	}

	/**
	 * 프로세스 완료
	 * 
	 * @Method Name : complete
	 * @Method Comment :
	 * 
	 * @param key
	 */
	public void complete(String key) {
		if (queueMap.containsKey(key)) {
			Queue q = queueMap.get(key);
			q.setAlive(false);
			q.setState(Queue.COMPLETE);
			queueMap.put(key, q);
		}
	}

	/**
	 * 큐진행상태를 percent 로 제공한다.
	 * 
	 * @Method Name : getPercent
	 * @Method Comment :
	 * 
	 * @param key
	 * @return
	 */
	public long getPercent(String key) {

		if (queueMap.containsKey(key)) {
			Queue q = queueMap.get(key);
			return q.getPercent();
		}
		return 0;
	}

	/**
	 * 총 처리 건수를 제공
	 * 
	 * @Method Name : getTotal
	 * @Method Comment :
	 * 
	 * @param key
	 * @return
	 */
	public long getTotal(String key) {
		if (queueMap.containsKey(key)) {
			Queue q = queueMap.get(key);
			return q.getTotal();
		}
		return 0;
	}

	/**
	 * 현재 진행 건수를 제공
	 * 
	 * @Method Name : getCurrent
	 * @Method Comment :
	 * 
	 * @param key
	 * @return
	 */
	public long getCurrent(String key) {
		if (queueMap.containsKey(key)) {
			Queue q = queueMap.get(key);
			return q.getCurrent();
		}
		return 0;
	}

	/**
	 * 큐의 진행 상태를 제공
	 * 
	 * @Method Name : getState
	 * @Method Comment :
	 * 
	 * @param key
	 * @return
	 */
	public int getState(String key) {
		if (queueMap.containsKey(key)) {
			Queue q = queueMap.get(key);
			return q.getState();
		}
		return Queue.NOINFO;
	}

	/**
	 * 큐의 길이를 구한다.
	 * 
	 * @Method Name : getQueueLength
	 * @Method Comment :
	 * 
	 * @return
	 */
	public int getQueueLength() {
		return queueMap.size();
	}

	/**
	 * 큐정보를 제공
	 * 
	 * @Method Name : getQueue
	 * @Method Comment :
	 * 
	 * @param key
	 * @return
	 */
	public Queue getQueue(String key) {
		if (queueMap.containsKey(key)) {
			return queueMap.get(key);
		}
		return null;
	}
}

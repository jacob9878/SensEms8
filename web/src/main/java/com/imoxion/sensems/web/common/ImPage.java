/*
 * �ۼ��� ��¥: 2005. 2. 17.
 */
package com.imoxion.sensems.web.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author imoxion
 */

public class ImPage {

	private Logger log = LoggerFactory.getLogger( ImPage.class );
	
	private int pageSu;

	private int start;

	private int end;

	private int pageGroupNum;

	private int pageStart;

	private int pageEnd;

	private int nextGroup;

	private int prevGroup;

	private int nextStart;

	private int prevStart;

	private int number;

	private int total;

	private int pageSize;

	private int pageGroupSize;

	private int cpage;
	
	private long esTotal;
	
	private long esNumber;

	public ImPage() {
	}

	/**
	 * ������ ������ ��
	 * 
	 * @param totalCount
	 * @param pagesize
	 * @param pageGroupSize
	 * @param pages
	 */
	/*
	 * public ImPage( int cpage , int pageSize , int total , int pageGroupSize ){ if( cpage < 1 ) cpage = 1; this.cpage = cpage; if( pageSize < 1 ) pageSize = 15; this.pageSize = pageSize; if(
	 * pageGroupSize < 1 ) pageGroupSize = 10; this.pageGroupSize = pageGroupSize;
	 * 
	 * this.total = total;
	 * 
	 * String dbType = ImsConstant.DATABASE_TYPE;
	 * 
	 * if( dbType.equalsIgnoreCase("mysql") ){ mysql(); }else if( dbType.equalsIgnoreCase("oracle") ){ oracle(); }else if( dbType.equalsIgnoreCase("mssql") ){ mysql(); } }
	 */
	/**
	 * 
	 * @param cpage
	 * @param pageSize
	 * @param total
	 * @param pageGroupSize
	 * @param opt
	 *            oracle의 경우 순서가 반대로 나오면 opt = 1
	 */
	public ImPage(int cpage, int pageSize, int total, int pageGroupSize) {
		int opt = ImbConstant.ImConfLoader.getProfileInt("general", "order", 0);

		if (cpage < 1)
			cpage = 1;
		this.cpage = cpage;
		if (pageSize < 1)
			pageSize = 15;
		this.pageSize = pageSize;
		if (pageGroupSize < 1)
			pageGroupSize = 5;
		this.pageGroupSize = pageGroupSize;

		this.total = total;

		String dbType = ImbConstant.DATABASE_TYPE;
		if (dbType.equalsIgnoreCase("mysql")) {
			mysql();
		} else if (dbType.equalsIgnoreCase("oracle")) {
			oracle(opt);
		} else if (dbType.equalsIgnoreCase("mssql")) {
			mysql();
		}
	}
	
	public ImPage(int cpage, int pageSize) {

		if (cpage < 1)
			cpage = 1;
		this.cpage = cpage;
		if (pageSize < 1)
			pageSize = 15;
		this.pageSize = pageSize;
		if (pageGroupSize < 1)
			pageGroupSize = 5;
		

		int start = (cpage - 1) * pageSize;
		if (start < 0)
			start = 0;
		int end = start + pageSize;
		
		this.start = start;
		this.end = end;
	}
	
	public ImPage(int cpage, int pageSize, long esTotal, int pageGroupSize) {

		if (cpage < 1)
			cpage = 1;
		this.cpage = cpage;
		if (pageSize < 1)
			pageSize = 15;
		this.pageSize = pageSize;
		if (pageGroupSize < 1)
			pageGroupSize = 5;
		this.pageGroupSize = pageGroupSize;
		
		this.esTotal = esTotal;

		elasticSearch();
	}
	
	public void elasticSearch() {

		int pageSu = (int) Math.ceil((double) esTotal / pageSize);
		if (pageSu < cpage)
			cpage = pageSu;
		int start = (cpage - 1) * pageSize;
		if (start < 0)
			start = 0;
		int end = start + pageSize;
		int pageGroupNum = (int) Math.ceil((double) esTotal / pageGroupSize);
		int pageStart = (pageGroupNum - 1) * pageGroupSize + 1;
		if (pageStart <= 0)
			pageStart = 1;
		int pageEnd = pageStart + pageGroupSize - 1;

		int nextGroup = pageGroupNum + 1;
		int prevGroup = pageGroupNum - 1; 
		int nextStart = (nextGroup - 1) * pageGroupSize;
		int prevStart = (prevGroup - 1) * pageGroupSize;
		long number = esTotal - start;
		this.pageSu = pageSu;
		this.start = start;
		this.end = end;
		this.pageGroupNum = pageGroupNum;
		this.pageStart = pageStart;
		this.pageEnd = pageEnd;
		this.nextGroup = nextGroup;
		this.prevGroup = prevGroup;
		this.nextStart = nextStart;
		this.prevStart = prevStart;
		this.esNumber = number;
	}
	
	
	public long getEsTotal() {
		return esTotal;
	}

	public long getEsNumber() {
		return esNumber;
	}

	public int getCpage() {
		return cpage;
	}

	public int getEnd() {
		return end;
	}

	public int getNextGroup() {
		return nextGroup;
	}

	public int getNextStart() {
		return nextStart;
	}

	public int getNumber() {
		return number;
	}

	public int getPageEnd() {
		return pageEnd;
	}

	public int getPageGroupNum() {
		return pageGroupNum;
	}

	public int getPageGroupSize() {
		return pageGroupSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPageStart() {
		if (pageStart < 0)
			pageStart = 0;
		return pageStart;
	}

	public int getPageSu() {
		return pageSu;
	}

	public int getPrevGroup() {
		return prevGroup;
	}

	public int getPrevStart() {
		return prevStart;
	}

	public int getStart() {
		if (start < 0)
			start = 0;
		return start;
	}

	public int getTotal() {
		return total;
	}

	public void mysql() {

		int pageSu = (int) Math.ceil((double) total / pageSize);
		if (pageSu < cpage)
			cpage = pageSu;
		int start = (cpage - 1) * pageSize;
		if (start < 0)
			start = 0;		
		int pageGroupNum = (int) Math.ceil((double) total / pageGroupSize);
		int pageStart = (pageGroupNum - 1) * pageGroupSize + 1;
		if (pageStart <= 0)
			pageStart = 1;
		int pageEnd = pageStart + pageGroupSize - 1;

		int nextGroup = pageGroupNum + 1;
		int prevGroup = pageGroupNum - 1; 
		int nextStart = (nextGroup - 1) * pageGroupSize;
		int prevStart = (prevGroup - 1) * pageGroupSize;
		int number = total - start;
		this.pageSu = pageSu;
		this.start = start;
		this.end = pageSize;
		this.pageGroupNum = pageGroupNum;
		this.pageStart = pageStart;
		this.pageEnd = pageEnd;
		this.nextGroup = nextGroup;
		this.prevGroup = prevGroup;
		this.nextStart = nextStart;
		this.prevStart = prevStart;
		this.number = number;
	}

	public void oracle() {
		// if(total == 0) return;

		int pageSu = (int) Math.ceil((double) total / pageSize); // 페이지수
		if (pageSu < cpage)
			cpage = pageSu;
		int start = (total - cpage * pageSize) + 1; // 시작위치
		int end = (start + pageSize) - 1; // 끝위치
		if (start < 1)
			start = 1;

		int pageGroupNum = (int) Math.ceil((double) cpage / pageGroupSize); // 현재
		// 페이지
		// 그룹
		int pageStart = (pageGroupNum - 1) * pageGroupSize + 1; // 페이지그룹의 첫페이지
		if (pageStart <= 0)
			pageStart = 1;
		int pageEnd = pageStart + pageGroupSize - 1; // 페이지그룹의 마지막페이지

		int nextGroup = pageGroupNum + 1; // 다음그룹
		int prevGroup = pageGroupNum - 1; // 이전그룹
		int nextStart = ((nextGroup - 1) * pageGroupSize) + 1; // 다음 페이지 그룹의 첫
		// 페이지
		int prevStart = ((prevGroup - 1) * pageGroupSize) + 1;

		this.pageSu = pageSu;
		this.start = start;
		this.end = end;
		this.pageGroupNum = pageGroupNum;
		this.pageStart = pageStart;
		this.pageEnd = pageEnd;
		this.nextGroup = nextGroup;
		this.prevGroup = prevGroup;
		this.nextStart = nextStart;
		this.prevStart = prevStart;
	}

	public void oracle(int opt) {

		int pageSu = (int) Math.ceil((double) total / pageSize); // 페이지수
		if (pageSu < cpage)
			cpage = pageSu;

		int start = 0;
		int end = 0;

		if (opt == 0) {
			start = (total - cpage * pageSize) + 1; // 시작위치
			end = (start + pageSize) - 1; // 끝위치
			if (start < 0)
				start = 0;
		} else {
			start = pageSize * (cpage - 1); // 시작위치
			end = (pageSize * (cpage - 1)) + pageSize;
		}
		int pageGroupNum = (int) Math.ceil((double) cpage / pageGroupSize); // 현재
		// 페이지
		// 그룹
		int pageStart = (pageGroupNum - 1) * pageGroupSize + 1; // 페이지그룹의 첫페이지
		if (pageStart <= 0)
			pageStart = 1;
		int pageEnd = pageStart + pageGroupSize - 1; // 페이지그룹의 마지막페이지

		int nextGroup = pageGroupNum + 1; // 다음그룹
		int prevGroup = pageGroupNum - 1; // 이전그룹
		int nextStart = ((nextGroup - 1) * pageGroupSize) + 1; // 다음 페이지 그룹의 첫
		// 페이지
		int prevStart = ((prevGroup - 1) * pageGroupSize) + 1;

		this.pageSu = pageSu;
		this.start = start;
		this.end = end;
		this.pageGroupNum = pageGroupNum;
		this.pageStart = pageStart;
		this.pageEnd = pageEnd;
		this.nextGroup = nextGroup;
		this.prevGroup = prevGroup;
		this.nextStart = nextStart;
		this.prevStart = prevStart;
		this.end = end;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setStart(int start) {
		if (start < 0)
			start = 0;
		this.start = start;
	}
}

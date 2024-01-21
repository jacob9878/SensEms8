package com.imoxion.sensems.web.beans;

public class ImportResultBean {

	private int blank = 0;
	private int success = 0;
	private int total = 0;
	private int fail = 0;
	private int same = 0;
	private String sameEmail = "";
	
	public int getBlank() {
		return blank;
	}
	public void setBlank(int blank) {
		this.blank = blank;
	}
	public int getFail() {
		return fail;
	}
	public void setFail(int fail) {
		this.fail = fail;
	}
	public int getSuccess() {
		return success;
	}
	public void setSuccess(int success) {
		this.success = success;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
    public int getSame() {
        return same;
    }
    public void setSame(int same) {
        this.same = same;
    }
    public String getSameEmail() {
        return sameEmail;
    }
    public void setSameEmail(String sameEmail) {
        if (sameEmail != null)
            this.sameEmail = sameEmail;
    }
}

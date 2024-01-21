/*
 *
 *
 */
package com.imoxion.sensems.web.beans;



public class StatBean {
    private String startTime = "";
    private String endTime = "";
    private String senderId = "";
    private int sendCount = 0;
    private int mailCount = 0;
    private int successCount = 0;
    private int failCount = 0;
    private int openCount = 0;
    
    //private int e0 = 0;
    private int e901 = 0;
    private int e902 = 0;
    private int e903 = 0;
    private int e904 = 0;
    private int e905 = 0;
    private int e906 = 0;
    private int e907 = 0;
    private int e908 = 0;
    private int e909 = 0;
    private int e910 = 0;
    private int e911 = 0;
    private int e912 = 0;
    private int e913 = 0;
    private int e914 = 0;
    private int e915 = 0;
    
    private HostCountBean[] hostCountBeans = null;
    
    
    public String getStartTime() {
        return startTime;
    }
    
    
    /*public int getE0() {
		return e0;
	}

	public void setE0(int e0) {
		this.e0 = e0;
	}*/

	public int getE901() {
		return e901;
	}
	public void setE901(int e901) {
		this.e901 = e901;
	}
	public int getE902() {
		return e902;
	}
	public void setE902(int e902) {
		this.e902 = e902;
	}
	public int getE903() {
		return e903;
	}
	public void setE903(int e903) {
		this.e903 = e903;
	}
	public int getE904() {
		return e904;
	}
	public void setE904(int e904) {
		this.e904 = e904;
	}
	public int getE905() {
		return e905;
	}
	public void setE905(int e905) {
		this.e905 = e905;
	}
	public int getE906() {
		return e906;
	}
	public void setE906(int e906) {
		this.e906 = e906;
	}
	public int getE907() {
		return e907;
	}
	public void setE907(int e907) {
		this.e907 = e907;
	}
	public int getE908() {
		return e908;
	}
	public void setE908(int e908) {
		this.e908 = e908;
	}
	public int getE909() {
		return e909;
	}
	public void setE909(int e909) {
		this.e909 = e909;
	}
	public int getE910() {
		return e910;
	}
	public void setE910(int e910) {
		this.e910 = e910;
	}
	public int getE911() {
		return e911;
	}
	public void setE911(int e911) {
		this.e911 = e911;
	}
	public int getE912() {
		return e912;
	}
	public void setE912(int e912) {
		this.e912 = e912;
	}
	public int getE913() {
		return e913;
	}
	public void setE913(int e913) {
		this.e913 = e913;
	}
	public int getE914() {
		return e914;
	}
	public void setE914(int e914) {
		this.e914 = e914;
	}
	public int getE915() {
		return e915;
	}
	public void setE915(int e915) {
		this.e915 = e915;
	}
	public HostCountBean[] getHostCountBeans() {
		HostCountBean[] safeArray = null;
		if (this.hostCountBeans != null) {
			safeArray = new HostCountBean[this.hostCountBeans.length];
			for (int i = 0; i < this.hostCountBeans.length ; i++) {
				safeArray[i] = this.hostCountBeans[i];
			}
		}
		return safeArray;
    }
    public void setHostCountBeans(HostCountBean[] hostCountBeans) {
		this.hostCountBeans = new HostCountBean[hostCountBeans.length];
		for (int i = 0; i < hostCountBeans.length; ++i)
			this.hostCountBeans[i] = hostCountBeans[i];
    }
    public void setStartTime(String startTime) {
        if (startTime != null)
            this.startTime = startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public void setEndTime(String endTime) {
        if (endTime != null)
            this.endTime = endTime;
    }
    public String getSenderId() {
        return senderId;
    }
    public void setSenderId(String senderId) {
        if (senderId != null)
            this.senderId = senderId;
    }
    public int getSendCount() {
        return sendCount;
    }
    public void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }
    public int getMailCount() {
        return mailCount;
    }
    public void setMailCount(int mailCount) {
        this.mailCount = mailCount;
    }
    public int getSuccessCount() {
        return successCount;
    }
    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }
    public int getFailCount() {
        return failCount;
    }
    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }
    public int getOpenCount() {
        return openCount;
    }
    public void setOpenCount(int openCount) {
        this.openCount = openCount;
    }
    
    
}
package com.imoxion.sensems.web.beans;

public class ScheduleBean {

	private String ukey;
    private String oid;
	private String userid;
	private String title;
	private String content;
	private String sch_date;
    private String year;
    private String month;
    private String day;
    private String weekday;
    private String weekdaystr;
    private String type;
    private String typestr;
    private String repeat;
    private String repeatstr;
    private String rep_start;
    private String rep_end;
    private String pubmode;
    private String pubmodestr;
    private String pubmember;
	private String time;
	private String min;
	//private int isshare;
    
    
	public String getContent() {
		return content;
	}
	//public int getIsshare() {
	//	return isshare;
	//}
	public String getMin() {
		return min;
	}
	public String getSch_date() {
		return sch_date;
	}
	public String getTime() {
		return time;
	}
	public String getTitle() {
		return title;
	}
	public String getUkey() {
		return ukey;
	}
	public String getUserid() {
		return userid;
	}
	public void setContent(String content) {
		this.content = content;
	}
	//public void setIsshare(int isshare) {
	//	this.isshare = isshare;
	//}
	public void setMin(String min) {
		this.min = min;
	}
	public void setSch_date(String sch_date) {
		this.sch_date = sch_date;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setUkey(String ukey) {
		this.ukey = ukey;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
    public String getDay() {
        return day;
    }
    public void setDay(String day) {
        this.day = day;
    }
    public String getMonth() {
        return month;
    }
    public void setMonth(String month) {
        this.month = month;
    }
    public String getPubmode() {
        return pubmode;
    }
    public void setPubmode(String pubmode) {
        this.pubmode = pubmode;
    }
    public String getRep_end() {
        return rep_end;
    }
    public void setRep_end(String rep_end) {
        this.rep_end = rep_end;
    }
    public String getRep_start() {
        return rep_start;
    }
    public void setRep_start(String rep_start) {
        this.rep_start = rep_start;
    }
    public String getRepeat() {
        return repeat;
    }
    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getWeekday() {
        return weekday;
    }
    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }
    public String getYear() {
        return year;
    }
    public void setYear(String year) {
        this.year = year;
    }
    public String getOid() {
        return oid;
    }
    public void setOid(String oid) {
        this.oid = oid;
    }
    public String getPubmember() {
        return pubmember;
    }
    public void setPubmember(String pubmember) {
        this.pubmember = pubmember;
    }
    public String getPubmodestr() {
        return pubmodestr;
    }
    public void setPubmodestr(String pubmodestr) {
        this.pubmodestr = pubmodestr;
    }
    public String getRepeatstr() {
        return repeatstr;
    }
    public void setRepeatstr(String repeatstr) {
        this.repeatstr = repeatstr;
    }
    public String getTypestr() {
        return typestr;
    }
    public void setTypestr(String typestr) {
        this.typestr = typestr;
    }
    public String getWeekdaystr() {
        return weekdaystr;
    }
    public void setWeekdaystr(String weekdaystr) {
        this.weekdaystr = weekdaystr;
    }
    
    
}

package com.imoxion.sensems.web.common;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ImParam {

	protected Logger log = LoggerFactory.getLogger(ImParam.class);
	
	private Map map = null;

	public ImParam(HttpServletRequest request) {
		this.map = new HashMap();
		Enumeration parameters = request.getParameterNames();
		while (parameters.hasMoreElements()) {
			String param = (String)parameters.nextElement();
			String[] params = request.getParameterValues( param );			
			if( params != null && params.length > 1 ){
				this.map.put( param , params );
			}else{
				this.map.put( param , request.getParameter( param ) );
			}			
		}
	}

	public String get(String key) {
		String value = "";
		if (this.map != null) {
			if (map.get(key) != null) {
				value = map.get(key).toString();
			}
		}
		return ImStringUtil.trim(value);
	}
	
	public String getDecode(String key) {
		String value = "";
		if (this.map != null) {
			if (map.get(key) != null) {
				value = map.get(key).toString();
			}
		}
		String decode_str = value;
		try {
			decode_str = URLDecoder.decode(value,"UTF-8");	
		} catch (UnsupportedEncodingException e) {
			String errorId = ErrorTraceLogger.log(e);
			log.error("{}, getDecode ERROR ",errorId);
		} catch (Exception ee) {
			String errorId = ErrorTraceLogger.log(ee);
			log.error("{}, getDecode ERROR ",errorId);
		}
		return ImStringUtil.trim( decode_str );
	}

	public String[] getArray(String key) {
		String[] value = null;
		if (this.map != null) {
			if (map.get(key) != null) {
				if( map.get(key) instanceof String ){
					value = new String[1];
					value[0] = (String) map.get(key);
				}else{
					value = (String[]) map.get(key);
				}
			}
		}
		return value;
	}

	public String getDate(String key) {
		String value = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (this.map != null) {
			if (map.get(key) != null) {
				try {
					value = sdf.format(map.get(key));
				}catch (NullPointerException ne) {
					String errorId = ErrorTraceLogger.log(ne);
					log.error("{}, getDate ERROR ",errorId);
				}
				catch (Exception e) {
					String errorId = ErrorTraceLogger.log(e);
					log.error("{}, getDate ERROR ",errorId);
				}
			}
		}
		return value;
	}

	public String getDate(String key, String pattern) {
		String value = null;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		if (this.map != null) {
			if (map.get(key) != null) {
				try {
					value = sdf.format(map.get(key));
				}catch ( NullPointerException ne) {
					String errorId = ErrorTraceLogger.log(ne);
					log.error("{}, getDate ERROR ",errorId);
				}
				catch (Exception e) {
					String errorId = ErrorTraceLogger.log(e);
					log.error("{}, getDate ERROR ",errorId);
				}
			}
		}
		return value;
	}

	public double getDouble(String key) {
		double value = 0;
		if (this.map != null) {
			if (map.get(key) != null) {
				try {
					value = Double.parseDouble(map.get(key).toString());
				}catch (NullPointerException ne) {
					String errorId = ErrorTraceLogger.log(ne);
					log.error("{}, getDouble ERROR ",errorId);
				}
				catch (Exception e) {
					String errorId = ErrorTraceLogger.log(e);
					log.error("{}, getDouble ERROR ",errorId);
				}
			}
		}
		return value;
	}

	public float getFloat(String key) {
		float value = 0;
		if (this.map != null) {
			if (map.get(key) != null) {
				try {
					value = Float.parseFloat(map.get(key).toString());
				}catch (NullPointerException ne) {
					String errorId = ErrorTraceLogger.log(ne);
					log.error("{}, getFloat ne ERROR ",errorId);
				}
				catch (Exception e) {
					String errorId = ErrorTraceLogger.log(e);
					log.error("{}, getFloat ERROR ",errorId);
				}
			}
		}
		return value;
	}

	public int getInt(String key) {
		int value = 0;
		if (this.map != null) {
			if (map.get(key) != null) {
				try {
					if (map.get(key).toString().indexOf(".") > -1) {
						value = (int) Float.parseFloat(map.get(key).toString());
					} else {
						value = Integer.parseInt(map.get(key).toString());
					}
				}catch (NullPointerException ne ) {
					String errorId = ErrorTraceLogger.log(ne);
					log.error("{}, getInt ERROR ",errorId);
				}
				catch (Exception e) {
					String errorId = ErrorTraceLogger.log(e);
					log.error("{}, getInt ERROR ",errorId);
				}
			}
		}
		return value;
	}

	public long getLong(String key) {
		long value = 0;
		if (this.map != null) {
			if (map.get(key) != null) {
				try {
					value = (long) Double.parseDouble(map.get(key).toString());
				}catch (NullPointerException ne) {
					log.error("getLong error");
				}
				catch (Exception e) {
					log.error("getLong error");
				}
			}
		}
		return value;
	}

	public Map getObject() {
		return this.map;
	}

	public void put(Object key, Object value) {
		map.put(key, value);
	}
	
	/**
	 * 넘어온 파라미터나 값이 "" 일경우 false 
	 * @param key
	 * @return
	 */
	public boolean hasParam(String key ){
		boolean hasParam = false;
		if( map.containsKey( key ) ){			
			if( !map.get(key).toString().equals("") ){
				hasParam = true;
			}
		}
		return hasParam;
	}
}

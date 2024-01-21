/**
 * 
 */
package com.imoxion.sensems.web.util;

import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * @author : jhpark
 * @date : 2021. 02. 02.
 * @desc :
 * 
 */
public class JSONResult extends ModelAndView {

	private Logger logger = LoggerFactory.getLogger(JSONResult.class);
	/**
	 * 작업 성공 JSON 호출시 응답 코드로 사용.
	 */
	public static final int SUCCESS = 1;

	/**
	 * 작업 실패 JSON 호출시 응답 코드로 사용.
	 */
	public static final int FAIL = -1;

	public JSONResult(){
		this.setViewName("jsonView");
	}

	public void setResultCode(int resultCode) {
		this.addObject("code",resultCode);
		this.addObject("resultCode",resultCode);
	}

	public void setMessage(String message) {
		this.addObject("message",message);
	}

	public void setData(JSONObject data) {
		this.addObject("data",data);
	}

    public void setErrors(List<FieldError> errors) {
	    this.addObject("errors",errors);
    }

	public void setCode(int code) {
		this.addObject("code",code);
	}

	public String toString() {
		JSONObject result = new JSONObject();
		ModelMap modelMap = this.getModelMap();
		result.put("code", modelMap.get("code"));
		result.put("result", modelMap.get("code"));
		result.put("message", modelMap.get("message"));
		result.put("data", modelMap.get("data"));
        if( modelMap.containsKey("errors") ){
            result.put("errors", modelMap.get("errors") );
        }
		return result.toString();
	}
}
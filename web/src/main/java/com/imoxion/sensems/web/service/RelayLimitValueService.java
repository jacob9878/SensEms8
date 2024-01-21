package com.imoxion.sensems.web.service;

import com.imoxion.common.util.ImCheckUtil;
import com.imoxion.sensems.common.logger.ErrorTraceLogger;
import com.imoxion.sensems.web.database.domain.LimitValueBean;
import com.imoxion.sensems.web.database.domain.RelayLimitValue;
import com.imoxion.sensems.web.database.mapper.RelayLimitValueMapper;
import com.imoxion.sensems.web.exception.AdminException;
import com.imoxion.sensems.web.form.RelayLimitValueForm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * 송신제한설정 Service클래스
 * @author minideji
 *
 */

@Service
public class RelayLimitValueService {

    protected Logger log = LoggerFactory.getLogger(RelayLimitValueService.class);

    @Autowired
    private RelayLimitValueMapper relayLimitValueMapper;

    @Autowired
    private MessageSourceAccessor message;

    private Map<String, String> serverConfigureMap = new HashMap<String, String>();


/**
     * 서버 설정값 가져오기
     *
     * @Method Name : getServerConfing
     * @Method Comment :
     *
     * @param limit_type
     * @return
     */

    public String getServerConfigValue(String limit_type) {
        String value = null;
        if (serverConfigureMap.containsKey(limit_type)) {
            value = serverConfigureMap.get(limit_type);
        }
        return value;
    }


    /**
     * 메일 제한값 설정 업데이트
     * @param limit_value
     * @param limit_type
     * @return
     * @throws Exception
     */

    public int updateLimitValue(String limit_value, String limit_type) throws Exception {
        if (!ImCheckUtil.isNotEmpty(limit_type) ||  !ImCheckUtil.isNotEmpty(limit_value)) {
            log.error("Argument is limitType = {}", limit_type);
            log.error("Argument is limit_value = {}", limit_value);
            throw new AdminException(AdminException.NO_REQUIRED_ITEM);
        }

        int i_limitType = 0;
        // 입력된 값 타입에 따라 저장 가능 여부 판단
        if ( LimitValueBean.MASS_QUEUE_SWITCH_LIMIT_VALUE.equalsIgnoreCase(limit_type)
                || LimitValueBean.MASS_QUEUE_SWITCH_FROM_LIMIT_COUNT_VALUE.equalsIgnoreCase(limit_type)
                || LimitValueBean.MASS_QUEUE_SWITCH_FROM_LIMIT_SIZE_VALUE.equalsIgnoreCase(limit_type)
                || LimitValueBean.MAX_RECEIVER_COUNT.equalsIgnoreCase(limit_type)
                || LimitValueBean.SMTP_SAME_TIME_MAX_MAIL_SIZE.equalsIgnoreCase(limit_type)
                || LimitValueBean.SMTP_MAX_MAIL_SIZE.equalsIgnoreCase(limit_type) ) {
            // 숫자만 입력이 가능하며 정수형으로 제한이 가능하도록
            try {
                i_limitType = Integer.parseInt(limit_value);
            } catch (Exception e) {
                log.error("Error is ", e);
                log.error("Argument is limit_value = {}", limit_value);
                throw new AdminException(AdminException.INCORRECT_INTEGER_VALUE);
            }
        }
        if(LimitValueBean.MASS_QUEUE_SWITCH_LIMIT_VALUE.equalsIgnoreCase(limit_type)
        && i_limitType > 10000 ){ // 대용량큐로 전환되는 건당 메일 크기 10000MB 제한
            log.error("Argument for {}(limit type) is Incorrect  {}",limit_type, limit_value);
            throw new AdminException(AdminException.INCORRECT_INTEGER_VALUE);
        }
        if(LimitValueBean.MASS_QUEUE_SWITCH_FROM_LIMIT_COUNT_VALUE.equalsIgnoreCase(limit_type)
        && i_limitType > 100000 ){ //대용량큐로 전환되는 From의 시간당 메일 건수 100000건 제한
            log.error("Argument for {}(limit type) is Incorrect  {}",limit_type, limit_value);
            throw new AdminException(AdminException.INCORRECT_INTEGER_VALUE);
        }
        if(LimitValueBean.MASS_QUEUE_SWITCH_FROM_LIMIT_SIZE_VALUE.equalsIgnoreCase(limit_type)
        && i_limitType > 10000 ){ //대용량큐로 전환되는 From의 시간당 메일 총 용량 10000(MB)/10GB
            log.error("Argument for {}(limit type) is Incorrect  {}",limit_type, limit_value);
            throw new AdminException(AdminException.INCORRECT_INTEGER_VALUE);
        }
        if(LimitValueBean.MAX_RECEIVER_COUNT.equalsIgnoreCase(limit_type)
        && i_limitType > 10000000 ){ // 1회 발송시 최대 수신자 수 10000000
            log.error("Argument for {}(limit type) is Incorrect  {}",limit_type, limit_value);
            throw new AdminException(AdminException.INCORRECT_INTEGER_VALUE);
        }
        if(LimitValueBean.SMTP_MAX_MAIL_SIZE.equalsIgnoreCase(limit_type)
        && i_limitType > 1000000 ){ //1회 발송 시 최대 메일 크기(MB)
            log.error("Argument for {}(limit type) is Incorrect  {}",limit_type, limit_value);
            throw new AdminException(AdminException.INCORRECT_INTEGER_VALUE);
        }
        if(LimitValueBean.SMTP_SAME_TIME_MAX_MAIL_SIZE.equalsIgnoreCase(limit_type)
        && i_limitType > 10000000 ){ //1회 발송 시 최대 총 메일 크기(수신자수 * 메일크기, MB)
            log.error("Argument for {}(limit type) is Incorrect  {}",limit_type, limit_value);
            throw new AdminException(AdminException.INCORRECT_INTEGER_VALUE);
        }

        int result = 0;
        try {
            LimitValueBean limitValueBean = new LimitValueBean();
            limitValueBean.setLimit_type(limit_type);
            limitValueBean.setLimit_value(limit_value);
            result = relayLimitValueMapper.updateLimitValue(limitValueBean);
        } catch (Exception e) {
            String errorId = ErrorTraceLogger.log(e);
            log.error("Argument is limitType = {}", limit_type);
            log.error("Argument is limit_value = {}", limit_value);
            log.error("{} - {}:{}", errorId, ErrorTraceLogger.getMethodName(e), e.getMessage());
            throw new AdminException(AdminException.DB_ERROR);
        }
        return result;
    }

    public void loadLimitValue(){
        List<RelayLimitValue> relayLimitValues = relayLimitValueMapper.getLimitValueList();
        for( RelayLimitValue value : relayLimitValues ){
            serverConfigureMap.put(value.getLimit_type(), value.getLimit_value());
            log.debug("relayLimitValueService.loadLimitValue: {} = {}",  value.getLimit_type(), value.getLimit_value());
        }

        log.info("relayLimitValueService.loadLimitValue - OK");
    }


/**
     *
     * @Method Name : getLimitServerIdList
     * @Method Comment : 메일 제한값 설정의 서버 아이디 취득
     *
     * @return
     */

    public RelayLimitValueForm getLimitValueList() throws Exception {

        RelayLimitValueForm form = new RelayLimitValueForm();

        List<RelayLimitValue> limitValueList = null;

        // 메일 한계값 설정 추가를 위한 값
        // 메세지 때문에 여기서 설정함...다국어 위해
        // DB에 저장해서 쓰려면 영어로 저장해야할 듯
        Map<String, String> discriptMap = new HashMap<String, String>();
        discriptMap.put(RelayLimitValue.MAX_RECEIVER_COUNT,
                message.getMessage("MAX_RECEIVER_COUNT", "1회 발송시 최대 수신자 수"));
        discriptMap.put(RelayLimitValue.SMTP_MAX_MAIL_SIZE,
                message.getMessage("SMTP_MAX_MAIL_SIZE", "SMTP 발송 및 수신 최대 메일 크기(MB)"));

        discriptMap.put(RelayLimitValue.MASS_QUEUE_SWITCH_LIMIT_VALUE,
                message.getMessage("MASS_QUEUE_SWITCH_LIMIT_VALUE", "대용량 큐로 넘어가는 한계 용량값"));

        discriptMap.put(RelayLimitValue.MASS_QUEUE_SWITCH_FROM_LIMIT_COUNT_VALUE,
                message.getMessage("MASS_QUEUE_SWITCH_FROM_LIMIT_COUNT_VALUE", "대용량 큐로 넘어가는 From 한계 메일 수"));

        discriptMap.put(RelayLimitValue.MASS_QUEUE_SWITCH_FROM_LIMIT_SIZE_VALUE,
                message.getMessage("MASS_QUEUE_SWITCH_FROM_LIMIT_SIZE_VALUE", "대용량 큐로 넘어가는 From 한계 총 용량"));

        discriptMap.put(RelayLimitValue.SMTP_SAME_TIME_MAX_MAIL_SIZE,
                message.getMessage("SMTP_SAME_TIME_MAX_MAIL_SIZE", "1회 발송 시 최대 총 메일 크기(수신자수 * 메일크기, MB)"));

        // 현재 설정된 메일 한계 값 설정 목록 취득
        limitValueList = relayLimitValueMapper.getLimitValueList();

        // 현재 있는 타입은 제거
        for (RelayLimitValue limit : limitValueList) {
            limit.setDescript(discriptMap.get(limit.getLimit_type()));
            discriptMap.remove(limit.getLimit_type());
        }

        form.setLimitValueList(limitValueList);
        form.setDiscriptMap(discriptMap);

        return form;

    }


/**
     *
     * @param limit_type
     * @return
     */

    public RelayLimitValueForm getLimitValue(String  limit_type) {
        RelayLimitValue relayLimitValue =  relayLimitValueMapper.getLimitValue(limit_type);

        //등록되어있는 정보가 없다면 에러!
        if(relayLimitValue == null || StringUtils.isEmpty(relayLimitValue.getLimit_type())) {
            return null;
        }
        RelayLimitValueForm form = new RelayLimitValueForm();
        form.setDescript(relayLimitValue.getDescript());
        form.setLimit_type(relayLimitValue.getLimit_type());
        form.setLimit_value(relayLimitValue.getLimit_value());
        return form;
    }


/**
     * 숫자 정규식
     * @param value
     * @return
     */

    public boolean isnumber(String value){
        String regExp = "^[0-9]+$";
        if(Pattern.matches(regExp, value)){
            return true;
        }
        return false;
    }

}



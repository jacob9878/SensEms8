/**
 *
 */
package com.imoxion.sensems.web.exception.message;

import com.imoxion.sensems.web.database.domain.LimitValueBean;
import org.springframework.stereotype.Component;

/**
 * @author : -
 * @date : -
 * @desc : 발송제한 Controller에서 사용하는 에러 메세지
 *
 */
@Component
public class LimitErrorMessage  {

    // TODO 각 에러메시지에 표시되는 최대치값을 가변적으로 처리하도록 보완 필요

    public String getMessage(String error_code) {
        String errorMessage = null;
        switch (error_code) {
            case LimitValueBean.MASS_QUEUE_SWITCH_LIMIT_VALUE:
                errorMessage = "해당 설정은 10,000MB이상 설정할수 없습니다.";
                break;
            case LimitValueBean.MASS_QUEUE_SWITCH_FROM_LIMIT_COUNT_VALUE:
                errorMessage = "해당 설정은 100,000건 이상 설정할수 없습니다.";
                break;
            case LimitValueBean.MASS_QUEUE_SWITCH_FROM_LIMIT_SIZE_VALUE:
                errorMessage ="해당 설정은 10,000(MB)이상 설정할수 없습니다.";
                break;
            case LimitValueBean.MAX_RECEIVER_COUNT:
                errorMessage = "해당 설정은 10,000,000 이상 설정할수 없습니다.";
                break;
            case LimitValueBean.SMTP_MAX_MAIL_SIZE:
                errorMessage = "해당 설정은 1,000,000(MB) 이상 설정할수 없습니다.";
                break;
            case LimitValueBean.SMTP_SAME_TIME_MAX_MAIL_SIZE:
                errorMessage = "해당 설정은 10,000,000 이상 설정할수 없습니다.";
                break;
            default:
                errorMessage = "에러가 발생하였습니다.";
                break;
        }

        return errorMessage;
    }

}

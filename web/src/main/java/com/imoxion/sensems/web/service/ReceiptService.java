package com.imoxion.sensems.web.service;


import com.imoxion.common.util.ImStringUtil;
import com.imoxion.security.ImSecurityLib;
import com.imoxion.sensems.web.beans.*;
import com.imoxion.sensems.web.common.ImPage;
import com.imoxion.sensems.web.common.ImbConstant;
import com.imoxion.sensems.web.database.mapper.ReceiptMapper;
import com.imoxion.sensems.web.form.ReceiptForm;
import com.imoxion.sensems.web.util.ImUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ReceiptService {

    private Logger log = LoggerFactory.getLogger(ReceiptService.class);
    @Autowired
    private ReceiptMapper receiptMapper;

    public ReceiptResultBean search(ReceiptForm form, int pagesize) throws Exception {
        ReceiptResultBean result = new ReceiptResultBean();
        ReceiptBean receiptBean = new ReceiptBean();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        receiptBean.setSearchKeywordMsgid(form.getSearchKeywordMsgid());
        receiptBean.setSearchKeywordRcode(form.getSearchKeywordRcode());
        ImPage pageInfo = new ImPage(ImStringUtil.parseInt(form.getCpage()), pagesize,
                1, ImStringUtil.parseInt(form.getPagesize()));
        // msgid와 rcode 두개로 검색했을 때 나오는 검색 결과 리스트가 나와야한다.
        List<ReceiptBean> resultList = receiptMapper.selectReceiptRecvList(form.getSearchKeywordMsgid(),form.getSearchKeywordRcode());
        String msg_name = receiptMapper.getMsgName(form.getSearchKeywordMsgid());
        String mail_from = receiptMapper.getMailFrom(form.getSearchKeywordMsgid());

        // imb_emsmain 테이블의 mail_from 이라는 컬럼이 아이모션<imoxion@imoxion.com> 형태로 저장되는데 <까지 자르고 >도 잘라야해서 만든 변수.
        int beginIndex = mail_from.indexOf("<")+1;
        int endindex = mail_from.indexOf(">");
        mail_from =mail_from.substring(beginIndex, endindex);

        //수신확인 목록의 이메일을 불러와서 복호화 시켜줘야 화면 목록상에 암호화된 이메일 주속 값이 안나옴.
        for (int i = 0; i<resultList.size(); i++){
            resultList.get(i).setField1(ImSecurityLib.decryptAES256(ImbConstant.DATABASE_AES_KEY, resultList.get(i).getField1()));

            Date date = sdf.parse(resultList.get(i).getRecv_time());

            resultList.get(i).setRecv_time(ImUtility.getDateFormat(date, "yyyy-MM-dd HH:mm"));
        }
        result.setResultlist(resultList);
        result.setPageInfo(pageInfo);
        result.setMailfrom(mail_from);
        result.setMsg_name(msg_name);

        return result;
    }


}

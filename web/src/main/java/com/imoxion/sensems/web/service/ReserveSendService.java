package com.imoxion.sensems.web.service;

import com.imoxion.common.util.ImStringUtil;
import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.web.beans.ReserveSendBean;
import com.imoxion.sensems.web.beans.UserInfoBean;
import com.imoxion.sensems.web.database.domain.ImbMessage;
import com.imoxion.sensems.web.database.mapper.ReserveSendMapper;
import com.imoxion.sensems.web.form.ReserveSendForm;
import com.imoxion.sensems.web.form.ReserveSendListForm;
import org.apache.commons.lang.StringUtils;
import org.docx4j.wml.P;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
@Service
public class ReserveSendService {

    @Autowired
    ReserveSendMapper reserveSendMapper;

    /**
     * 해당되는 사용자의 정기발송 카운트를 가져온다.
     * (관리자 or 사용자 )
     * userid가 없으면 조건없이 모든 카운트 처리
     * @param userid
     * @return
     */
    public int reserveUserSendTotalCount(String userid) {
        return reserveSendMapper.reserveUserSendTotalCount(userid);
    }

    /**
     * 정기예약 리스트 형성을 위한 목록을 불러온다. (관리자 or 사용자 )
     * userid가 없으면 관리자로 처리
     * @param start
     * @param end
     * @return
     */
    public List<ReserveSendListForm> getReserveSendList(int start, int end, String userid){
        return  reserveSendMapper.getReserveSendList(start, end, userid);
    }

    /**
     * 정기예약발송 쓰기 페이지를 형성하기 위한 ReserveSendForm을 setting
     * @param userInfo
     * @return
     */
    public ReserveSendForm getWriteForm(UserInfoBean userInfo) {
        ReserveSendForm form = new ReserveSendForm();
        String from =  userInfo.getEmail();

            if(StringUtils.isNotEmpty(userInfo.getName())){
                from = userInfo.getName() + "<" +from + ">";
            }

        form.setMail_from(from);
        form.setIshtml("1");
        form.setReplyto(userInfo.getEmail());
        return form;
    }

    /**
     * 정기예약발송을 등록한다.
     * imbMessage setting and insert
     * reserveSendBean setting and insert
     * @param reserveSendForm
     */
    public void reserveRegist(ReserveSendForm reserveSendForm) {
        String ukey = ImUtils.makeKeyNum(24);

        //imbMessage setting and insert
        ImbMessage imbMessage = new ImbMessage();
        imbMessage.setMsgid(ukey);
        imbMessage.setContents(reserveSendForm.getContent());
        reserveSendMapper.reserveRegistMsg(imbMessage);

        //reserveSendBean setting and insert
        ReserveSendBean reserveSendBean = new ReserveSendBean();
        reserveSendBean.setCategoryid(reserveSendForm.getCategoryid());
        reserveSendBean.setCharset(reserveSendForm.getCharset());
        reserveSendBean.setReplyto(reserveSendForm.getReplyto());
        reserveSendBean.setStart_time(reserveSendForm.getStart_time().replaceAll("-",""));
        reserveSendBean.setEnd_time(reserveSendForm.getEnd_time().replaceAll("-",""));
        String sendtime = reserveSendForm.getReserve_hour()+reserveSendForm.getReserve_minute();

        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddhhmm");
        reserveSendBean.setRegdate(date.format(new Date()));
        reserveSendBean.setSend_time(sendtime);
        reserveSendBean.setIshtml(reserveSendForm.getIshtml());
        reserveSendBean.setIslink(reserveSendForm.getIslink());
        reserveSendBean.setMsgid(ukey);
        reserveSendBean.setRecid(reserveSendForm.getRecid());
        reserveSendBean.setMail_from(reserveSendForm.getMail_from());
        reserveSendBean.setMsg_name(reserveSendForm.getMsg_name());
        reserveSendBean.setUserid(reserveSendForm.getUserid());
        reserveSendBean.setRot_flag(reserveSendForm.getRot_flag());
        reserveSendBean.setRot_point(reserveSendForm.getRot_point());

        reserveSendMapper.reserveRegist(reserveSendBean);

    }

    /**
     * 수신자 목록을 불러온다.
     * @param msgid
     * @return
     */
    public ReserveSendBean getReserveInfo(String msgid) {
        return reserveSendMapper.getReserveInfo(msgid);
    }

    public String getContent(String msgid) {
        return reserveSendMapper.getContent(msgid);
    }

    public ReserveSendForm convertForm(ReserveSendBean reserveSendBean, String content) {
        ReserveSendForm reserveSendForm = new ReserveSendForm();

        reserveSendForm.setUserid(reserveSendBean.getUserid());
        reserveSendForm.setMsgid(reserveSendBean.getMsgid()); // 메세지 ukey
        reserveSendForm.setMail_from(reserveSendBean.getMail_from()); // 보내는 사람
        reserveSendForm.setReplyto(reserveSendBean.getReplyto()); // 회신주소
        reserveSendForm.setMsg_name(reserveSendBean.getMsg_name()); // 제목
        String regdate = reserveSendBean.getRegdate().substring(0,4) +"-"+reserveSendBean.getRegdate().substring(4,6) +"-"+ reserveSendBean.getRegdate().substring(6,8) +" " + reserveSendBean.getRegdate().substring(8, 10) +":"+reserveSendBean.getRegdate().substring(10, 12);
        reserveSendForm.setRegdate(regdate); // 등록일
        reserveSendForm.setRot_flag(reserveSendBean.getRot_flag()); // 반복일정 0:매일, 1:매주, 2:매월

        // 발송 시간
        if(StringUtils.isNotEmpty(reserveSendBean.getSend_time())){
            String send_time = reserveSendBean.getSend_time().substring(0,2) + ":"+reserveSendBean.getSend_time().substring(2,4);
            reserveSendForm.setSend_time(send_time);
        }
        // 발송시작일
        if(StringUtils.isNotEmpty(reserveSendBean.getStart_time())){
            String start_time  =  reserveSendBean.getStart_time().substring(0,4) +"-"+reserveSendBean.getStart_time().substring(4,6) +"-"+ reserveSendBean.getStart_time().substring(6,8);
            reserveSendForm.setStart_time(start_time); // 발송시작일
        }

        // 발송종료일
        if(StringUtils.isNotEmpty(reserveSendBean.getEnd_time())){
            String end_time  =  reserveSendBean.getEnd_time().substring(0,4) +"-"+reserveSendBean.getEnd_time().substring(4,6) +"-"+ reserveSendBean.getEnd_time().substring(6,8);
            reserveSendForm.setEnd_time(end_time); // 발송종료일 yyyyMMdd
        }

        // 최근 발송일
        if(StringUtils.isNotEmpty(reserveSendBean.getLast_send())){
            String last_send = reserveSendBean.getLast_send().substring(0,2) + ":"+reserveSendBean.getLast_send().substring(2,4);
            reserveSendForm.setLast_send(last_send); //최근 발송일
        }

        //charset 인코딩
        reserveSendForm.setCharset(reserveSendBean.getCharset());
        //ishtml html , Text 타입
        reserveSendForm.setIshtml(reserveSendBean.getIshtml());
        //카테고리 category
        reserveSendForm.setCategoryid(reserveSendBean.getCategoryid());
        //수신그룹 recid
        reserveSendForm.setRecid(reserveSendBean.getRecid());
        //반복설정일
        reserveSendForm.setRot_point(reserveSendBean.getRot_point());
        // 본문내용
        reserveSendForm.setContent(content);
        // 업데이트 플래그를 1로 설정한다.
        reserveSendForm.setUpdate_flag("1");

        return reserveSendForm;
    }

    /**
     * 정기예약발송을 삭제한다.
     * @param ukeys
     */
    public boolean deleteReserveList(String[] ukeys, UserInfoBean userInfoBean) {
        boolean checker = false;

        String permission = userInfoBean.getPermission();
        String userid = userInfoBean.getUserid();

        for(int i=0;i<ukeys.length;i++){
            if (UserInfoBean.UTYPE_ADMIN.equals(permission)) { // 삭제 권한이 존재하는 사용자인지 권한 체크
                checker = true;
                removeReserveSendLogic(ukeys[i]);   // 메시지 및 본문을 삭제한다.
            }else{ // 삭제권한이 없다면 게시자 본인인지 체크
                ReserveSendBean reserveSendBean = reserveSendMapper.getReserveInfo(ukeys[i]);

                if(reserveSendBean != null){
                    if(reserveSendBean.getUserid().equals(userid)){ // 등록된 값과 시도하는 사용자 아이디가 같다면 권한이 있는 사용자로 판단
                        removeReserveSendLogic(ukeys[i]);     // 메시지 및 본문을 삭제한다.
                        checker = true;
                    }
                }else{ // 예외로 null 인 경우이므로 false로 리턴
                    checker = false;
                    return checker;
                }
            } // else end
        } // if end

        return checker;
    }

    /**
     * 등록된 정계예약발송 데이터 수정하기 위한 form data를 setting한다.
     * @param reserveSendForm
     */
    public void reserveModify(ReserveSendForm reserveSendForm) {
        //imbMessage setting and update
        ImbMessage imbMessage = new ImbMessage();
        imbMessage.setMsgid(reserveSendForm.getMsgid());
        imbMessage.setContents(reserveSendForm.getContent());
        reserveSendMapper.modifyReserveSendContent(imbMessage);

        //reserveSendBean setting and update
        ReserveSendBean reserveSendBean = new ReserveSendBean();
        reserveSendBean.setCategoryid(reserveSendForm.getCategoryid());
        reserveSendBean.setCharset(reserveSendForm.getCharset());
        reserveSendBean.setReplyto(reserveSendForm.getReplyto());
        reserveSendBean.setStart_time(reserveSendForm.getStart_time().replaceAll("-",""));
        reserveSendBean.setEnd_time(reserveSendForm.getEnd_time().replaceAll("-",""));
        String sendtime = reserveSendForm.getReserve_hour()+reserveSendForm.getReserve_minute();

        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddhhmm");
        reserveSendBean.setRegdate(date.format(new Date()));
        reserveSendBean.setSend_time(sendtime);
        reserveSendBean.setIshtml(reserveSendForm.getIshtml());
        reserveSendBean.setIslink(reserveSendForm.getIslink());
        reserveSendBean.setMsgid(reserveSendForm.getMsgid());
        reserveSendBean.setRecid(reserveSendForm.getRecid());
        reserveSendBean.setMail_from(reserveSendForm.getMail_from());
        reserveSendBean.setMsg_name(reserveSendForm.getMsg_name());
        reserveSendBean.setUserid(reserveSendForm.getUserid());
        reserveSendBean.setRot_flag(reserveSendForm.getRot_flag());
        reserveSendBean.setRot_point(reserveSendForm.getRot_point());

        reserveSendMapper.modifyReserveSend(reserveSendBean);

    }

    /**
     * 메시지와 본문을 삭제한다.
     * @param ukey
     */
    public void removeReserveSendLogic(String ukey){
        reserveSendMapper.deleteReserveSend(ukey);
        reserveSendMapper.deleteReserveSendContent(ukey);
    }

}

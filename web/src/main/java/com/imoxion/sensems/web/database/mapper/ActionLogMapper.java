package com.imoxion.sensems.web.database.mapper;

import com.imoxion.sensems.web.beans.ActionLogBean;
import com.imoxion.sensems.web.database.domain.ImbActionLog;
import com.imoxion.sensems.web.database.domain.ImbActionMenu;
import org.mybatis.spring.annotation.MapperScan;

import java.util.List;

@MapperScan
public interface ActionLogMapper {

    /**
     * 로그 기록
     *
     * @param imbActionLog
     */
    public void insertActionLog(ImbActionLog imbActionLog);


    /**
     * 로그 메뉴 리스트
     *
     */
    public List<ImbActionMenu> selectActionMenu();

    /**
     * 로그 count
     *
     * @param actionLogBean
     */
    public int selectActionLogCount(ActionLogBean actionLogBean);

    /**
     * 로그 리스트
     *
     * @param actionLogBean
     */
    public List<ImbActionLog> selectActionLogList(ActionLogBean actionLogBean);

    /**
     * 활동로그 전체 목록 조회 (전체 검색결과 목록 다운로드에 사용)
     * @param actionLogBean
     * @return
     */
    public List<ImbActionLog> selectAllActionLogList(ActionLogBean actionLogBean);
}

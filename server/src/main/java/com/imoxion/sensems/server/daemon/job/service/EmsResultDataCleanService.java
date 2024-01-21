package com.imoxion.sensems.server.daemon.job.service;

import com.imoxion.common.util.ImConfLoaderEx;
import com.imoxion.common.util.ImTimeUtil;
import com.imoxion.sensems.server.config.ImEmsConfig;
import com.imoxion.sensems.server.domain.ImbEmsAttach;
import com.imoxion.sensems.server.domain.ImbEmsMain;
import com.imoxion.sensems.server.logger.ErrorTraceLogger;
import com.imoxion.sensems.server.repository.*;
import com.imoxion.sensems.server.service.FileDeleteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.List;

public class EmsResultDataCleanService {

	private Logger logger = LoggerFactory.getLogger("DAEMON");

	public void run() {
		ImConfLoaderEx conf = ImConfLoaderEx.getInstance("sensems.home","sensems.xml");
		
		try {
			// 자동 삭제 대기일
			int delayDays = conf.getProfileInt("general.ems_data_delete_day", 365);
			logger.info("Ems send result delete. delay days :{}", delayDays);

			Date now = new Date();
			String deleteDate = ImTimeUtil.getDateFormat2(ImTimeUtil.getDateSub(now, delayDays), "yyyyMMddHH:mm");

			EmsMainRepository emsMainRepository = EmsMainRepository.getInstance();
			ErrorCountRepository errorCountRepository = ErrorCountRepository.getInstance();
			LinkRepository linkRepository = LinkRepository.getInstance();
			ReceiptCountRepository receiptCountRepository = ReceiptCountRepository.getInstance();
			MsgidHostCountRepository msgidHostCountRepository = MsgidHostCountRepository.getInstance();
			MsgidRecvRepository msgidRecvRepository = MsgidRecvRepository.getInstance();
			// 기간이 지난 로그를 일괄삭제.
//			SELECT * FROM imb_emsmain WHERE msgid='AAAAA';
//
//  		=> msg_path 값을 가지고 실제  eml 파일 삭제 처리 후
//
//			DELETE FROM imb_emsmain WHERE msgid='AAAAA';
//			DELETE  FROM imb_msg_info WHERE msgid='AAAAA';
//			SELECT * FROM imb_emsattach WHERE msgid='AAAAA';
//
//  		=> file_path를 가지고 실제 경로에서 첨부파일을 삭제 처리 후
//
//			DELETE FROM imb_emsattach WHERE msgid='AAAAA';
//			DELETE FROM imb_error_count WHERE msgid='AAAAA';
//			DELETE FROM imb_link_count WHERE msgid='AAAAA';
//			DELETE FROM imb_link_info WHERE msgid='AAAAA';
//			DELETE FROM imb_receipt_count WHERE msgid='AAAAA';
//
//			DROP TABLE IF EXISTS recv_AAAAA;
//			DROP TABLE IF EXISTS hc_AAAAA;
//			DROP TABLE IF EXISTS linklog_AAAAA;

			List<ImbEmsMain> listEmsMain = emsMainRepository.getListToDelete(deleteDate);

			String msgPath = ImEmsConfig.getInstance().getMsgPath();


			for(ImbEmsMain emsMain : listEmsMain){
				// eml file 삭제
				String emlPath = msgPath + File.separator + emsMain.getMsg_path();
				FileDeleteService.fileDelete(emlPath);

				// imb_emsmain
				emsMainRepository.deleteEmsMain(emsMain.getMsgid());
				// imb_msg_info
				emsMainRepository.deleteMsgInfo(emsMain.getMsgid());

				// attach 테이블
				deleteAttach(emsMain.getMsgid());

				// imb_error_count
				errorCountRepository.delete(emsMain.getMsgid());

				// imb_link_count, imb_link_info
				linkRepository.deleteLinkCount(emsMain.getMsgid());
				linkRepository.deleteLinkInfo(emsMain.getMsgid());

				// imb_receipt_count
				receiptCountRepository.delete(emsMain.getMsgid());

				// drop table
				msgidHostCountRepository.dropHostCount(emsMain.getMsgid());
				msgidRecvRepository.dropRecvTable(emsMain.getMsgid());
				linkRepository.dropLinkLogTable(emsMain.getMsgid());

				logger.info("Delete Msgid: {}, Subject: {}, regdate: {}", emsMain.getMsgid(), emsMain.getMsg_name(), emsMain.getRegdate());
			}

		} catch (Exception e) {
			String errorId = ErrorTraceLogger.log(e);
			logger.error("{} - EmsResultDataCleanService Error:{}", errorId, e.getMessage());
		}
	}

	public void deleteAttach(String msgid){
		AttachRepository attachRepository = AttachRepository.getInstance();
		String attachPath = ImEmsConfig.getInstance().getAttachPath();
		try {
			List<ImbEmsAttach> listAttach = attachRepository.getAttachList(msgid);
			for (ImbEmsAttach attach : listAttach) {
				String filePath = attachPath + File.separator + attach.getFile_path();
				FileDeleteService.fileDelete(filePath);
			}

			attachRepository.deleteAttachByMsgid(msgid);
		} catch(Exception e){
			String errorId = ErrorTraceLogger.log(e);
			logger.error("{} - EmsResultDataCleanService.deleteAttach Error:{}", errorId, e.getMessage());
		}
	}
	
	/*public static void main(String[] args) {
        SensEmsEnvironment.init();

        LoggerLoader.initLog("sensems-daemon-log.xml");
        ImDatabaseConnectionEx.init("sensems.home","mybatis-config.xml");

        ActionLogCleanService transmitDataLogCleanService = new ActionLogCleanService();
		transmitDataLogCleanService.run();
	}*/
}

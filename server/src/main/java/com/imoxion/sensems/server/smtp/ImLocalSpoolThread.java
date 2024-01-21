package com.imoxion.sensems.server.smtp;

import com.imoxion.common.util.ImFileUtil;
import com.imoxion.sensems.server.util.ImSmtpSendData;
import com.imoxion.sensems.server.util.ImSmtpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class ImLocalSpoolThread extends Thread {

	public Logger smtpLogger = LoggerFactory.getLogger("SMTP");

	private String queuePath = "";
	
	public ImLocalSpoolThread(String queuePath){
		this.queuePath = queuePath;
	}
	
	public ImLocalSpoolThread(){
		if(this.queuePath.equals("")){
			this.queuePath = System.getProperty("sensmail.home") + File.separator + "spool";
		}
	}
	
	
	private void checkLocalSpoolDir(){
		try{
			String sLocalSpoolDir = this.queuePath + File.separator + "local";
			// accept spool이 있는지 체크해서 없으면 return
			File localSpoolDir = new File(sLocalSpoolDir);

			//smtpLogger.info( "ImLocalSpoolThread.checkLocalSpoolDir  sLocalSpoolDir : " + sLocalSpoolDir);

			if(!localSpoolDir.exists() && !localSpoolDir.mkdirs()) {
				return;
			}
			File[] spoolDirs = localSpoolDir.listFiles();
			if( spoolDirs != null ) {
				for (File f : spoolDirs) {
					if (f.isDirectory()) {
						continue;
					}

					// log 파일을 읽어들인다.
					if (f.getName().endsWith("sml")) {
						ObjectInputStream ois = null;
						FileInputStream fis = null;
						try {
							smtpLogger.info( "ImLocalSpoolThread.checkLocalSpoolDir : " + f.getAbsolutePath());
							// .body 가 없으면 건너뜀
							Thread.sleep(100);
							String sBodyPath = f.getAbsolutePath() + ".body";
							File bf = new File(sBodyPath);
							if (!bf.exists()) {
								f.delete();
								continue;
							}

							fis = new FileInputStream(f);
							ois = new ObjectInputStream(fis);
							ImSmtpSendData issd = (ImSmtpSendData) ois.readObject();
							if (issd != null) {
								String fileKey = ImFileUtil.getFileNameWithoutExt(f.getName());

								ImSmtpUtil.addQueueLocal(issd, sLocalSpoolDir, fileKey);

								ImFileUtil.deleteFile(f.getAbsolutePath());
							}
						} finally {
							try {
								if (ois != null) ois.close();
							} catch (Exception e) {
							}
							try {
								if (fis != null) fis.close();
							} catch (Exception e) {
							}
						}
					}
					//Thread.sleep(50);
				}
			}
		}catch(Exception ex){
			smtpLogger.error( "ImLocalSpoolThread.checkLocalSpoolDir : " + ex.getMessage());
		}
	}
	
	
	public void run(){
		try {
			while (!isInterrupted()) {
				checkLocalSpoolDir();
				// 10초 정도 대기한다.
				sleep(30000);
			}
		}catch(InterruptedException ie){
			smtpLogger.debug( "Interrupted ImLocalSpoolThread");
		}catch(Exception ex){
			smtpLogger.error( "ImLocalSpoolThread.run : {}", ex.getMessage());
		}
	}
}

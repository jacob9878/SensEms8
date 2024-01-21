package com.imoxion.sensems.server.util;

import com.imoxion.common.util.ImUtils;
import com.imoxion.sensems.server.exception.ImMessQueueException;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class ImMessQueue {
	static final int QUEUE_DEFAULTDEPTH = 20;
	static final int QUEUE_DEFAULTDEPTH_SUB = 10;
/*
	public static ImSpoolData getSpoolData(String p_sQueuePath) throws ImMessQueueException{
		ImSpoolData sData = new ImSpoolData();
		BufferedReader br = null;
		FileOutputStream fw = null;
		String sLine = "";
		try{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(p_sQueuePath)));
			while((sLine=br.readLine()) != null){
				if(sLine.indexOf("MAIL FROM:") == 0){
					String[] arrStr = sLine.split(":");
					if(arrStr.length > 1){
						InternetAddress[] arrAddr = InternetAddress.parse(arrStr[1]);
						sData.sFrom = arrAddr[0].getAddress();
					}else{
						br.close();
						throw new ImMessQueueException("mail from error");
					}
				}else if(sLine.indexOf("MSGID:") == 0){
					String[] arrStr = sLine.split(":");
					if(arrStr.length > 1){
						sData.sMsgid = arrStr[1];
					}else{
						br.close();
						throw new ImMessQueueException("msgid error");
					}
				}else if(sLine.indexOf("RCPTID:") == 0){
					String[] arrStr = sLine.split(":");
					if(arrStr.length > 1){
						sData.sRcptid = arrStr[1];
					}else{
						br.close();
						throw new ImMessQueueException("rcptid error");
					}
				}else if(sLine.indexOf("RCPT TO:") == 0){
					String[] arrStr = sLine.split(":");
					if(arrStr.length > 1){
						InternetAddress[] arrAddr = InternetAddress.parse(arrStr[1]);
						sData.sRcptto = arrAddr[0].getAddress();
					}else{
						br.close();
						throw new ImMessQueueException("rcpt to error");
					}
					
					String[] arrEmail = sData.sRcptto.split("@");
					if(arrEmail.length > 1){
						sData.sToDomain = arrEmail[1];
					}else{
						br.close();
						throw new ImMessQueueException("email address error");
					}
				}else if(sLine.equals("<<MAIL_DATA>>")){
					break;
				}
			}
			
//			char[] chBuff = new char[1024];
			File fTemp = File.createTempFile("imsend",".tmsg");
//			fTemp.deleteOnExit();
			
			fw = new FileOutputStream(fTemp,true);
			String sMsg = "";
			while((sLine=br.readLine()) != null){
//			while(br.read(chBuff,0,1024) > 0){
//				sMsg = sMsg+String.valueOf(chBuff).trim();
				sMsg = sMsg+sLine+"\r\n";
			}
			
			fw.write(sMsg.getBytes());
			
			sData.sTempPath = fTemp.getPath();
			sData.sMessPath = p_sQueuePath;

		}catch(Exception ex){
			System.out.println(ex);
		}finally{
			try{
				if(br != null) br.close();
				if(fw != null) fw.close();

				File f = new File(p_sQueuePath);
				f.delete();
			}catch(Exception fileex){}
		}
		
		return sData;
	}
*/
	public static ArrayList<?> getSpoolMailData(String p_sQueuePath) throws ImMessQueueException {
		ObjectInputStream in = null;
		ArrayList<?> arrMail = null;
		
		File f = new File(p_sQueuePath);
		try{
			in = new ObjectInputStream(new FileInputStream(f));
			
			arrMail = (ArrayList<?>)in.readObject();
		}catch(Exception ex){
			System.out.println(ex);
		}finally{
			try{
				if(in != null) in.close();
				f.delete();
			}catch(Exception fileex){fileex.printStackTrace();}
		}
		
		return arrMail;
	}
/*	
	public static ImEMSMailData getSpoolData(String p_sQueuePath) throws ImMessQueueException{
		ObjectInputStream in = null;
		ImEMSMailData mData = null;
		
		File f = new File(p_sQueuePath);
		try{
			in = new ObjectInputStream(new FileInputStream(f));
			
			mData = (ImEMSMailData)in.readObject();
		}catch(Exception ex){
			System.out.println(ex);
		}finally{
			try{
				if(in != null) in.close();
				f.delete();
			}catch(Exception fileex){}
		}
		
		return mData;
	}
*/
	public static String createQueueMessage(String p_sQueuePath){
		int nLevel1 = ImUtils.getRandom(0,QUEUE_DEFAULTDEPTH-1);
		int nLevel2 = ImUtils.getRandom(0,QUEUE_DEFAULTDEPTH-1);
		String sMsgKey = ImUtils.makeKey(24);
		
		String sResult = p_sQueuePath+File.separator+nLevel1+File.separator+nLevel2+
				File.separator+"mess"+File.separator+sMsgKey+".sml";
		
		File f = new File(sResult);
		if(f.exists()){
			createQueueMessage(p_sQueuePath);
		}
		
		return sResult;
	}
	
	public static String createQueueDir(String p_sQueuePath){
		int nLevel1 = ImUtils.getRandom(0,QUEUE_DEFAULTDEPTH-1);
		int nLevel2 = ImUtils.getRandom(0,QUEUE_DEFAULTDEPTH-1);
		
		String sResult = p_sQueuePath+File.separator+nLevel1+File.separator+nLevel2;
		
		return sResult;
	}
	
	// ���� �߼� ť�� �д´�
	public static ArrayList<String> loadQueue(String p_sQueuePath){
		String sCurrPath = "";
		ArrayList<String> vResult = new ArrayList<String>();
		
		File f = new File(p_sQueuePath);
		if(!f.exists() && !f.mkdir()){
			return null;
		}

		for(int i=0;i<QUEUE_DEFAULTDEPTH;i++){
			sCurrPath = p_sQueuePath+File.separator+i;
			File f1 = new File(sCurrPath);
			if(!f1.exists() && !f1.mkdir()){
				return null;
			}
			
			for(int j=0;j<QUEUE_DEFAULTDEPTH;j++){
				sCurrPath = p_sQueuePath+File.separator+i+File.separator+j;
				File f2 = new File(sCurrPath);
				if(!f2.exists() && !f2.mkdir()){
					return null;
				}

				sCurrPath = p_sQueuePath+File.separator+i+File.separator+j+File.separator+"mess";
				f2 = new File(sCurrPath);
				if(!f2.exists() && !f2.mkdir()){
					return null;
				}

				String[] arrMsgList = f2.list();
				if( arrMsgList != null ) {
					for (int k = 0; k < arrMsgList.length; k++) {
						if (arrMsgList[k].equalsIgnoreCase(".") || arrMsgList[k].equalsIgnoreCase(".."))
							continue;
						vResult.add(p_sQueuePath + File.separator + i + File.separator + j + File.separator + "mess" + File.separator + arrMsgList[k]);
					}
				}
				
				sCurrPath = p_sQueuePath+File.separator+i+File.separator+j+File.separator+"slog";
				f2 = new File(sCurrPath);
				if(!f2.exists() && !f2.mkdir()){
					return null;
				}

				sCurrPath = p_sQueuePath+File.separator+i+File.separator+j+File.separator+"temp";
				f2 = new File(sCurrPath);
				if(!f2.exists() && !f2.mkdir()){
					return null;
				}

				// 에러 메일 큐를 생성한다.
				sCurrPath = p_sQueuePath+File.separator+i+File.separator+j+File.separator+"serr";
				f2 = new File(sCurrPath);
				if(!f2.exists() && !f2.mkdir()){
					return null;
				}
				
				// 재발송 메일 큐를 생성한다.
				sCurrPath = p_sQueuePath+File.separator+i+File.separator+j+File.separator+"rsnd";
				f2 = new File(sCurrPath);
				if(!f2.exists() && !f2.mkdir()){
					return null;
				}
			}
		}
		
		return vResult;
	}

	// ���� ��߼� ť�� �д´�.
	public static ArrayList<String> loadRsndQueue(String p_sRsndQueuePath){
		String sCurrPath = "";
		ArrayList<String> vResult = new ArrayList<String>();
		
		File f = new File(p_sRsndQueuePath);
		if(!f.exists() && !f.mkdir()){
			return null;
		}

		for(int i=0;i<QUEUE_DEFAULTDEPTH;i++){
			sCurrPath = p_sRsndQueuePath+File.separator+i;
			File f1 = new File(sCurrPath);
			if(!f1.exists() && !f1.mkdir()){
				return null;
			}
			
			for(int j=0;j<QUEUE_DEFAULTDEPTH;j++){
				sCurrPath = p_sRsndQueuePath+File.separator+i+File.separator+j;
				File f2 = new File(sCurrPath);
				if(!f2.exists() && !f2.mkdir()){
					return null;
				}

				sCurrPath = p_sRsndQueuePath+File.separator+i+File.separator+j+File.separator+"rsnd";
				f2 = new File(sCurrPath);
				if(!f2.exists() && !f2.mkdir()){
					return null;
				}

				String[] arrMsgList = f2.list();
				if( arrMsgList != null ) {
					for (int k = 0; k < arrMsgList.length; k++) {
						if (arrMsgList[k].equalsIgnoreCase(".") || arrMsgList[k].equalsIgnoreCase(".."))
							continue;

						vResult.add(p_sRsndQueuePath + File.separator + i + File.separator + j + File.separator + "rsnd" + File.separator + arrMsgList[k]);
					}
				}
				
			}
		}
		
		return vResult;
	}
	
	// ���� �߼� ť�� �д´�.
	public static ArrayList<String> loadRsrvQueue(String p_sRsrvQueuePath){
		String sCurrPath = "";
		ArrayList<String> vResult = new ArrayList<String>();
		
		File f = new File(p_sRsrvQueuePath);
		if(!f.exists() && !f.mkdir()){
			return null;
		}

		for(int i=0;i<QUEUE_DEFAULTDEPTH;i++){
			sCurrPath = p_sRsrvQueuePath+File.separator+i;
			File f1 = new File(sCurrPath);
			if(!f1.exists() && !f1.mkdir()){
				return null;
			}
			
			for(int j=0;j<QUEUE_DEFAULTDEPTH;j++){
				sCurrPath = p_sRsrvQueuePath+File.separator+i+File.separator+j;
				File f2 = new File(sCurrPath);
				if(!f2.exists() && !f2.mkdir()){
					return null;
				}

				String[] arrMsgList = f2.list();
				if( arrMsgList != null ) {
					for (int k = 0; k < arrMsgList.length; k++) {
						if (arrMsgList[k].equalsIgnoreCase(".") || arrMsgList[k].equalsIgnoreCase(".."))
							continue;
						vResult.add(p_sRsrvQueuePath + File.separator + i + File.separator + j + File.separator + arrMsgList[k]);
					}
				}
/*
				sCurrPath = p_sRsrvQueuePath+File.separator+i+File.separator+j+File.separator+"rsrv";
				f2 = new File(sCurrPath);
				if(!f2.exists() && !f2.mkdir()){
					return null;
				}
*/
				
			}
		}
		
		return vResult;
	}
	
	public static void createSubDir(String p_sDir){
		String sCurrPath = "";
		
		File f = new File(p_sDir);
		if(!f.exists() && !f.mkdir()){
			return;
		}

		for(int i=0;i<QUEUE_DEFAULTDEPTH;i++){
			sCurrPath = p_sDir+File.separator+i;
			File f1 = new File(sCurrPath);
			if(!f1.exists() && !f1.mkdir()){
				return;
			}
			
			for(int j=0;j<QUEUE_DEFAULTDEPTH;j++){
				sCurrPath = p_sDir+File.separator+i+File.separator+j;
				File f2 = new File(sCurrPath);
				if(!f2.exists() && !f2.mkdir()){
					return;
				}
				
				for(int k=0;k<QUEUE_DEFAULTDEPTH_SUB;k++){
					sCurrPath = p_sDir+File.separator+i+File.separator+j+File.separator+k;
					File f3 = new File(sCurrPath);
					if(!f3.exists() && !f3.mkdir()){
						return;
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
	}
}

package com.imoxion.sensems.web.common.util;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.imoxion.common.util.ImConfLoaderEx;
import com.imoxion.common.util.ImFileUtil;

public class ImServerUtils {

	public static String getUserPath(String path,String userid){
		String ret = "";

		if(StringUtils.isNotEmpty(userid)) userid = userid.toLowerCase();

		if(userid.length() < 1){
			return null;
		}

		if(userid.length() < 2){
			return path+File.separator+userid.substring(0, 1)+File.separator+userid;
		}else if(userid.length() < 3){
			return path+File.separator+userid.substring(0, 1)+File.separator+userid.substring(1,2)
					+File.separator+userid;
		}else{
			return path+File.separator+userid.substring(0, 1)+File.separator+userid.substring(1,2)
					+File.separator + userid.substring(userid.length()-1, userid.length())+File.separator+userid;
		}
	}

	/**
	 * sqlite db 파일에 권한
	 * @param filePath
	 */
	public static void setFilePermission(String filePath){
		String systemUser = ImConfLoaderEx.getInstance().getProfileString("system", "user");
		String systemGroup = ImConfLoaderEx.getInstance().getProfileString("system", "group");
		String filePermission = ImConfLoaderEx.getInstance().getProfileString("system", "permission.file");
		String dirPermission = ImConfLoaderEx.getInstance().getProfileString("system", "permission.dir");
		if(StringUtils.isEmpty(filePermission)) filePermission = "rwxrw-r--";
		if(StringUtils.isEmpty(dirPermission)) filePermission = "rwxrwxr-x";

		try {
			File f = new File(filePath);
			// 파일에 읽기/실행 권한
			if (StringUtils.isNotEmpty(systemUser) && StringUtils.isNotEmpty(systemGroup)) {
				if (f.isFile()) {
					ImFileUtil.setPermission(f.getParentFile(), systemUser, systemGroup, dirPermission);
					ImFileUtil.setPermission(f, systemUser, systemGroup, filePermission);
				} else {
					ImFileUtil.setPermission(f, systemUser, systemGroup, dirPermission);
				}
			} else {
				// file은 rw, dir은 rwx
				if (f.isFile()) {
					ImFileUtil.setFileReadWriteExecute(f.getParent());
					ImFileUtil.setFileReadWrite(filePath);
				} else {
					ImFileUtil.setFileReadWriteExecute(filePath);
				}
			}
		}catch (NullPointerException ne){
			System.out.println("setfilepermission error");;
		}
		catch(Exception e){
			System.out.println("setfilepermission error");
		}
	}
}

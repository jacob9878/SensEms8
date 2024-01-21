package com.imoxion.sensems.server.util;

import com.imoxion.common.util.ImConfLoaderEx;
import com.imoxion.common.util.ImFileUtil;
import org.apache.commons.lang.StringUtils;

import java.io.File;

public class ImFilePermission {
	/**
	 * sqlite db 파일에 권한
	 * @param filePath
	 */
	public static void setFilePermission(String filePath){
		String systemUser = ImConfLoaderEx.getInstance("sensems.home","sensems.xml").getProfileString("system", "user");
		String systemGroup = ImConfLoaderEx.getInstance("sensems.home","sensems.xml").getProfileString("system", "group");
		String filePermission = ImConfLoaderEx.getInstance("sensems.home","sensems.xml").getProfileString("system", "permission.file");
		String dirPermission = ImConfLoaderEx.getInstance("sensems.home","sensems.xml").getProfileString("system", "permission.dir");
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
		}catch(Exception e){}
	}
}

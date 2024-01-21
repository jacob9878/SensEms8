package com.imoxion.sensems.server.service;

import java.io.File;

public class FileDeleteService {

	public static synchronized boolean fileDelete(String filepath) {
		File f = new File(filepath);
		return fileDelete(f);
	}

	public static synchronized boolean fileDelete(File file) {
		if( file.exists() && file.isFile() ){
			return file.delete();
		}else{
			return false;
		}
	}
}

package com.imoxion.sensems.web.common.util;

import com.imoxion.common.util.ImZipFileEx;
import com.imoxion.sensems.web.common.ProgressService;

/**
 * 
 * @author minideji
 *
 */
public class BackupZipFile extends ImZipFileEx {
	private String queueKey = null;

	public BackupZipFile(String queueKey) {

		this.queueKey = queueKey;
	}

	public BackupZipFile() {
	}

	public void progressFileCount(long p_lProc) {

		ProgressService.getInstance().update(queueKey, p_lProc);
	}

}

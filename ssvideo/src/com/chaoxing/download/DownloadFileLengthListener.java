package com.chaoxing.download;

import com.chaoxing.database.SSVideoLocalVideoBean;

public interface DownloadFileLengthListener {
	// ����Ҫ���ص��ļ��ĳ���
	public long onGetDownloadFileLen(long fileLen);
}

package com.chaoxing.download;

import com.chaoxing.database.SSVideoLocalVideoBean;

public interface DownloadFileLengthListener {
	// 监听要下载的文件的长度
	public long onGetDownloadFileLen(long fileLen);
}

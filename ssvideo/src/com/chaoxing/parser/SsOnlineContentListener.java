package com.chaoxing.parser;

import com.chaoxing.database.SSVideoPlayListBean;

public interface SsOnlineContentListener {
		public void onEndElement(SSVideoPlayListBean playListBean);
		public void onEndDocument();
}

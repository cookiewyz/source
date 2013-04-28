package com.chaoxing.util;

import android.util.Log;
/**
 * 
 * @author YongZheng
 * @version 2013-3-6
 */
public class ToolsLog {
	private static boolean DEBUG = true;
	public static void LOG_DBG(String paramString) {
		if (DEBUG) {
			Log.d("FAYUANVIDEO", paramString);
		}
	}

	public static void LOG_ERROR(String paramString) {
		if (DEBUG) {
			Log.e("FAYUANVIDEO", paramString);
		}
	}

	public static void LOG_INFO(String paramString) {
		StackTraceElement[] arrayOfStackTraceElement = new Exception()
				.getStackTrace();
		Log.i("FAYUANVIDEO",
				"Class:"
						+ arrayOfStackTraceElement[1].getClassName()
						+ "."
						+ arrayOfStackTraceElement[1].getMethodName()
						+ " (Line:"
						+ new Integer(arrayOfStackTraceElement[1]
								.getLineNumber()).toString() + ") :"
						+ paramString);
	}

	public static void LOG_WARN(String paramString) {
		if (Log.isLoggable("FAYUANVIDEO", 5)) {
			StackTraceElement[] arrayOfStackTraceElement = new Exception()
					.getStackTrace();
			Log.w("FAYUANVIDEO",
					"Class:"
							+ arrayOfStackTraceElement[1].getClassName()
							+ "."
							+ arrayOfStackTraceElement[1].getMethodName()
							+ " (Line:"
							+ new Integer(arrayOfStackTraceElement[1]
									.getLineNumber()).toString() + ") :"
							+ paramString);
		}
	}
}

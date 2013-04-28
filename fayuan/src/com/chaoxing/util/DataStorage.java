package com.chaoxing.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class DataStorage {

	private static final String FAYUAN_TABLE = "fayuan_storage_table";
	private static DataStorage storage;
	private final Editor editor;
	private final SharedPreferences pref;

	private DataStorage(Context paramContext) {
		this.pref = paramContext.getSharedPreferences(FAYUAN_TABLE, 0);
		this.editor = this.pref.edit();
	}

	public static DataStorage getInstance(Context paramContext) {
		if (storage == null)
			storage = new DataStorage(paramContext);
		return storage;
	}

	public boolean getBoolean(String paramString, boolean paramBoolean) {
		return this.pref.getBoolean(paramString, paramBoolean);
	}

	public int getInt(String paramString, int paramInt) {
		return this.pref.getInt(paramString, paramInt);
	}

	public String getString(String paramString1, String paramString2) {
		return this.pref.getString(paramString1, paramString2);
	}

	public void setBoolean(String paramString, boolean paramBoolean) {
		this.editor.putBoolean(paramString, paramBoolean);
		this.editor.commit();
	}

	public void setInt(String paramString, int paramInt) {
		this.editor.putInt(paramString, paramInt);
		this.editor.commit();
	}

	public void setString(String paramString1, String paramString2) {
		this.editor.putString(paramString1, paramString2);
		this.editor.commit();
	}

}

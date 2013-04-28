package com.chaoxing.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;

public class BitmapUtil {
	
	public static void saveBitmap2jpg(File saveDir, String jpgName, Bitmap bitmap){
		saveBitmap2jpg(saveDir, jpgName, bitmap, 0, 0);
	}
	
	public static void  saveBitmap2png(File saveDir, String pngName, Bitmap bitmap){
		saveBitmap2png(saveDir, pngName, bitmap, 0, 0);
	}
	
	public static void saveBitmap2jpg(File saveDir, String jpgName, Bitmap bitmap, int width, int height){
		saveBitmap2File(saveDir, jpgName, bitmap, Bitmap.CompressFormat.JPEG, width, height);
	}
	
	public static void saveBitmap2png(File saveDir, String pngName, Bitmap bitmap, int width, int height){
		saveBitmap2File(saveDir, pngName, bitmap, Bitmap.CompressFormat.PNG, width, height);
	}
	
	public static void saveBitmap2File(File saveDir, String jpgName, Bitmap bitmap, CompressFormat format, int width, int height){
		if(!saveDir.exists()) 	{
			saveDir.mkdirs();
		}
		File jpgFile = new File(saveDir, jpgName);
		try {
			jpgFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(jpgFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(width * height != 0){
			Bitmap newBitmap = scaleBitmap(bitmap, width, height); // 根据所要的图片做相应修改
			newBitmap.compress(format, 100, fOut);
		}else
			bitmap.compress(format, 100, fOut);
		try {
			fOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight){
	    int width = bitmap.getWidth();
	    int height = bitmap.getHeight();
	    float scaleWidth = ((float)newWidth) / width;
	    float scaleHight = ((float)newHeight) / height;
	    Matrix matrix = new Matrix();
	    matrix.postScale(scaleWidth, scaleHight);
	    Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	    return newBitmap;
	}
	
}

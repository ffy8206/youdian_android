/**
 * 
 */
package com.outsourcing.industry.restaurant.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

/**
 * @author Tianhua, Pan
 *
 */
public class CacheUtil {
	private static CacheUtil instance;
	private File cache_dir;
	private CacheUtil(Context context){
		if(null == cache_dir){
			File files_root = context.getFilesDir();
			cache_dir = new File(files_root, "imgcache");
			if(!cache_dir.exists()){
				cache_dir.mkdirs();
			}
		}
	}
	
	public static CacheUtil getInstance(Context context){
		if(null == instance){
			instance = new CacheUtil(context);
		}
		return instance;
	}
	
	public synchronized void saveImageAsCache(String filename, Bitmap bmp) throws IOException{
		if(cache_dir.exists()){
			File bmpfile = new File(cache_dir, filename);
			if(!bmpfile.exists()){
				FileOutputStream fos = new FileOutputStream(bmpfile);
				bmp.compress(CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
			}
			else{
				Log.i(GlobalParam.TAG, "image cache for "+filename+" already exists.");
			}
		}
		else{
			Log.w(GlobalParam.TAG, "Can not save image cache file.");
		}
	}
	
	public synchronized void updateImageAsCache(String filename, Bitmap bmp) throws IOException{
		if(cache_dir.exists()){
			File bmpfile = new File(cache_dir, filename);
			FileOutputStream fos = new FileOutputStream(bmpfile);
			bmp.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		}
		else{
			Log.w(GlobalParam.TAG, "Can not save image cache file.");
		}
	}
	
	public synchronized Bitmap loadImageFromCache(String filename){
		if(null == filename || filename.equals("")){
			Log.w(GlobalParam.TAG, "input file name is null or empty.");
			return null;
		}
		if(!cache_dir.exists()){
			Log.w(GlobalParam.TAG, "Can not load image from cache file.");
			return null;
		}
		File bmpfile = new File(cache_dir, filename);
		if(!bmpfile.exists()){
			Log.w(GlobalParam.TAG, "no required image in cache folder.");
			return null;
		}
		return BitmapFactory.decodeFile(bmpfile.getPath());
	}
	
	public synchronized Bitmap loadImageByUrl(String url){
		if(null == url || url.equals("")){
			Log.w(GlobalParam.TAG, "input url is null or empty.");
		}
		return null;
	}
}

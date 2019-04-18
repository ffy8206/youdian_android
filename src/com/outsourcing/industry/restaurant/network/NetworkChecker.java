/**
 * 
 */
package com.outsourcing.industry.restaurant.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author Tianhua, Pan
 * 
 */
public class NetworkChecker {
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(null != connectivityManager){
			NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
			if(null != networkInfos){
				for(int i=0; i<networkInfos.length; i++){
					if(networkInfos[i].getState() == NetworkInfo.State.CONNECTED){
						return true;
					}
				}
			}
		}
		return false;
	}
}

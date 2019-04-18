/**
 * 
 */
package com.outsourcing.industry.restaurant.receiver;

import com.outsourcing.industry.restaurant.activity.RestaurantMain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Tianhua, Pan
 *
 */
public class BootReceiver extends BroadcastReceiver {

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// when receive boot complete event, start main activity
		Intent main_intent = new Intent();
		main_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		main_intent.setClass(context, RestaurantMain.class);
		context.startActivity(main_intent);
	}

}

/**
 * 
 */
package com.outsourcing.industry.restaurant.activity;

import com.outsourcing.industry.restaurant.R;
import com.outsourcing.industry.restaurant.util.GlobalParam;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Tianhua, Pan
 *
 */
public class TestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		Log.d(GlobalParam.TAG, getString(R.string.label_currentorder_briefinfo, 1, 2, 2.0));
	}
}

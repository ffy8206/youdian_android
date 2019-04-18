/**
 * 
 */
package com.outsourcing.industry.restaurant.activity;

import com.outsourcing.industry.restaurant.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author Tianhua, Pan
 * 
 */
public class SettingPreference extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.setting_title);
		addPreferencesFromResource(R.xml.preferences);
	}
}

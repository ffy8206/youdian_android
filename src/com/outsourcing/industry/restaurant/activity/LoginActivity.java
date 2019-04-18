/**
 * 
 */
package com.outsourcing.industry.restaurant.activity;

import com.outsourcing.industry.restaurant.R;
import com.outsourcing.industry.restaurant.util.GlobalParam;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Tianhua, Pan
 * 
 */
public class LoginActivity extends Activity {
	private EditText edit_loginname, edit_loginpass;
	private Button button_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		edit_loginname = (EditText) findViewById(R.id.edit_loginname);
		edit_loginpass = (EditText) findViewById(R.id.edit_loginpass);
		button_login = (Button) findViewById(R.id.button_login);

		button_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(edit_loginname.getText())) {
					showMessage(R.string.label_loginname_empty);
					return;
				}
				if (TextUtils.isEmpty(edit_loginpass.getText())) {
					showMessage(R.string.label_loginpass_empty);
					return;
				}
				String loginname = edit_loginname.getText().toString();
				String password = edit_loginpass.getText().toString();
				if (!loginname.equals(GlobalParam.LOGIN_NAME) || !password.equals(GlobalParam.LOGIN_PASS)) {
					showMessage(R.string.label_login_fail);
					return;
				} else {
					SharedPreferences prefs = getSharedPreferences(
							getPackageName(), MODE_PRIVATE);
					prefs.edit().putString("loginname", loginname).putString(
							"loginpass", password).commit();
					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, RestaurantMain.class);
					startActivity(intent);
					finish();
				}

			}
		});
	}

	private void showMessage(int msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

}

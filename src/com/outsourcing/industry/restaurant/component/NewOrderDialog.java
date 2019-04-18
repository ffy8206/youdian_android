/**
 * 
 */
package com.outsourcing.industry.restaurant.component;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.outsourcing.industry.restaurant.R;
import com.outsourcing.industry.restaurant.entity.Order;
import com.outsourcing.industry.restaurant.util.DBHelper;
import com.outsourcing.industry.restaurant.util.GlobalParam;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Tianhua, Pan
 * 
 */
public class NewOrderDialog extends Dialog implements
		android.view.View.OnClickListener{
	private long current_time;
	private Context context;
	private Handler handler;
	private ImageButton imgbtn_neworder, imgbtn_continueorder;
	private ArrayAdapter<CharSequence> adapter_spinner_position;
	private Spinner spinner_position;

	public NewOrderDialog(Context context, Handler handler) {
		super(context, R.style.dialog);
		this.current_time = System.currentTimeMillis();
		this.context = context;
		this.handler = handler;
		show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(GlobalParam.TAG, "onCreate");
		setContentView(R.layout.dialog_neworder);
		resizeAndPosition();

		TextView text_neworder_time = (TextView) findViewById(R.id.text_neworder_time);
		SimpleDateFormat sdf = new SimpleDateFormat(getContext().getString(
				R.string.label_formatted_timestr));
		String str_time = getContext().getString(R.string.label_neworder_time,
				sdf.format(new Date(current_time)));
		text_neworder_time.setText(str_time);
		spinner_position = (Spinner)findViewById(R.id.spinner_position);
		
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(GlobalParam.TAG, "onStart");
		imgbtn_neworder = (ImageButton) findViewById(R.id.imgbtn_neworder);
		imgbtn_neworder.setOnClickListener(this);

		imgbtn_continueorder = (ImageButton) findViewById(R.id.imgbtn_continueorder);
		imgbtn_continueorder.setOnClickListener(this);
		
		adapter_spinner_position = ArrayAdapter.createFromResource(context, R.array.strarr_position,R.layout.spinner_text);
		adapter_spinner_position.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_position.setAdapter(adapter_spinner_position);
	}

	public void resizeAndPosition() {
		LayoutParams params = getWindow().getAttributes();
		params.height = 700;
		params.width = 620;
		params.y = 48;
		getWindow().setAttributes(params);
		getWindow().setGravity(Gravity.TOP);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_neworder:
			EditText edit_position = (EditText) findViewById(R.id.edit_customerno);
			EditText edit_customerno = (EditText) findViewById(R.id.edit_customerno);
			EditText edit_tableno = (EditText) findViewById(R.id.edit_tableno);
			EditText edit_waiterno = (EditText) findViewById(R.id.edit_waiterno);
			EditText edit_notes = (EditText) findViewById(R.id.edit_notes);
			if (checkIsEmpty(edit_position) || checkIsEmpty(edit_customerno)
					|| checkIsEmpty(edit_tableno)
					|| checkIsEmpty(edit_waiterno)) {
				return;
			}
			Order order = new Order(spinner_position.getSelectedItemPosition(),
					edit_waiterno.getText().toString(), Integer
							.parseInt(edit_tableno.getText().toString()),
					Integer.parseInt(edit_customerno.getText().toString()),
					current_time);
			order.setOrder_notes(edit_notes.getText().toString());
			DBHelper.getInstance(context).create(order);
			Message message = new Message();
			message.obj = order;
			handler.sendMessage(message);
			break;
		case R.id.imgbtn_continueorder:

			break;

		default:
			break;
		}
		dismiss();

	}

	/**
	 * return true if text in EditText is empty otherwise return false
	 * 
	 * @param edit
	 * @return boolean
	 */
	private boolean checkIsEmpty(EditText edit) {
		if (TextUtils.isEmpty(edit.getText())) {
			Toast.makeText(context, "", Toast.LENGTH_LONG).show();
			return true;
		}
		return false;
	}

}

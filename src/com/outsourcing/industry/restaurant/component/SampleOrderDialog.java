/**
 * 
 */
package com.outsourcing.industry.restaurant.component;

import com.outsourcing.industry.restaurant.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager.LayoutParams;

/**
 * @author Tianhua, Pan
 *
 */
public class SampleOrderDialog extends Dialog {

	public SampleOrderDialog(Context context) {
		super(context, R.style.dialog);
		show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		resizeAndPosition();
		setContentView(R.layout.dialog_sampleorder);
	}
	
	public void resizeAndPosition() {
		LayoutParams params = getWindow().getAttributes();
		params.height = 700;
		params.width = 620;
		params.y = 48;
		getWindow().setAttributes(params);
		getWindow().setGravity(Gravity.TOP);
	}

}

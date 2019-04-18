/**
 * 
 */
package com.outsourcing.industry.restaurant.component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.outsourcing.industry.restaurant.R;
import com.outsourcing.industry.restaurant.entity.Dish;
import com.outsourcing.industry.restaurant.entity.DishFactory;
import com.outsourcing.industry.restaurant.entity.Order;
import com.outsourcing.industry.restaurant.util.DBHelper;
import com.outsourcing.industry.restaurant.util.GlobalParam;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * @author Tianhua, Pan
 * 
 */
public class OrderDialog extends Dialog implements
		android.view.View.OnClickListener, OnItemSelectedListener {
	private LinearLayout linear_cold, linear_hot, linear_other,
			linear_orderrow_submit_layout, linear_orderrow_unsubmit_layout;
	private Handler handler;
	private Order current_order;
	private TextView text_totaldishesnum, text_totalprice, text_tableinfo,
			text_customerinfo, text_order_time, text_waiter_info;
	private ImageButton imgbtn_currentOrder, imgbtn_historyOrder,
			imgbtn_continueorder, imgbtn_hot_behave, imgbtn_cold_behave,
			imgbtn_other_behave, imgbtn_search;
	private Spinner spinner_orderstatus, spinner_tableno, spinner_customerno;
	private ArrayAdapter<Object> adapter_tableno, adapter_customerno;
	private ArrayAdapter<String> adapter_orderstatus;
	private List<Order> list_orders;

	private boolean inCurrentPage;
	private int selected_tableno, selected_custno;
	private short selected_status;

	public OrderDialog(Context context, Handler handler, Order order) {
		super(context, R.style.dialog);
		this.handler = handler;
		this.current_order = order;
		this.inCurrentPage = true;
		selected_status = -1;
		selected_custno = -1;
		selected_tableno = -1;
		show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		resizeAndPosition();
		list_orders = loadOrderes(-1, -1, (short) -1);
		refreshContent();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_currentorder:
			imgbtn_historyOrder
					.setImageResource(R.drawable.history_order_before);
			imgbtn_currentOrder
					.setImageResource(R.drawable.current_order_after);
			inCurrentPage = true;
			refreshContent();
			break;
		case R.id.imgbtn_historyorder:
			imgbtn_historyOrder
					.setImageResource(R.drawable.history_order_after);
			imgbtn_currentOrder
					.setImageResource(R.drawable.current_order_before);
			inCurrentPage = false;
			refreshContent();
			break;
		case R.id.imgbtn_operation:
			Order order = (Order) v.getTag();
			if (order.getSubmit() == 0) {
				Message message = new Message();
				message.obj = order;
				handler.sendMessage(message);
			}
			OrderDialog.this.dismiss();
			break;
		case R.id.imgbtn_continueorder:
			if (current_order != null) {
				Message message = new Message();
				message.obj = current_order;
				handler.sendMessage(message);
			}
			OrderDialog.this.dismiss();
			break;
		case R.id.imgbtn_hot_behave:
			if (Integer.parseInt(v.getTag().toString()) == GlobalParam.STATUS_COLLAPSE) {
				linear_hot.setVisibility(View.VISIBLE);
				imgbtn_hot_behave.setImageResource(R.drawable.collapse);
				imgbtn_hot_behave.setTag(GlobalParam.STATUS_SPREAD);
			} else {
				linear_hot.setVisibility(View.GONE);
				imgbtn_hot_behave.setImageResource(R.drawable.spread);
				imgbtn_hot_behave.setTag(GlobalParam.STATUS_COLLAPSE);
			}
			break;
		case R.id.imgbtn_cold_behave:
			if (Integer.parseInt(v.getTag().toString()) == GlobalParam.STATUS_COLLAPSE) {
				linear_cold.setVisibility(View.VISIBLE);
				imgbtn_cold_behave.setImageResource(R.drawable.collapse);
				imgbtn_cold_behave.setTag(GlobalParam.STATUS_SPREAD);
			} else {
				linear_cold.setVisibility(View.GONE);
				imgbtn_cold_behave.setImageResource(R.drawable.spread);
				imgbtn_cold_behave.setTag(GlobalParam.STATUS_COLLAPSE);
			}
			break;
		case R.id.imgbtn_other_behave:
			if (Integer.parseInt(v.getTag().toString()) == GlobalParam.STATUS_COLLAPSE) {
				linear_other.setVisibility(View.VISIBLE);
				imgbtn_other_behave.setImageResource(R.drawable.collapse);
				imgbtn_other_behave.setTag(GlobalParam.STATUS_SPREAD);
			} else {
				linear_other.setVisibility(View.GONE);
				imgbtn_other_behave.setImageResource(R.drawable.spread);
				imgbtn_other_behave.setTag(GlobalParam.STATUS_COLLAPSE);
			}
			break;
		case R.id.imgbtn_search:
			/*
			 * short selectedStatus = -1; int selectedTableno = -1; int
			 * selectedCustno = -1; if(imgbtn_search.getTag(0)!=null){
			 * selectedStatus = (Short)imgbtn_search.getTag(0); }
			 * if(imgbtn_search.getTag(1)!=null){ selectedTableno =
			 * (Integer)imgbtn_search.getTag(1); }
			 * if(imgbtn_search.getTag(2)!=null){ selectedCustno =
			 * (Integer)imgbtn_search.getTag(2); }
			 */
			list_orders = loadOrderes(selected_tableno, selected_custno,
					selected_status);
			refreshContent();
			break;
		default:
			break;
		}
	}

	public void refreshContent() {
		if (inCurrentPage) {
			setContentView(R.layout.dialog_order);
			imgbtn_continueorder = (ImageButton) findViewById(R.id.imgbtn_continueorder);
			imgbtn_continueorder.setOnClickListener(this);
			imgbtn_currentOrder = (ImageButton) findViewById(R.id.imgbtn_currentorder);
			imgbtn_currentOrder.setOnClickListener(this);
			imgbtn_historyOrder = (ImageButton) findViewById(R.id.imgbtn_historyorder);
			imgbtn_historyOrder.setOnClickListener(this);
			text_totaldishesnum = (TextView) findViewById(R.id.text_totaldishesnum);
			text_totalprice = (TextView) findViewById(R.id.text_totalprice);
			text_tableinfo = (TextView) findViewById(R.id.text_tableinfo);
			text_customerinfo = (TextView) findViewById(R.id.text_customerinfo);
			text_order_time = (TextView) findViewById(R.id.text_order_time);
			text_waiter_info = (TextView) findViewById(R.id.text_waiter_info);
			linear_cold = (LinearLayout) findViewById(R.id.linear_cold);
			linear_hot = (LinearLayout) findViewById(R.id.linear_hot);
			linear_other = (LinearLayout) findViewById(R.id.linear_other);
			imgbtn_hot_behave = (ImageButton) findViewById(R.id.imgbtn_hot_behave);
			imgbtn_hot_behave.setTag(GlobalParam.STATUS_SPREAD);
			imgbtn_hot_behave.setOnClickListener(this);
			imgbtn_cold_behave = (ImageButton) findViewById(R.id.imgbtn_cold_behave);
			imgbtn_cold_behave.setTag(GlobalParam.STATUS_SPREAD);
			imgbtn_cold_behave.setOnClickListener(this);
			imgbtn_other_behave = (ImageButton) findViewById(R.id.imgbtn_other_behave);
			imgbtn_other_behave.setTag(GlobalParam.STATUS_SPREAD);
			imgbtn_other_behave.setOnClickListener(this);
			TextView text_noorder = (TextView) findViewById(R.id.text_noorder);
			LinearLayout linear_orderbody = (LinearLayout) findViewById(R.id.linear_orderbody);
			if (null != current_order) {
				linear_orderbody.setVisibility(View.VISIBLE);
				text_noorder.setVisibility(View.GONE);
				refreshDishRows(current_order.getMap_orderedDishes());
			} else {
				linear_orderbody.setVisibility(View.GONE);
				text_noorder.setVisibility(View.VISIBLE);
			}
		} else {
			setContentView(R.layout.dialog_historyorder);
			imgbtn_continueorder = (ImageButton) findViewById(R.id.imgbtn_continueorder);
			imgbtn_continueorder.setOnClickListener(this);
			imgbtn_currentOrder = (ImageButton) findViewById(R.id.imgbtn_currentorder);
			imgbtn_currentOrder.setOnClickListener(this);
			imgbtn_historyOrder = (ImageButton) findViewById(R.id.imgbtn_historyorder);
			imgbtn_historyOrder.setOnClickListener(this);
			linear_orderrow_submit_layout = (LinearLayout) findViewById(R.id.linear_orderrow_submit_layout);
			linear_orderrow_unsubmit_layout = (LinearLayout) findViewById(R.id.linear_orderrow_unsubmit_layout);
			spinner_orderstatus = (Spinner) findViewById(R.id.spinner_orderstatus);
			adapter_orderstatus = new ArrayAdapter<String>(getContext(),
					android.R.layout.simple_spinner_item);
			adapter_orderstatus
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner_orderstatus.setAdapter(adapter_orderstatus);
			spinner_orderstatus.setOnItemSelectedListener(this);
			spinner_tableno = (Spinner) findViewById(R.id.spinner_tableno);
			adapter_tableno = new ArrayAdapter<Object>(getContext(),
					android.R.layout.simple_spinner_item);
			adapter_tableno
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner_tableno.setAdapter(adapter_tableno);
			spinner_tableno.setOnItemSelectedListener(this);
			spinner_customerno = (Spinner) findViewById(R.id.spinner_customerno);
			adapter_customerno = new ArrayAdapter<Object>(getContext(),
					android.R.layout.simple_spinner_item);
			adapter_customerno
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner_customerno.setAdapter(adapter_customerno);
			spinner_customerno.setOnItemSelectedListener(this);
			imgbtn_search = (ImageButton) findViewById(R.id.imgbtn_search);
			imgbtn_search.setOnClickListener(this);
			loadAdapterData();
			refreshOrderRows();
		}
	}

	private List<Order> loadOrderes(int tblid, int custno, short submit) {
		return DBHelper.getInstance(getContext()).queryOrderes(tblid, custno,
				submit, 10);
	}

	private void refreshDishRows(Map<Integer, Integer> map_id_dish) {
		Iterator<Integer> iter = map_id_dish.keySet().iterator();
		while (iter.hasNext()) {
			Dish dish = DishFactory.getInstance(getContext()).getDishById(
					iter.next());
			addDishRow(dish);
		}
		String str_total_dishnum = getContext().getString(
				R.string.label_total_dishnum, map_id_dish.size());
		text_totaldishesnum.setText(str_total_dishnum);
		String str_total_price = getContext().getString(
				R.string.label_total_price, current_order.getTotalPrice());
		text_totalprice.setText(str_total_price);
		String str_tableno = getContext().getString(R.string.label_table_info,
				current_order.getTable_id());
		text_tableinfo.setText(str_tableno);
		String str_customerno = getContext().getString(
				R.string.label_customer_info, current_order.getCust_num());
		text_customerinfo.setText(str_customerno);
		String str_ordertime = new SimpleDateFormat("HH:mm").format(new Date(
				current_order.getOrder_time()));
		text_order_time.setText(str_ordertime);
		text_waiter_info.setText(current_order.getWaiter_id());
	}

	private void refreshOrderRows() {
		linear_orderrow_submit_layout.removeAllViews();
		linear_orderrow_unsubmit_layout.removeAllViews();
		if (null != list_orders) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			for (int i = 0; i < list_orders.size(); i++) {
				View view = inflater.inflate(R.layout.historyorder_tablerow,
						null);
				Order order = list_orders.get(i);
				TextView text_tableno = (TextView) view
						.findViewById(R.id.text_tableno);
				String str_tableno = getContext().getString(
						R.string.label_historyorder_tableno,
						order.getTable_id());
				text_tableno.setText(str_tableno);
				TextView text_customerno = (TextView) view
						.findViewById(R.id.text_customerno);
				text_customerno.setText("" + order.getCust_num());
				TextView text_dishestotalnum = (TextView) view
						.findViewById(R.id.text_dishestotalnum);
				text_dishestotalnum.setText(""
						+ order.getMap_orderedDishes().size());
				TextView text_totalprice = (TextView) view
						.findViewById(R.id.text_totalprice);
				text_totalprice.setText("" + order.getTotalPrice());
				ImageButton imgbtn_operation = (ImageButton) view
						.findViewById(R.id.imgbtn_operation);
				imgbtn_operation.setTag(order);
				imgbtn_operation.setOnClickListener(this);
				if (order.getSubmit() == 0) { // unsubmit
					imgbtn_operation
							.setImageResource(R.drawable.order_smallimgbtn_continue);
					linear_orderrow_unsubmit_layout.addView(view, -1, 40);
				} else {
					imgbtn_operation
							.setImageResource(R.drawable.order_smallimgbtn_details);
					linear_orderrow_submit_layout.addView(view, -1, 40);
				}

			}
		}
	}

	private void loadAdapterData() {
		adapter_orderstatus.add(getContext().getString(
				R.string.label_tag_submitall));
		adapter_orderstatus.add(getContext().getString(
				R.string.label_tag_unsubmit));
		adapter_orderstatus.add(getContext().getString(
				R.string.label_tag_submitted));
		adapter_orderstatus.notifyDataSetChanged();
		List<Order> list_allorders = loadOrderes(-1, -1, (short) -1);
		if (null != list_allorders) {
			Set<Object> set_tableno = new HashSet<Object>();
			Set<Object> set_customerno = new HashSet<Object>();
			for (int i = 0; i < list_allorders.size(); i++) {
				// if(adapter_tableno.)
				set_tableno.add(list_allorders.get(i).getTable_id());
				set_customerno.add(list_allorders.get(i).getCust_num());
				// adapter_tableno.add(list_allorders.get(i).getTable_id());
				// adapter_customerno.add(list_allorders.get(i).getCust_num());
			}
			adapter_tableno.add(getContext().getString(
					R.string.label_tag_submitall));
			adapter_customerno.add(getContext().getString(
					R.string.label_tag_submitall));
			Iterator<Object> it_tableno = set_tableno.iterator();
			while (it_tableno.hasNext()) {
				adapter_tableno.add(it_tableno.next());
			}
			Iterator<Object> it_customerno = set_customerno.iterator();
			while (it_customerno.hasNext()) {
				adapter_customerno.add(it_customerno.next());
			}
			adapter_tableno.notifyDataSetChanged();
			adapter_customerno.notifyDataSetChanged();
			spinner_orderstatus.setSelection(selected_status + 1);
		}
	}

	public void resizeAndPosition() {
		LayoutParams params = getWindow().getAttributes();
		params.height = 700;
		params.width = 620;
		params.y = 48;
		getWindow().setAttributes(params);
		getWindow().setGravity(Gravity.TOP);
	}

	public void addDishRow(Dish dish) {
		int dishtype = dish.getDishsubtype();
		View row = createDishTableRow(dish);
		switch (dishtype) {
		case GlobalParam.DISHTYPE_OTHER: // other
			linear_other.addView(row);
			break;
		case GlobalParam.DISHTYPE_COLD: // cold
			linear_cold.addView(row);
			break;
		case GlobalParam.DISHTYPE_HOT: // hot
			linear_hot.addView(row);
			break;
		default:
			Log.w(GlobalParam.TAG, "Unknow dish type.");
			break;
		}
	}

	private View createDishTableRow(Dish dish) {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout row = (LinearLayout) inflater.inflate(
				R.layout.ordereddish_tablerow, null);
		row.setTag(dish);
		row.setId(dish.getDishid());
		TextView text_dishname = (TextView) row
				.findViewById(R.id.text_dishname);
		text_dishname.setText(dish.getName());
		EditText edit_dishnum = (EditText) row.findViewById(R.id.edit_dishnum);
		edit_dishnum.setText(""
				+ current_order.getOrderedDishNum(dish.getDishid()));
		DishRowButtonOnClickListener listener = new DishRowButtonOnClickListener(
				row);
		ImageButton imgbtn_minus = (ImageButton) row
				.findViewById(R.id.imgbtn_minus);
		imgbtn_minus.setOnClickListener(listener);
		ImageButton imgbtn_plus = (ImageButton) row
				.findViewById(R.id.imgbtn_plus);
		imgbtn_plus.setOnClickListener(listener);

		ImageButton imgbtn_dishdelete = (ImageButton) row
				.findViewById(R.id.imgbtn_dishdelete);
		imgbtn_dishdelete.setOnClickListener(listener);

		TextView text_dishprice = (TextView) row
				.findViewById(R.id.text_dishprice);
		long priceid = current_order.getPirceidBySelectedDishid(dish
				.getDishid());
		text_dishprice.setText(""
				+ DishFactory.getInstance(getContext()).getPriceByPriceid(
						priceid));

		return row;
	}

	class DishRowButtonOnClickListener implements
			android.view.View.OnClickListener {
		private View row;

		public DishRowButtonOnClickListener(View row) {
			this.row = row;
		}

		@Override
		public void onClick(View v) {
			Dish targetDish = (Dish) row.getTag();
			long priceid = current_order.getPirceidBySelectedDishid(targetDish
					.getDishid());
			EditText edit_dishnum = (EditText) row
					.findViewById(R.id.edit_dishnum);
			int dishnum = current_order.getOrderedDishNum(targetDish
					.getDishid());

			switch (v.getId()) {
			case R.id.imgbtn_minus:
				dishnum--;
				current_order.removeDish(getContext(), targetDish.getDishid(),
						1);
				break;
			case R.id.imgbtn_plus:
				dishnum++;
				current_order.addDish(getContext(), targetDish.getDishid(),
						priceid, 1);
				break;
			case R.id.imgbtn_dishdelete:
				Dish tagdish = (Dish) row.getTag();
				switch (tagdish.getDishsubtype()) {
				case GlobalParam.DISHTYPE_COLD:
					linear_cold.removeView(row);
					break;
				case GlobalParam.DISHTYPE_HOT:
					linear_hot.removeView(row);
					break;
				case GlobalParam.DISHTYPE_OTHER:
					linear_other.removeView(row);
					break;
				default:
					break;
				}
				current_order.removeDish(getContext(), targetDish.getDishid(),
						-1);
				break;
			default:
				break;
			}
			if (dishnum < 0) {
				dishnum = 0;
			}
			edit_dishnum.setText("" + dishnum);
			String str_total_dishnum = getContext().getString(
					R.string.label_total_dishnum,
					current_order.getMap_orderedDishes().size());
			text_totaldishesnum.setText(str_total_dishnum);
			String str_total_price = getContext().getString(
					R.string.label_total_price, current_order.getTotalPrice());
			text_totalprice.setText(str_total_price);
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int pos,
			long arg3) {
		switch (adapterView.getId()) {
		case R.id.spinner_orderstatus:
			selected_status = (short) (pos - 1);
			break;
		case R.id.spinner_tableno:
			if (adapterView.getItemAtPosition(pos) instanceof String) {
				selected_tableno = -1;
			} else {
				selected_tableno = (Integer) adapterView.getItemAtPosition(pos);
			}
			/*
			 * imgbtn_search.setTag(1, ((Spinner) adapterView)
			 * .getItemAtPosition(pos));
			 */
			break;
		case R.id.spinner_customerno:
			if (adapterView.getItemAtPosition(pos) instanceof String) {
				selected_custno = -1;
			} else {
				selected_custno = (Integer) adapterView.getItemAtPosition(pos);
			}
			/*
			 * imgbtn_search.setTag(2, ((Spinner) adapterView)
			 * .getItemAtPosition(pos));
			 */
			break;
		default:
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

}

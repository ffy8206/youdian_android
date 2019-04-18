/**
 * 
 */
package com.outsourcing.industry.restaurant.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.outsourcing.industry.restaurant.R;
import com.outsourcing.industry.restaurant.entity.Dish;
import com.outsourcing.industry.restaurant.entity.DishFactory;
import com.outsourcing.industry.restaurant.entity.Order;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * @author Tianhua, Pan
 * 
 */
public class ListDishAdapter extends BaseAdapter {
	private List<Dish> list_dishes;
	private Context context;
	private File cache_dir;
	private Order current_order;

	public ListDishAdapter(Context context, Order current_order) {
		this.context = context;
		list_dishes = new ArrayList<Dish>();
		this.current_order = current_order;
		cache_dir = new File(context.getFilesDir(), "imgcache");
	}

	public void setListDishes(List<Dish> list_dishes) {
		this.list_dishes = list_dishes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		if (null != list_dishes) {
			return list_dishes.size();
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int pos) {
		if (null != list_dishes && pos < list_dishes.size()) {
			return list_dishes.get(pos);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int pos) {
		if (null != list_dishes && pos < list_dishes.size()) {
			return list_dishes.get(pos).getDishid();
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int pos, View view, ViewGroup arg2) {
		Dish dish = list_dishes.get(pos);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ArrayAdapter<Dish.DishPrice> adapter_dishPrice = null;
		Spinner spinner_dishprice = null;
		ImageButton imgbtn_makeorder = null;
		if (null == view) {
			view = inflater.inflate(R.layout.listview_item, null);
			imgbtn_makeorder = (ImageButton) view
					.findViewById(R.id.imgbtn_makeorder);
			spinner_dishprice = (Spinner) view
					.findViewById(R.id.spinner_dishprice);
			adapter_dishPrice = new ArrayAdapter<Dish.DishPrice>(context,
					R.layout.main_spinner_text);
			spinner_dishprice.setAdapter(adapter_dishPrice);
		} else {
			imgbtn_makeorder = (ImageButton) view
					.findViewById(R.id.imgbtn_makeorder);
			spinner_dishprice = (Spinner) view
					.findViewById(R.id.spinner_dishprice);
			adapter_dishPrice = (ArrayAdapter<Dish.DishPrice>) spinner_dishprice
					.getAdapter();
		}
		spinner_dishprice
				.setOnItemSelectedListener(new PriceSpinOnSelectListener(dish));

		adapter_dishPrice.clear();
		for (int i = 0; i < dish.dish_prices.length; i++) {
			adapter_dishPrice.add(dish.dish_prices[i]);
		}
		adapter_dishPrice.notifyDataSetChanged();
		ImageView listview_item_dishimg = (ImageView) view
				.findViewById(R.id.listview_item_dishimg);
		TextView text_dishname = (TextView) view
				.findViewById(R.id.text_dishname);

		imgbtn_makeorder.setOnClickListener(new OrderButtonOnClickListener(
				dish, spinner_dishprice));

		listview_item_dishimg.setImageURI(Uri.fromFile(loadImageFile(dish)));
		text_dishname.setText(dish.getName());
		int orderednum = -1;
		if (current_order != null) {
			orderednum = current_order.getOrderedDishNum(dish.getDishid());
		}
		if (orderednum == -1) {
			imgbtn_makeorder
					.setImageResource(R.drawable.makeorder_small_before);
		} else {
			imgbtn_makeorder.setImageResource(R.drawable.makeorder_small_after);
			long priceid = current_order.getPirceidBySelectedDishid(dish
					.getDishid());
			Dish.DishPrice dishPrice_object = DishFactory.getInstance(context)
					.getPriceObjectByPriceid(priceid);
			spinner_dishprice.setSelection(adapter_dishPrice
					.getPosition(dishPrice_object));
		}
		imgbtn_makeorder.setTag(dish);
		return view;
	}

	public class OrderButtonOnClickListener implements OnClickListener {
		private Dish dish;
		private Spinner spinner_dishprice;

		public OrderButtonOnClickListener(Dish dish, Spinner spinner_dishprice) {
			this.dish = dish;
			this.spinner_dishprice = spinner_dishprice;
		}

		@Override
		public void onClick(View v) {
			if (current_order == null) {
				Toast.makeText(context, R.string.error_noorder,
						Toast.LENGTH_LONG).show();
				return;
			}
			long selected_priceid = ((Dish.DishPrice) spinner_dishprice
					.getSelectedItem()).getPriceid();
			if (current_order.getOrderedDishNum(dish.getDishid()) == -1) {
				current_order.addDish(context, dish.getDishid(),
						selected_priceid, 1);
				((ImageButton) v)
						.setImageResource(R.drawable.makeorder_small_after);
			} else {
				current_order.removeDish(context, dish.getDishid(), -1);
				((ImageButton) v)
						.setImageResource(R.drawable.makeorder_small_before);
			}

		}
	}

	/*
	 * public class SpinnerItemSelectListener implements OnItemSelectedListener{
	 * private View orderbuttonview; public SpinnerItemSelectListener(View
	 * orderbuttonview){ this.orderbuttonview = orderbuttonview; }
	 * 
	 * @Override public void onItemSelected(AdapterView<?> adapterview, View
	 * arg1, int pos, long arg3) { Dish.DishPrice dish_price =
	 * (Dish.DishPrice)adapterview.getAdapter().getItem(pos);
	 * adapterview.setTag(dish_price.getPriceid()); orderbuttonview.setTag(1,
	 * dish_price.getPriceid()); }
	 * 
	 * @Override public void onNothingSelected(AdapterView<?> arg0) {
	 * 
	 * } }
	 */

	public void clear() {
		if (null != list_dishes) {
			list_dishes.clear();
		}
	}

	private File loadImageFile(Dish dish) {
		if (null == cache_dir) {
			cache_dir = new File(context.getFilesDir(), "imgcache");
		}
		return new File(cache_dir, dish.getImgfilename());
	}

	public class PriceSpinOnSelectListener implements OnItemSelectedListener {
		private Dish dish;

		public PriceSpinOnSelectListener(Dish dish) {
			this.dish = dish;
		}

		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view,
				int pos, long arg3) {
			if (current_order != null
					&& current_order.getOrderedDishNum(dish.getDishid()) != -1) {
				int org_num = current_order.removeDish(context, dish
						.getDishid(), -1);
				long selected_priceid = ((Dish.DishPrice) adapterView
						.getSelectedItem()).getPriceid();
				current_order.addDish(context, dish.getDishid(),
						selected_priceid, org_num);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}

	}
}

/**
 * 
 */
package com.outsourcing.industry.restaurant.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.outsourcing.industry.restaurant.R;
import com.outsourcing.industry.restaurant.entity.Order;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
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
public class ListOrderAdapter extends BaseAdapter implements OnClickListener{
	private List<Order> list_orders;
	private Context context;
	private File cache_dir;
	private TextView text_suit_custtype, text_suit_custno, text_totalprice, text_dishestotalnum;
	private ImageButton imgbtn_operation;
	
	public ListOrderAdapter(Context context){
		this.context = context;
		list_orders = new ArrayList<Order>();
		cache_dir = new File(context.getFilesDir(), "imgcache");
	}
	
	public void setListDishes(List<Order> list_orders){
		this.list_orders = list_orders;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		if(null != list_orders){
			return list_orders.size();
		} 
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int pos) {
		if(null != list_orders && pos < list_orders.size()){
			return list_orders.get(pos);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int pos) {
		if(null != list_orders && pos < list_orders.size()){
			return list_orders.get(pos).getOrder_id();
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int pos, View view, ViewGroup arg2) {
		Order order = list_orders.get(pos);
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(null == view){
			view = inflater.inflate(R.layout.sampleorder_tablerow, null);	
		}
		text_suit_custtype = (TextView)view.findViewById(R.id.text_suit_custtype);
		text_suit_custno = (TextView)view.findViewById(R.id.text_suit_custno);
		text_totalprice = (TextView)view.findViewById(R.id.text_totalprice);
		text_dishestotalnum = (TextView)view.findViewById(R.id.text_dishestotalnum);
		imgbtn_operation = (ImageButton)view.findViewById(R.id.imgbtn_operation);
		imgbtn_operation.setOnClickListener(this);
		return view;
	}
	
	public void clear(){
		if(null != list_orders){
			list_orders.clear();
		}
	}

	@Override
	public void onClick(View view) {
		
		
	}
}

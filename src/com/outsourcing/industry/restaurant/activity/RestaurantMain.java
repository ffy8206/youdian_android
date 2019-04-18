/**
 * 
 */
package com.outsourcing.industry.restaurant.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.outsourcing.industry.restaurant.R;
import com.outsourcing.industry.restaurant.adapter.ListDishAdapter;
import com.outsourcing.industry.restaurant.component.NewOrderDialog;
import com.outsourcing.industry.restaurant.component.OrderDialog;
import com.outsourcing.industry.restaurant.component.SampleOrderDialog;
import com.outsourcing.industry.restaurant.entity.Dish;
import com.outsourcing.industry.restaurant.entity.DishFactory;
import com.outsourcing.industry.restaurant.entity.DishType;
import com.outsourcing.industry.restaurant.entity.Order;
import com.outsourcing.industry.restaurant.entity.Shop;
import com.outsourcing.industry.restaurant.network.NetworkChecker;
import com.outsourcing.industry.restaurant.util.DBHelper;
import com.outsourcing.industry.restaurant.util.GlobalParam;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.method.SingleLineTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * @author Tianhua, Pan
 * 
 */
public class RestaurantMain extends Activity implements View.OnClickListener,
		OnItemSelectedListener, OnItemClickListener {
	private TextView text_clocktime, text_announce, text_dishname,
			text_dishintro, text_rawmaterialintro, text_shopname,
			text_shopaddress, text_dishserial, text_personalcost,
			text_shoptype, text_shopdesc;
	private ClockTimeThread thread_clocktime;
	private EditText edit_phone1, edit_phone2, edit_phone3, edit_phone4;
	private ImageButton imgbtn_neworder, imgbtn_order, imgbtn_exampleorder,
			imgbtn_down, imgbtn_up, imgbtn_galleryleft, imgbtn_galleryright,
			imgbtn_makeorder, imgbtn_changeview, imgbtn_discount, imgbtn_us,
			imgbtn_set, imgbtn_join;
	private Button btn_title_typeclass, btn_dishcat0, btn_dishcat1,
			btn_dishcat2, btn_dishcat3, btn_dishcat4, btn_dishcat5;
	private Order current_order;
	private DishType current_dishType;
	private Dish current_dish;
	private Handler handler_clocktime, handler_order;
	private ImageView imgview_dish_pic, imgview_dishingallery0,
			imgview_dishingallery1, imgview_dishingallery2,
			imgview_dishingallery3, imgview_dishingallery4,
			imgview_dishingallery5, imgview_shoppic;
	private ListView listview_dishes;
	private List<DishType> list_allDishTypes;
	private List<Dish> list_dishesToType;

	private ArrayAdapter<Dish.DishPrice> adapter_dishprice_spinner;
	private ListDishAdapter adapter_dishes;
	private Spinner spinner_selected_price;
	// index position of first element in list_allDishTypes
	private int index_dishTypeStartCursor;
	private int index_dishStartCursor;
	private final static int TOTAL_DISPLAYTYPES = 6;
	private final static int TOTAL_DISPLAYDISHES = 6;

	private final static int VIEW_GALLERARY = 0;
	private final static int VIEW_LISTVIEW = 1;
	private final static int VIEW_DISCOUNT = 2;
	private final static int VIEW_ABOUTUS = 3;
	private final static int VIEW_SETTING = 4;
	private int current_screen_view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// if check login returns false, go to login screen
		if (!checkLogin()) {
			Intent loginintent = new Intent();
			loginintent.setClass(this, LoginActivity.class);
			startActivity(loginintent);
			finish();
		} else {
			// init main activity
			// setContentView(R.layout.main_screen);
			handler_order = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					if (msg.obj != null) {
						// if there's order from dialog, persist old one
						persistOrder();
						current_order = (Order) msg.obj;
					}
				}
			};
			handler_clocktime = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					String str_time = (String) msg.obj;
					text_clocktime.setText(str_time);
				}
			};
		}
		current_screen_view = VIEW_GALLERARY;
		initScreen();
	}

	@Override
	protected void onResume() {
		super.onResume();

		thread_clocktime = new ClockTimeThread(handler_clocktime);
		new Thread(thread_clocktime).start();
	}

	private void initScreen() {
		// load all dish type fill navigator bar
		if (null == list_allDishTypes) {
			list_allDishTypes = loadAllDishType();
		}
		if (null != list_allDishTypes && list_allDishTypes.size() > 0) {
			if (current_dishType == null) {
				current_dishType = list_allDishTypes.get(0);
			}
		}
		if (current_screen_view == VIEW_GALLERARY) {
			setContentView(R.layout.main_screen);
			retrieveAndSetPublicViewInfo();

			text_dishname = (TextView) findViewById(R.id.text_dishname);
			text_dishintro = (TextView) findViewById(R.id.text_dishintro);
			text_rawmaterialintro = (TextView) findViewById(R.id.text_rawmaterialintro);
			imgbtn_galleryleft = (ImageButton) findViewById(R.id.imgbtn_galleryleft);
			imgbtn_galleryleft.setOnClickListener(this);
			imgbtn_galleryright = (ImageButton) findViewById(R.id.imgbtn_galleryright);
			imgbtn_galleryright.setOnClickListener(this);
			imgbtn_makeorder = (ImageButton) findViewById(R.id.imgbtn_makeorder);
			imgbtn_makeorder.setOnClickListener(this);
			imgview_dishingallery0 = (ImageView) findViewById(R.id.imgview_dishingallery0);
			imgview_dishingallery0.setOnClickListener(this);
			imgview_dishingallery1 = (ImageView) findViewById(R.id.imgview_dishingallery1);
			imgview_dishingallery1.setOnClickListener(this);
			imgview_dishingallery2 = (ImageView) findViewById(R.id.imgview_dishingallery2);
			imgview_dishingallery2.setOnClickListener(this);
			imgview_dishingallery3 = (ImageView) findViewById(R.id.imgview_dishingallery3);
			imgview_dishingallery3.setOnClickListener(this);
			imgview_dishingallery4 = (ImageView) findViewById(R.id.imgview_dishingallery4);
			imgview_dishingallery4.setOnClickListener(this);
			imgview_dishingallery5 = (ImageView) findViewById(R.id.imgview_dishingallery5);
			imgview_dishingallery5.setOnClickListener(this);

			spinner_selected_price = (Spinner) findViewById(R.id.spinner_selected_price);
			adapter_dishprice_spinner = new ArrayAdapter<Dish.DishPrice>(this,
					R.layout.main_spinner_text);
			adapter_dishprice_spinner
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner_selected_price.setAdapter(adapter_dishprice_spinner);
			spinner_selected_price.setOnItemSelectedListener(this);
			imgbtn_changeview.setImageResource(R.drawable.changetolist_before);
			imgview_dish_pic = (ImageView) findViewById(R.id.imgview_dish_pic);
			refreshNavigator();

		} else if (current_screen_view == VIEW_LISTVIEW) {
			setContentView(R.layout.main_listview);
			retrieveAndSetPublicViewInfo();
			imgbtn_changeview.setImageResource(R.drawable.changetolist_after);
			listview_dishes = (ListView) findViewById(R.id.listview_dishes);
			adapter_dishes = new ListDishAdapter(this, current_order);
			listview_dishes.setAdapter(adapter_dishes);
			listview_dishes.setOnItemClickListener(this);
			refreshNavigator();
		} else if (current_screen_view == VIEW_DISCOUNT) {
			setContentView(R.layout.main_discount);
			retrieveAndSetPublicViewInfo();
			imgbtn_join = (ImageButton) findViewById(R.id.imgbtn_join);
			edit_phone1 = (EditText) findViewById(R.id.edit_phone1);
			edit_phone2 = (EditText) findViewById(R.id.edit_phone2);
			edit_phone3 = (EditText) findViewById(R.id.edit_phone3);
			edit_phone4 = (EditText) findViewById(R.id.edit_phone4);
			imgbtn_join.setOnClickListener(this);
			refreshNavigator();
		} else {
			setContentView(R.layout.main_us);
			retrieveAndSetPublicViewInfo();
			Shop shop = DBHelper.getInstance(this).queryShopDetail();
			imgview_shoppic = (ImageView) findViewById(R.id.imgview_shoppic);
			imgview_shoppic.setImageURI(Uri.fromFile(loadImageFile(shop)));
			text_shopname = (TextView) findViewById(R.id.text_shopname);
			text_shopname.setText(shop.getShopname());
			text_shopaddress = (TextView) findViewById(R.id.text_shopaddress);
			text_shopaddress.setText(shop.getAddress());
			text_dishserial = (TextView) findViewById(R.id.text_dishserial);
			text_dishserial.setText(shop.getDishserias());
			text_personalcost = (TextView) findViewById(R.id.text_personalcost);
			text_personalcost.setText("" + shop.getMincost() + " - "
					+ shop.getMaxcost());
			text_shoptype = (TextView) findViewById(R.id.text_shoptype);
			text_shoptype.setText(shop.getShopfeature());
			text_shopdesc = (TextView) findViewById(R.id.text_shopdesc);
			text_shopdesc.setText(shop.getShopsummary());
			refreshNavigator();
		}

	}

	private void retrieveAndSetPublicViewInfo() {
		text_clocktime = (TextView) findViewById(R.id.text_clocktime);
		if (null != thread_clocktime) {
			thread_clocktime.stop();
		}
		thread_clocktime = new ClockTimeThread(handler_clocktime);
		new Thread(thread_clocktime).start();

		text_announce = (TextView) findViewById(R.id.text_announce);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String announce = prefs.getString("announce", "");
		while(announce.length()<30){
			announce+=" ";
		}
		text_announce.setText(announce);
		text_announce.setTransformationMethod(SingleLineTransformationMethod.getInstance());
		text_announce.requestFocus();

		imgbtn_neworder = (ImageButton) findViewById(R.id.imgbtn_neworder);
		imgbtn_neworder.setOnClickListener(this);

		imgbtn_order = (ImageButton) findViewById(R.id.imgbtn_order);
		imgbtn_order.setOnClickListener(this);

		imgbtn_exampleorder = (ImageButton) findViewById(R.id.imgbtn_exampleorder);
		imgbtn_exampleorder.setOnClickListener(this);
		if (current_screen_view != VIEW_ABOUTUS
				&& current_screen_view != VIEW_DISCOUNT) {
			imgbtn_changeview = (ImageButton) findViewById(R.id.imgbtn_changeview);
			imgbtn_changeview.setOnClickListener(this);
		}

		btn_dishcat0 = (Button) findViewById(R.id.btn_dishcat0);
		btn_dishcat0.setBackgroundResource(R.drawable.cat_before);
		btn_dishcat0.setTag(null);
		btn_dishcat0.setOnClickListener(this);
		btn_dishcat1 = (Button) findViewById(R.id.btn_dishcat1);
		btn_dishcat1.setBackgroundResource(R.drawable.cat_before);
		btn_dishcat1.setTag(null);
		btn_dishcat1.setOnClickListener(this);
		btn_dishcat2 = (Button) findViewById(R.id.btn_dishcat2);
		btn_dishcat2.setBackgroundResource(R.drawable.cat_before);
		btn_dishcat2.setTag(null);
		btn_dishcat2.setOnClickListener(this);
		btn_dishcat3 = (Button) findViewById(R.id.btn_dishcat3);
		btn_dishcat3.setBackgroundResource(R.drawable.cat_before);
		btn_dishcat3.setTag(null);
		btn_dishcat3.setOnClickListener(this);
		btn_dishcat4 = (Button) findViewById(R.id.btn_dishcat4);
		btn_dishcat4.setBackgroundResource(R.drawable.cat_before);
		btn_dishcat4.setTag(null);
		btn_dishcat4.setOnClickListener(this);
		btn_dishcat5 = (Button) findViewById(R.id.btn_dishcat5);
		btn_dishcat5.setBackgroundResource(R.drawable.cat_before);
		btn_dishcat5.setTag(null);
		btn_dishcat5.setOnClickListener(this);
		btn_title_typeclass = (Button) findViewById(R.id.btn_title_typeclass);
		btn_title_typeclass.setOnClickListener(this);
		btn_title_typeclass.setText(current_dishType.getName());
		imgbtn_down = (ImageButton) findViewById(R.id.imgbtn_down);
		imgbtn_down.setOnClickListener(this);
		imgbtn_up = (ImageButton) findViewById(R.id.imgbtn_up);
		imgbtn_up.setOnClickListener(this);

		imgbtn_discount = (ImageButton) findViewById(R.id.imgbtn_discount);
		imgbtn_discount.setOnClickListener(this);
		imgbtn_us = (ImageButton) findViewById(R.id.imgbtn_us);
		imgbtn_us.setOnClickListener(this);
		imgbtn_set = (ImageButton) findViewById(R.id.imgbtn_set);
		imgbtn_set.setOnClickListener(this);

	}

	private void refreshListView() {
		adapter_dishes.setListDishes(list_dishesToType);
		adapter_dishes.notifyDataSetChanged();
	}

	/*
	 * private void selectDishType() {
	 * 
	 * }
	 */

	public void setAllEmptySelection() {
		if (current_screen_view == VIEW_GALLERARY) {
			imgview_dishingallery0.setVisibility(View.GONE);
			imgview_dishingallery1.setVisibility(View.GONE);
			imgview_dishingallery2.setVisibility(View.GONE);
			imgview_dishingallery3.setVisibility(View.GONE);
			imgview_dishingallery4.setVisibility(View.GONE);
			imgview_dishingallery5.setVisibility(View.GONE);

			imgbtn_makeorder.setVisibility(View.GONE);
			text_dishname.setText("");
			text_dishintro.setText("");
			text_rawmaterialintro.setText("");
			adapter_dishprice_spinner.clear();
			adapter_dishprice_spinner.notifyDataSetChanged();
		} else if (current_screen_view == VIEW_LISTVIEW) {
			adapter_dishes.clear();
			adapter_dishes.notifyDataSetChanged();
		}
	}

	private void selectDish() {
		if (current_dish != null) {
			text_dishname.setText(current_dish.getName());
			text_dishintro.setText(current_dish.getDesc());
			text_rawmaterialintro.setText(current_dish.getStuff());
			adapter_dishprice_spinner.clear();
			Dish.DishPrice[] dish_prices = current_dish.dish_prices;
			for (int i = 0; i < dish_prices.length; i++) {
				adapter_dishprice_spinner.add(dish_prices[i]);
			}
			adapter_dishprice_spinner.notifyDataSetChanged();
			imgbtn_makeorder.setVisibility(View.VISIBLE);
			imgbtn_makeorder.setTag(adapter_dishprice_spinner.getItem(0));
			if (current_order == null
					|| current_order
							.getOrderedDishNum(adapter_dishprice_spinner
									.getItem(0).getDishid()) == -1) {
				imgbtn_makeorder.setImageResource(R.drawable.makeorder_before);
			} else {
				imgbtn_makeorder.setImageResource(R.drawable.makeorder_after);
				long priceid = current_order
						.getPirceidBySelectedDishid(current_dish.getDishid());
				Dish.DishPrice dishPrice_object = DishFactory.getInstance(this)
						.getPriceObjectByPriceid(priceid);
				spinner_selected_price.setSelection(adapter_dishprice_spinner
						.getPosition(dishPrice_object));
			}
			imgview_dish_pic.setImageURI(Uri
					.fromFile(loadImageFile(current_dish)));
		}

	}

	private void refreshNavigator() {
		for (int i = 0; i < TOTAL_DISPLAYTYPES; i++) {
			int index = i + index_dishTypeStartCursor;
			if (index < list_allDishTypes.size()) {
				DishType dishtype = list_allDishTypes.get(index);
				switch (i) {
				case 0:
					updateTypeButton(btn_dishcat0, dishtype);
					break;
				case 1:
					updateTypeButton(btn_dishcat1, dishtype);
					break;
				case 2:
					updateTypeButton(btn_dishcat2, dishtype);
					break;
				case 3:
					updateTypeButton(btn_dishcat3, dishtype);
					break;
				case 4:
					updateTypeButton(btn_dishcat4, dishtype);
					break;
				case 5:
					updateTypeButton(btn_dishcat5, dishtype);
					break;
				default:
					break;
				}
			}
		}
		setAllEmptySelection();
		index_dishStartCursor = 0;

		if (current_dishType != null) {
			btn_title_typeclass.setText(current_dishType.getName());
			DBHelper dbHelper = DBHelper.getInstance(this);
			list_dishesToType = dbHelper
					.queryAllDishesByTypeid(current_dishType.getTypeid());
			if (null != list_dishesToType && list_dishesToType.size() > 0) {
				current_dish = list_dishesToType.get(0);
				if (current_screen_view == VIEW_GALLERARY) {
					refreshGallery();

				} else if (current_screen_view == VIEW_LISTVIEW) {
					refreshListView();
				}

			}
		}
	}

	private void updateTypeButton(Button button, DishType dishtype) {
		button.setText(dishtype.getName());
		button.setTag(dishtype);
		if (dishtype.getTypeid() == current_dishType.getTypeid()) {
			button.setBackgroundResource(R.drawable.dishcat_after);
		} else {
			button.setBackgroundResource(R.drawable.dishcat_before);
		}
	}

	private void updateGalleryImage(ImageView imgview, Dish dish) {
		Uri imagefileUri = Uri.fromFile(loadPreviewFile(dish));
		imgview.setImageURI(imagefileUri);
		imgview.setTag(dish);
		imgview.setVisibility(View.VISIBLE);
		if (dish.getDishid() == current_dish.getDishid()) {
			imgview.setBackgroundResource(R.drawable.gallery_dishselect_bg);
		} else {
			imgview.setBackgroundResource(R.drawable.gallery_dishunselect_bg);
		}
	}

	private void refreshGallery() {
		for (int i = 0; i < TOTAL_DISPLAYDISHES; i++) {
			int index = i + index_dishStartCursor;
			if (index < list_dishesToType.size()) {
				Dish dish = list_dishesToType.get(index);
				switch (i) {
				case 0:
					updateGalleryImage(imgview_dishingallery0, dish);
					break;
				case 1:
					updateGalleryImage(imgview_dishingallery1, dish);
					break;
				case 2:
					updateGalleryImage(imgview_dishingallery2, dish);
					break;
				case 3:
					updateGalleryImage(imgview_dishingallery3, dish);
					break;
				case 4:
					updateGalleryImage(imgview_dishingallery4, dish);
					break;
				case 5:
					updateGalleryImage(imgview_dishingallery5, dish);
					break;
				default:
					break;
				}
			}
		}
		selectDish();
	}

	private List<DishType> loadAllDishType() {
		return DBHelper.getInstance(this).queryAllDishType();
	}

	@Override
	protected void onPause() {
		super.onPause();
		persistOrder();
		if (null != thread_clocktime) {
			thread_clocktime.stop();
		}
	}

	private void persistOrder() {
		if (null != current_order) {
			DBHelper.getInstance(this).create(current_order);
		}
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder alertDiag = new AlertDialog.Builder(this);
		alertDiag.setTitle(R.string.label_quit);
		alertDiag.setMessage(R.string.label_passtoquit);
		final View view_passwd = LayoutInflater.from(this).inflate(
				R.layout.dialog_passinput, null);
		alertDiag.setView(view_passwd);
		alertDiag.setPositiveButton(R.string.label_confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText edit_passwd = (EditText) view_passwd
								.findViewById(R.id.edit_passinput);
						String passwd = edit_passwd.getText().toString();
						if (passwd.equals(GlobalParam.QUIT_PASS)) {
							finish();
						}

					}
				}).setNegativeButton(R.string.label_cancel, null).show();
	}

	private boolean checkLogin() {
		SharedPreferences prefs = getSharedPreferences(getPackageName(),
				MODE_PRIVATE);
		String loginname = prefs.getString("loginname", "");
		String password = prefs.getString("loginpass", "");
		// if neither loginname nor password is empty, treat it as already
		// login.
		if (!loginname.equals("") && !password.equals("")) {
			return true;
		}
		return false;
	}

	class ClockTimeThread implements Runnable {
		private Handler handler;
		private SimpleDateFormat sdf;
		private boolean keepRun;

		public ClockTimeThread(Handler handler) {
			this.keepRun = true;
			this.handler = handler;
			this.sdf = new SimpleDateFormat("HH:mm");
		}

		@Override
		public void run() {
			while (keepRun) {
				String str_time = sdf.format(new Date());
				Message message = new Message();
				message.obj = str_time;
				handler.sendMessage(message);
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					Log.e(GlobalParam.TAG, "InterruptedException", e);
				}
			}

		}

		public void stop() {
			keepRun = false;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgbtn_neworder:
			imgbtn_neworder.setImageResource(R.drawable.neworder_after);
			NewOrderDialog neworderDialog = new NewOrderDialog(this,
					handler_order);
			neworderDialog
					.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							imgbtn_neworder
									.setImageResource(R.drawable.neworder_before);
						}
					});

			break;
		case R.id.imgbtn_order:
			imgbtn_order.setImageResource(R.drawable.order_after);
			OrderDialog orderDialog = new OrderDialog(this, handler_order,
					current_order);
			orderDialog
					.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							imgbtn_order
									.setImageResource(R.drawable.order_before);
						}
					});
			break;
		case R.id.imgbtn_exampleorder:
			imgbtn_exampleorder.setImageResource(R.drawable.exampleorder_after);
			SampleOrderDialog sampleOrderDialog = new SampleOrderDialog(this);
			sampleOrderDialog
					.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							imgbtn_exampleorder
									.setImageResource(R.drawable.exampleorder_before);
						}
					});

			break;
		case R.id.imgbtn_down:
			index_dishTypeStartCursor++;
			if (index_dishTypeStartCursor < list_allDishTypes.size() - 5) {
				refreshNavigator();
			} else {
				index_dishTypeStartCursor = list_allDishTypes.size() - 6;
			}
			break;

		case R.id.imgbtn_up:
			index_dishTypeStartCursor--;
			if (index_dishTypeStartCursor >= 0) {
				refreshNavigator();
			} else {
				index_dishTypeStartCursor = 0;
			}
			break;

		case R.id.imgbtn_galleryleft:
			index_dishStartCursor--;
			if (index_dishStartCursor >= 0) {
				refreshGallery();
			} else {
				index_dishStartCursor = 0;
			}
			break;

		case R.id.imgbtn_galleryright:
			if (list_dishesToType.size() > TOTAL_DISPLAYDISHES) {
				index_dishStartCursor++;
				if (index_dishStartCursor < list_dishesToType.size() - 5) {
					refreshGallery();
				} else {
					index_dishStartCursor = list_dishesToType.size() - 6;
				}
			}
			break;

		case R.id.imgbtn_makeorder:
			if (null == current_order) {
				Toast.makeText(this, R.string.error_noorder, Toast.LENGTH_LONG)
						.show();
				return;
			}
			Object _o = imgbtn_makeorder.getTag();
			if (null == _o) {
				imgbtn_makeorder.setVisibility(View.GONE);
			} else {
				Dish.DishPrice selectedPrice = (Dish.DishPrice) _o;
				Log.d(GlobalParam.TAG, selectedPrice.toString());
				if (current_order.getOrderedDishNum(current_dish.getDishid()) != -1) {
					imgbtn_makeorder
							.setImageResource(R.drawable.makeorder_before);
					current_order
							.removeDish(this, current_dish.getDishid(), -1);
				} else {
					imgbtn_makeorder
							.setImageResource(R.drawable.makeorder_after);
					current_order.addDish(this, current_dish.getDishid(),
							selectedPrice.getPriceid(), 1);

				}
			}
			break;
		case R.id.btn_dishcat0:
		case R.id.btn_dishcat1:
		case R.id.btn_dishcat2:
		case R.id.btn_dishcat3:
		case R.id.btn_dishcat4:
		case R.id.btn_dishcat5:
			if (v.getTag() != null) {
				if (current_dishType.getTypeid() == ((DishType) v.getTag())
						.getTypeid()) {
					return;
				} else {
					current_dishType = (DishType) v.getTag();
				}
			} else {
				current_dishType = null;
			}
			if (current_screen_view != VIEW_LISTVIEW) {
				current_screen_view = VIEW_GALLERARY;
			}
			initScreen();
			break;
		case R.id.imgview_dishingallery0:
		case R.id.imgview_dishingallery1:
		case R.id.imgview_dishingallery2:
		case R.id.imgview_dishingallery3:
		case R.id.imgview_dishingallery4:
		case R.id.imgview_dishingallery5:
			if (v.getTag() != null) {
				current_dish = (Dish) v.getTag();
			} else {
				current_dish = null;
			}
			refreshGallery();
			break;
		case R.id.imgbtn_changeview:
			if (current_screen_view == VIEW_GALLERARY) {
				current_screen_view = VIEW_LISTVIEW;
			} else {
				current_screen_view = VIEW_GALLERARY;
			}
			initScreen();
			break;
		case R.id.imgbtn_discount:
			if (current_screen_view != VIEW_DISCOUNT) {
				current_screen_view = VIEW_DISCOUNT;
				initScreen();
			}
			break;
		case R.id.imgbtn_us:
			if (current_screen_view != VIEW_ABOUTUS) {
				current_screen_view = VIEW_ABOUTUS;
				initScreen();
			}
			break;
		case R.id.imgbtn_set:
			Intent intent = new Intent();
			intent.setClass(RestaurantMain.this, SettingPreference.class);
			startActivityForResult(intent, 1);
			break;
		default:
			break;
		}
	}

	private File loadImageFile(Dish dish) {
		File cache_dir = new File(getFilesDir(), "imgcache");
		cache_dir.mkdirs();
		return new File(cache_dir, dish.getImgfilename());
	}

	private File loadPreviewFile(Dish dish) {
		File cache_dir = new File(getFilesDir(), "imgcache");
		return new File(cache_dir, dish.getPreviewfilename());
	}

	private File loadImageFile(Shop shop) {
		File cache_dir = new File(getFilesDir(), "imgcache");
		return new File(cache_dir, shop.getImgfile());
	}

	@Override
	public void onItemSelected(AdapterView<?> adapter, View arg1, int position,
			long arg3) {
		imgbtn_makeorder.setTag(adapter_dishprice_spinner.getItem(position));
		if (current_dish != null
				&& current_order != null
				&& current_order.getOrderedDishNum(current_dish.getDishid()) != -1) {
			int org_num = current_order.removeDish(this, current_dish
					.getDishid(), -1);
			long selected_priceid = ((Dish.DishPrice) adapter.getSelectedItem())
					.getPriceid();
			current_order.addDish(this, current_dish.getDishid(),
					selected_priceid, org_num);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (text_announce != null) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			String announce = prefs.getString("announce", "");
			while(announce.length()<30){
				announce+=" ";
			}
			text_announce.setText(announce);
		}
	}

}

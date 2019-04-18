/**
 * 
 */
package com.outsourcing.industry.restaurant.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.outsourcing.industry.restaurant.util.DBHelper;

/**
 * @author Tianhua, Pan
 *
 */
public class DishFactory {
	private static DishFactory instance;
	private Context context;
	private Map<Integer, Dish> map_alldishes;
	private Map<Long, Dish.DishPrice> map_allprices;
	private DishFactory(Context context){
		this.context = context;
		map_alldishes = new ConcurrentHashMap<Integer, Dish>();
		map_allprices = new ConcurrentHashMap<Long, Dish.DishPrice>();
		loadAllDishes();
		
	}
	public static DishFactory getInstance(Context context){
		if(instance == null){
			instance = new DishFactory(context);
		}
		return instance;
	}
	
	private void loadAllDishes(){
		DBHelper dbHelper = DBHelper.getInstance(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query("dish", null, null, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			int dishid = cursor.getInt(cursor.getColumnIndex("dishid"));
			String dishfono = cursor.getString(cursor.getColumnIndex("dishfono"));
			Dish dish = new Dish(dishid, dishfono);
			dish.setName(cursor.getString(cursor.getColumnIndex("dishname")));
			dish.setDesc(cursor.getString(cursor.getColumnIndex("dishdesc")));
			dish.setStuff(cursor.getString(cursor.getColumnIndex("dishstuff")));
			dish.setDishsubtype(cursor.getInt(cursor.getColumnIndex("dishsubtype")));
			dish.setIsdiscount(cursor.getInt(cursor.getColumnIndex("isdiscount")));
			dish.setIsspecial(cursor.getInt(cursor.getColumnIndex("isspecial")));
			dish.setImgfilename(cursor.getString(cursor.getColumnIndex("image_filename")));
			dish.setPreviewfilename(cursor.getString(cursor.getColumnIndex("preview_filename")));
			dish.setImgpreviewurl(cursor.getString(cursor.getColumnIndex("preview_url")));
			dish.setImgurl(cursor.getString(cursor.getColumnIndex("image_url")));
			//dish.setPrice(cursor.getFloat(cursor.getColumnIndex("price")));
			//dish.setUnits(cursor.getString(cursor.getColumnIndex("price_unit")));
				
			Cursor cursor_price = db.query("dish_price", new String[]{"rowid, *"}, "dishid=?", new String[]{""+dishid}, null, null, null);
			int price_count = cursor_price.getCount();
			if(price_count > 0){
				dish.dish_prices = new Dish.DishPrice[price_count];
				cursor_price.moveToFirst();
				int count = 0;
				while(!cursor_price.isAfterLast()){
					dish.dish_prices[count] = new Dish.DishPrice();
					dish.dish_prices[count].setDishid(dishid);
					dish.dish_prices[count].setPrice(cursor_price.getFloat(cursor_price.getColumnIndex("price")));
					long rowid = cursor_price.getLong(cursor_price.getColumnIndex("rowid"));
					dish.dish_prices[count].setPriceid(rowid);
					dish.dish_prices[count].setUnits(cursor_price.getString(cursor_price.getColumnIndex("price_unit")));
					map_allprices.put(rowid, dish.dish_prices[count]);
					count++ ;
					cursor_price.moveToNext();
				}
			}
			else{
				dish.dish_prices = new Dish.DishPrice[0];
			}	
			map_alldishes.put(dishid, dish);
			cursor_price.close();
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
	}
	
	public Dish getDishById(int dishid){
		if(null == map_alldishes){
			map_alldishes = new ConcurrentHashMap<Integer, Dish>();
			loadAllDishes();
		}
		return map_alldishes.get(dishid);
	}
	
	public float getPriceByPriceid(long priceid){
		if(map_allprices.containsKey(priceid)){
			return map_allprices.get(priceid).getPrice();
		}
		return 0.00f;
	}
	
	public Dish.DishPrice getPriceObjectByPriceid(long priceid){
		if(map_allprices.containsKey(priceid)){
			return map_allprices.get(priceid);
		}
		return null;
	}
	
/*	public static List<Dish> getDishByFono(Context context, String fono){
		List<Dish> list_dishes = null;
		DBHelper dbHelper = DBHelper.getInstance(context);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query("dish", null, "dishfono=?", new String[]{fono}, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			if(null == list_dishes){
				list_dishes = new ArrayList<Dish>();
			}
			int dishid = cursor.getInt(cursor.getColumnIndex("dishid"));
			String dishfono = cursor.getString(cursor.getColumnIndex("dishfono"));
			Dish dish = new Dish(dishid, dishfono);
			
		}
	
		return list_dishes;
	}*/
}

/**
 * 
 */
package com.outsourcing.industry.restaurant.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.outsourcing.industry.restaurant.entity.Dish;
import com.outsourcing.industry.restaurant.entity.DishFactory;
import com.outsourcing.industry.restaurant.entity.DishType;
import com.outsourcing.industry.restaurant.entity.Order;
import com.outsourcing.industry.restaurant.entity.Shop;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Tianhua, Pan
 * 
 */
public class DBHelper extends SQLiteOpenHelper {
	private final static String DBNAME = "youdian";
	private final static int DBVERSION = 1;
	private static DBHelper instance;
	private SQLiteDatabase sql_db;
	private Context context;

	private DBHelper(Context context) {
		super(context, DBNAME, null, DBVERSION);
		this.context = context;

	}

	public static DBHelper getInstance(Context context) {
		if (null == instance) {
			instance = new DBHelper(context);
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE table dish_type (typeid INTEGER PRIMARY KEY, "
				+ "typename VARCHAR(50) not null, identifer VARCHAR(50), parentid INTEGER not null default 0)";
		db.execSQL(sql);
		sql = "CREATE table dish (dishid INTEGER PRIMARY KEY, dishfono VARCHAR(50) not null, dishname VARCHAR(50) not null, "
				+ "dishsubtype INTEGER not null default 0, dishdesc VARCHAR(300), dishstuff VARCHAR(300),"
				+ "isdiscount INTEGER not null default 0, isspecial INTEGER not null default 0, "
				+ "image_filename VARCHAR(50), preview_filename VARCHAR(50), image_url VARCHAR(200), preview_url VARCHAR(200))";
		db.execSQL(sql);
		sql = "CREATE table dish_type_mapping (dish_id INTEGER not null, dish_typeid INTEGER not null)";
		db.execSQL(sql);
		sql = "CREATE table dish_order (create_time LONG not null, tableid INTEGER not null, "
				+ "customernum INTEGER not null, waiterid VARCHAR(10) not null, "
				+ "place INTEGER not null, notes varchar(200), submitstatus short not null default 0)";
		db.execSQL(sql);
		//sql = "CREATE table sample_order ()";
		sql = "CREATE table order_dish_mapping (orderid LONG not null, dishid INTEGER not null, orderednum INTEGER not null, priceid LONG not null)";
		db.execSQL(sql);
		sql = "CREATE table dish_price (dishid INTEGER not null, price float not null, price_unit VARCHAR(10) not null)";
		db.execSQL(sql);
		sql = "CREATE table rest_shop (restid INTEGER PRIMARY KEY, restname VARCHAR(50) not null, dishserias VARCHAR(50), " +
				"address VARCHAR(150), mincost float not null default 0.00, maxcost float not null default 0.00, " +
				"shopsummary text, feature VARCHAR(100), imgfile VARCHAR(50), imgurl VARCHAR(200))";
		db.execSQL(sql);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldver, int newver) {
		String sql = "DROP table dish_type_mapping";
		db.execSQL(sql);
		sql = "DROP table dish";
		db.execSQL(sql);
		sql = "DROP table dishtype";
		db.execSQL(sql);
		onCreate(db);
	}

	public synchronized void create(Order order) {
		sql_db = getWritableDatabase();
		sql_db.beginTransaction();
		long orderid = order.getOrder_id();
		sql_db.delete("order_dish_mapping", "orderid=?", new String[] { ""
				+ orderid });
		sql_db.delete("dish_order", "rowid=?", new String[] { "" + orderid });
		ContentValues cv = new ContentValues();
		cv.put("create_time", order.getOrder_time());
		cv.put("tableid", order.getTable_id());
		cv.put("customernum", order.getCust_num());
		cv.put("waiterid", order.getWaiter_id());
		cv.put("place", order.getOrder_place());
		cv.put("notes", order.getOrder_notes());
		cv.put("submitstatus", order.getSubmit());
		long neworderid = sql_db.insertOrThrow("dish_order", null, cv);
		order.setOrder_id(neworderid);

		Map<Integer, Integer> map_id_dish = order.getMap_orderedDishes();
		Iterator<Integer> iter = map_id_dish.keySet().iterator();
		while (iter.hasNext()) {
			int dishid = iter.next();
			Dish dish = DishFactory.getInstance(context).getDishById(dishid);
			cv = new ContentValues();
			cv.put("orderid", orderid);
			cv.put("dishid", dish.getDishid());
			cv.put("orderednum", order.getOrderedDishNum(dish.getDishid()));
			cv.put("priceid", order.getPirceidBySelectedDishid(dishid));
			sql_db.insertOrThrow("order_dish_mapping", null, cv);
		}
		sql_db.setTransactionSuccessful();
		sql_db.endTransaction();
		sql_db.close();
	}

	public synchronized List<DishType> queryAllDishType() {
		List<DishType> list_dishType = null;
		sql_db = getReadableDatabase();
		Cursor cursor = sql_db.query("dish_type", null, null, null, null, null,
				null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			if (null == list_dishType) {
				list_dishType = new ArrayList<DishType>();
			}
			DishType dishType = new DishType();
			dishType.setTypeid(cursor.getInt(cursor.getColumnIndex("typeid")));
			dishType.setIdentifer(cursor.getString(cursor
					.getColumnIndex("identifer")));
			dishType.setName(cursor
					.getString(cursor.getColumnIndex("typename")));
			dishType.setParentid(cursor.getInt(cursor
					.getColumnIndex("parentid")));
			list_dishType.add(dishType);
			cursor.moveToNext();
		}
		cursor.close();
		sql_db.close();
		return list_dishType;
	}

	public synchronized List<Dish> queryAllDishesByTypeid(int typeid) {
		List<Dish> list_dishes = null;
		sql_db = getReadableDatabase();
		String sql = "SELECT d.dishid FROM dish d, dish_type_mapping m WHERE m.dish_typeid=? and m.dish_id=d.dishid";
		Cursor cursor = sql_db.rawQuery(sql, new String[] { "" + typeid });
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			if (null == list_dishes) {
				list_dishes = new ArrayList<Dish>();
			}
			int dishid = cursor.getInt(cursor.getColumnIndex("dishid"));
			// String dishfono =
			// cursor.getString(cursor.getColumnIndex("dishfono"));
			Dish dish = DishFactory.getInstance(context).getDishById(dishid);
			/*
			 * dish.setName(cursor.getString(cursor.getColumnIndex("dishname")));
			 * dish
			 * .setDesc(cursor.getString(cursor.getColumnIndex("dishdesc")));
			 * dish
			 * .setStuff(cursor.getString(cursor.getColumnIndex("dishstuff")));
			 * dish
			 * .setDishsubtype(cursor.getInt(cursor.getColumnIndex("dishsubtype"
			 * )));
			 * dish.setIsdiscount(cursor.getInt(cursor.getColumnIndex("isdiscount"
			 * )));
			 * dish.setIsspecial(cursor.getInt(cursor.getColumnIndex("isspecial"
			 * )));dish.setImgfilename(cursor.getString(cursor.getColumnIndex(
			 * "image_filename")));
			 * dish.setImgpreviewurl(cursor.getString(cursor
			 * .getColumnIndex("preview_url")));
			 * dish.setImgurl(cursor.getString(
			 * cursor.getColumnIndex("image_url")));
			 */
			list_dishes.add(dish);
			cursor.moveToNext();
		}
		cursor.close();
		sql_db.close();
		return list_dishes;
	}

	public synchronized List<Order> queryOrderes(int tblid, int custno,
			short submit, int limit) {
		List<Order> list_orderes = null;
		sql_db = getReadableDatabase();
		Cursor cursor = null;
		StringBuilder whereClause = null;
		if (tblid != -1) {
			whereClause = new StringBuilder("tableid=" + tblid);
		}
		if (custno != -1) {
			if (whereClause == null) {
				whereClause = new StringBuilder("customernum=" + custno);
			} else {
				whereClause.append(" and customernum=" + custno);
			}
		}
		if (submit != -1) {
			if (whereClause == null) {
				whereClause = new StringBuilder("submitstatus=" + submit);
			} else {
				whereClause.append(" and submitstatus=" + submit);
			}
		}
		cursor = sql_db.query("dish_order", new String[] { "rowid", "*" },
				whereClause == null ? null : whereClause.toString(), null,
				null, null, "create_time desc", "" + limit);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			if (null == list_orderes) {
				list_orderes = new ArrayList<Order>();
			}
			long orderid = cursor.getLong(cursor.getColumnIndex("rowid"));
			int place = cursor.getInt(cursor.getColumnIndex("place"));
			String waiter_id = cursor.getString(cursor
					.getColumnIndex("waiterid"));
			int table_id = cursor.getInt(cursor.getColumnIndex("tableid"));
			int cust_num = cursor.getInt(cursor.getColumnIndex("customernum"));
			long time = cursor.getLong(cursor.getColumnIndex("create_time"));
			Order order = new Order(place, waiter_id, table_id, cust_num, time);
			order.setOrder_id(orderid);
			String notes = cursor.getString(cursor.getColumnIndex("notes"));
			order.setOrder_notes(notes);
			short submitStatus = cursor.getShort(cursor
					.getColumnIndex("submitstatus"));
			order.setSubmit(submitStatus);

			Cursor cursor_orderdish = sql_db.query("order_dish_mapping", null,
					"orderid=?", new String[] { "" + orderid }, null, null,
					null);
			cursor_orderdish.moveToFirst();
			while (!cursor_orderdish.isAfterLast()) {
				int dishid = cursor_orderdish.getInt(cursor_orderdish
						.getColumnIndex("dishid"));
				int orderednum = cursor_orderdish.getInt(cursor_orderdish
						.getColumnIndex("orderednum"));
				long orderedDishPriceid = cursor_orderdish.getLong(cursor_orderdish
						.getColumnIndex("priceid"));
				order.addDish(context, dishid, orderedDishPriceid, orderednum);
				cursor_orderdish.moveToNext();
			}
			cursor_orderdish.close();
			list_orderes.add(order);
			cursor.moveToNext();
		}
		cursor.close();
		sql_db.close();
		return list_orderes;
	}
	
	public synchronized Shop queryShopDetail(){
		Shop shop = null;
		sql_db = getReadableDatabase();
		Cursor cursor = sql_db.query("rest_shop", null, null, null, null, null, null);
		cursor.moveToFirst();
		if(!cursor.isAfterLast()){
			shop = new Shop(cursor.getInt(cursor.getColumnIndex("restid")));
			shop.setAddress(cursor.getString(cursor.getColumnIndex("address")));
			shop.setDishserias(cursor.getString(cursor.getColumnIndex("dishserias")));
			shop.setMaxcost(cursor.getFloat(cursor.getColumnIndex("maxcost")));
			shop.setMincost(cursor.getColumnIndex("mincost"));
			shop.setShopfeature(cursor.getString(cursor.getColumnIndex("feature")));
			shop.setShopname(cursor.getString(cursor.getColumnIndex("restname")));
			shop.setShopsummary(cursor.getString(cursor.getColumnIndex("shopsummary")));
			shop.setImgfile(cursor.getString(cursor.getColumnIndex("imgfile")));
			shop.setImgurl(cursor.getString(cursor.getColumnIndex("imgurl")));
		}
		cursor.close();
		sql_db.close();
		return shop;
	}
}

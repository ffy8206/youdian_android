/**
 * 
 */
package com.outsourcing.industry.restaurant.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;

/**
 * @author Tianhua, Pan
 * 
 */
public class Order {
	private long order_id;
	private int table_id;
	private int cust_num;
	private String waiter_id;
	private long order_time;
	private float total_price;
	private int order_place;
	private String order_notes;
	private short submit;
	// dish id - dish ordered number
	private Map<Integer, Integer> map_orderedDishes;
	// dish id - dish select price
	private Map<Integer, Long> map_dishprices;

	public Order() {

	}

	public Order(int place, String waiter_id, int table_id, int cust_num,
			long time) {
		this.order_place = place;
		this.table_id = table_id;
		this.waiter_id = waiter_id;
		this.cust_num = cust_num;
		this.order_time = time;
		this.map_orderedDishes = new ConcurrentHashMap<Integer, Integer>();
		this.map_dishprices = new ConcurrentHashMap<Integer, Long>();
	}

	public long getOrder_id() {
		return order_id;
	}

	public void setOrder_id(long orderId) {
		order_id = orderId;
	}

	public int getTable_id() {
		return table_id;
	}

	public void setTable_id(int tableId) {
		table_id = tableId;
	}

	public int getCust_num() {
		return cust_num;
	}

	public void setCust_num(int custNum) {
		cust_num = custNum;
	}

	public String getWaiter_id() {
		return waiter_id;
	}

	public void setWaiter_id(String waiterId) {
		waiter_id = waiterId;
	}

	public long getOrder_time() {
		return order_time;
	}

	public void setOrder_time(long orderTime) {
		order_time = orderTime;
	}

	public void addDish(Context context, int dishid, long priceid, int num) {
		if (map_orderedDishes.containsKey(dishid)) {
			int orderednum = map_orderedDishes.remove(dishid);
			orderednum += num;
			map_orderedDishes.put(dishid, orderednum);
		} else {
			map_orderedDishes.put(dishid, num);
		}
		map_dishprices.remove(dishid);
		map_dishprices.put(dishid, priceid);
		total_price += DishFactory.getInstance(context).getPriceByPriceid(
				priceid)
				* num;
	}

	public int removeDish(Context context, int dishid, int num) {
		if (map_dishprices.containsKey(dishid)) {
			long selected_priceid = map_dishprices.get(dishid);
			float dishprice = DishFactory.getInstance(context)
					.getPriceByPriceid(selected_priceid);
			int orderednum = map_orderedDishes.remove(dishid);
			if (num == -1) {
				total_price -= dishprice * orderednum;
			} else {
				if (orderednum - num < 0) {
					map_orderedDishes.put(dishid, 0);
					total_price -= dishprice * orderednum;
				} else {
					map_orderedDishes.put(dishid, orderednum - num);
					total_price -= dishprice * num;
				}
			}
			return orderednum;
		}
		return 0;
	}

	public float getTotalPrice() {
		return this.total_price;
	}

	public Map<Integer, Integer> getMap_orderedDishes() {
		return map_orderedDishes;
	}

	public int getOrder_place() {
		return order_place;
	}

	public void setOrder_place(int orderPlace) {
		order_place = orderPlace;
	}

	public String getOrder_notes() {
		return order_notes;
	}

	public void setOrder_notes(String orderNotes) {
		order_notes = orderNotes;
	}

	public short getSubmit() {
		return submit;
	}

	public void setSubmit(short submit) {
		this.submit = submit;
	}

	public int getOrderedDishNum(int dishid) {
		if (map_orderedDishes.containsKey(dishid)) {
			return map_orderedDishes.get(dishid);
		}
		return -1;
	}

	public long getPirceidBySelectedDishid(int dishid) {
		return map_dishprices.get(dishid);
	}
}

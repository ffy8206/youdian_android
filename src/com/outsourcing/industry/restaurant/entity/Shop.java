/**
 * 
 */
package com.outsourcing.industry.restaurant.entity;

/**
 * @author Tianhua, Pan
 *
 */
public class Shop {
	private int shopid;
	private String shopname;
	private String dishserias;
	private String address;
	private String shopsummary;
	private String shopfeature;
	private float mincost;
	private float maxcost;
	private String imgfile;
	private String imgurl;
	
	public Shop(int shopid){
		this.shopid = shopid;
	}

	public int getShopid() {
		return shopid;
	}

	public String getShopname() {
		return shopname;
	}

	public void setShopname(String shopname) {
		this.shopname = shopname;
	}

	public String getDishserias() {
		return dishserias;
	}

	public void setDishserias(String dishserias) {
		this.dishserias = dishserias;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getShopsummary() {
		return shopsummary;
	}

	public void setShopsummary(String shopsummary) {
		this.shopsummary = shopsummary;
	}

	public String getShopfeature() {
		return shopfeature;
	}

	public void setShopfeature(String shopfeature) {
		this.shopfeature = shopfeature;
	}

	public float getMincost() {
		return mincost;
	}

	public void setMincost(float mincost) {
		this.mincost = mincost;
	}

	public float getMaxcost() {
		return maxcost;
	}

	public void setMaxcost(float maxcost) {
		this.maxcost = maxcost;
	}

	public String getImgfile() {
		return imgfile;
	}

	public void setImgfile(String imgfile) {
		this.imgfile = imgfile;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	
	
}

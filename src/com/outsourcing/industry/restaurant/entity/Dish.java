/**
 * 
 */
package com.outsourcing.industry.restaurant.entity;

/**
 * @author Tianhua, Pan
 * 
 */
public class Dish {
	private int dishid;
	private String fono;
	private String name;
	private String desc;
	private String stuff;
	private int dishsubtype;
	private int isdiscount;
	private int isspecial;
	private String imgfilename, imgurl, previewfilename, imgpreviewurl;
	public Dish.DishPrice[] dish_prices;

	public Dish(int dishid, String fono) {
		this.dishid = dishid;
		this.fono = fono;
	}

	public int getDishid() {
		return dishid;
	}

	public int getDishsubtype() {
		return dishsubtype;
	}

	public void setDishsubtype(int dishsubtype) {
		this.dishsubtype = dishsubtype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getStuff() {
		return stuff;
	}

	public void setStuff(String stuff) {
		this.stuff = stuff;
	}

	public int getIsdiscount() {
		return isdiscount;
	}

	public void setIsdiscount(int isdiscount) {
		this.isdiscount = isdiscount;
	}

	public int getIsspecial() {
		return isspecial;
	}

	public String getFono() {
		return fono;
	}

	public void setFono(String fono) {
		this.fono = fono;
	}

	public void setIsspecial(int isspecial) {
		this.isspecial = isspecial;
	}

	public String getImgfilename() {
		return imgfilename;
	}

	public void setImgfilename(String imgfilename) {
		this.imgfilename = imgfilename;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getImgpreviewurl() {
		return imgpreviewurl;
	}

	public void setImgpreviewurl(String imgpreviewurl) {
		this.imgpreviewurl = imgpreviewurl;
	}
	
	public static class DishPrice{
		private int dishid;
		private long priceid;
		private float price;
		private String units;
		public int getDishid() {
			return dishid;
		}
		public void setDishid(int dishid) {
			this.dishid = dishid;
		}
		public float getPrice() {
			return price;
		}
		public void setPrice(float price) {
			this.price = price;
		}
		public String getUnits() {
			return units;
		}
		public void setUnits(String units) {
			this.units = units;
		}
		public long getPriceid() {
			return priceid;
		}
		public void setPriceid(long priceid) {
			this.priceid = priceid;
		}
		
		@Override
		public String toString() {
			return price + " / " + units;
		}
	}

	public Dish.DishPrice[] getDish_prices() {
		return dish_prices;
	}

	public String getPreviewfilename() {
		return previewfilename;
	}

	public void setPreviewfilename(String previewfilename) {
		this.previewfilename = previewfilename;
	}
}

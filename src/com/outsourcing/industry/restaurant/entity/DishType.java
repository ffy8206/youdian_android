/**
 * 
 */
package com.outsourcing.industry.restaurant.entity;

/**
 * @author Tianhua, Pan
 *
 */
public class DishType {
	private int typeid;
	private String identifer;
	private String name;
	private int parentid;
	private boolean selected;
	public DishType(){
		
	}
	public int getTypeid() {
		return typeid;
	}
	public void setTypeid(int typeid) {
		this.typeid = typeid;
	}
	public String getIdentifer() {
		return identifer;
	}
	public void setIdentifer(String identifer) {
		this.identifer = identifer;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getParentid() {
		return parentid;
	}
	public void setParentid(int parentid) {
		this.parentid = parentid;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	
}

package com.ibm.OrderManagement;

public class WorkOrderDTO {
	
	private String so_order_id;
	private String wo_order_id;
	private String item;
	private int  quantity;
	
	public String getSo_order_id() {
		return so_order_id;
	}
	public void setSo_order_id(String so_order_id) {
		this.so_order_id = so_order_id;
	}
	public String getWo_order_id() {
		return wo_order_id;
	}
	public void setWo_order_id(String wo_order_id) {
		this.wo_order_id = wo_order_id;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	

}

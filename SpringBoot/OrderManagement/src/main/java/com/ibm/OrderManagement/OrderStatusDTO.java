package com.ibm.OrderManagement;

public class OrderStatusDTO {
	
	private String so_order_id;
	public String getSo_order_id() {
		return so_order_id;
	}
	public void setSo_order_id(String so_order_id) {
		this.so_order_id = so_order_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	private String status;

}

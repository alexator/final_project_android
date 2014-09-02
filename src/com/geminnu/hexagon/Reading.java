package com.geminnu.hexagon;

public class Reading {
	
	private int id;
	private double value;
	private int type;
	private int user_id;
	private String time;
	private String status;
	
	public Reading(int id_prime, double value, int type, int userId, String stamp, String stat) {
		this.id = id_prime;
		this.value = value;
		this.type = type;
		this.user_id = userId;
		this.time = stamp;
		this.status = stat;
	}
	public Reading(double value, int type, int userId, String stat) {
		this.value = value;
		this.type = type;
		this.user_id = userId;
		this.status = stat;
	}
	
	public Reading() {
		
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getUserId() {
		return user_id;
	}
	
	public void setUserId(int u_id) {
		this.user_id = u_id;
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		this.time = time;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}

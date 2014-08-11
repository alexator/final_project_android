package com.geminnu.hexagon;

public class Reading {
	
	private long id;
	private float value;
	private long type;
	private long user_id;
	private String time;
	private String status;
	

	public long getId() {
		return id;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
	
	public long getType() {
		return type;
	}
	
	public void setType(long type) {
		this.type = type;
	}
	
	public long getUserId() {
		return user_id;
	}
	
	public void setUserId(long u_id) {
		this.user_id = u_id;
	}
	
	public String getTime() {
		return time;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}

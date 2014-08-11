package com.geminnu.hexagon;

public class Profile {
	
	private long id;
	private String first_name;
	private String last_name;
	private String sex;
	private long age;
	private long user_id;
	
	

	public long getId() {
		return id;
	}

	public String getFirstName() {
		return first_name;
	}

	public void setFirstName(String first) {
		this.first_name = first;
	}
	
	public String getLastName() {
		return last_name;
	}

	public void setLastName(String last) {
		this.last_name = last;
	}
	
	public String getSex() {
		return sex;
	}
	
	public void setSex(String sex) {
		this.sex = sex;
	}
	
	public long getAge() {
		return age;
	}
	
	public void setAge(long age) {
		this.age = age;
	}
	
	public long getUserId() {
		return user_id;
	}
	
	public void setUserId(long u_id) {
		this.user_id = u_id;
	}
}

package com.geminnu.hexagon;

public class Profile {
	
	private int id;
	private String first_name;
	private String last_name;
	private String sex;
	private int age;
	private int user_id;
	
	public Profile(int id_prime, String first, String last, String sex, int age, int id) {
		this.id = id_prime;
		this.first_name = first;
		this.last_name = last;
		this.sex = sex;
		this.age = age;
		this.user_id = id;
	}
	
	public Profile(String first, String last, String sex, int age, int id) {
		this.first_name = first;
		this.last_name = last;
		this.sex = sex;
		this.age = age;
		this.user_id = id;
	}

	public int getId() {
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
	
	public int getAge() {
		return age;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public int getUserId() {
		return user_id;
	}
	
	public void setUserId(int u_id) {
		this.user_id = u_id;
	}
}

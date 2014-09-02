package com.geminnu.hexagon;

public class ArduinoStatus {
	
	private int mType;
	private int mValue;
	
	public ArduinoStatus(int type, int value) {
		this.mType = type;
		this.mValue = value;
	}
	
	public int getType() {
		return mType;
	}
	
	public int getValue() {
		return mValue;
	}
}

package com.geminnu.hexagon;

public class ArduinoMessage {

	private int mMsgType;
	private int mType;
	private float mValue;
	
	public ArduinoMessage (int msg_type, int type, float value) {
		
		this.mMsgType = msg_type;
		this.mType = type;
		this.mValue = value;
	}

	public int getMsgType() {
		return mMsgType;
	}

	public int getType() {
		return mType;
	}

	public float getValue() {
		return mValue;
	}
}

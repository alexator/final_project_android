package com.geminnu.hexagon;

public class ArduinoMessage {

	private String mMsgType;
	private String mSensor;
	private float mValue;
	
	public ArduinoMessage (String msg_type, String sensor, float value) {
		
		this.mMsgType = msg_type;
		this.mSensor = sensor;
		this.mValue = value;
	}

	public String getMsgType() {
		return mMsgType;
	}

	public String getSensor() {
		return mSensor;
	}

	public float getValue() {
		return mValue;
	}
}

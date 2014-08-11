package com.geminnu.hexagon;

public class BioSensorEvent {
	//Which biosensor is responsible for the event
	private BioSensor mSensor;
	
	//The time of the event
	private long mTimeStamp;
	
	//The current reading of the biosensor
	private float mValue;
	
	
	//Constructor of the event 
	BioSensorEvent(BioSensor sensor, long timestamp, float value) {
		this.mSensor = sensor;
		this.mTimeStamp = timestamp;
		this.mValue = value;
	}
	
	//Getters
	public BioSensor getSensor() {
		return mSensor;
	}

	public long getTimestamp() {
		return mTimeStamp;
	}

	public float getValue() {
		return mValue;
	}

}

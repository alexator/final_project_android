package com.geminnu.hexagon;

public class BioSensorEvent {
	//Which biosensor is responsible for the event
	private BioSensor mSensor;
	
	//The current reading of the biosensor
	private float mValue;
	
	
	//Constructor of the event 
	BioSensorEvent(BioSensor sensor, float value) {
		this.mSensor = sensor;
		this.mValue = value;
	}
	
	//Getters
	public BioSensor getSensor() {
		return mSensor;
	}

	public float getValue() {
		return mValue;
	}

}

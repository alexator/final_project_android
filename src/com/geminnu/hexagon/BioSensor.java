package com.geminnu.hexagon;

public class BioSensor {
	
	//The name of the sensor
	private String mSensorName;
	
	//The type of the sensor (e.g.: generic, medical etc)
	private int mSensorType;
	
	//The signal type of the sensor (e.g.: analog or digital)
	private int mSignalType;
	
	//Constructor of the class
	public BioSensor(String name, int type, int signal) {
		this.mSensorName = name;
		this.mSensorType = type;
		this.mSignalType = signal;
	}
	
	//Getters
	public String getName() {
		return mSensorName;
	}
	
	public int getType() {
		return mSensorType;
	}
	
	public int getSignal() {
		return mSignalType;
	}
}

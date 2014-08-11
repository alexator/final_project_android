package com.geminnu.hexagon;

public class BioSensorListenerItem {
	
	BioSensorEventListener mListener;
	BioSensor mSensor;
	int mSampleRate;
	
	public BioSensorListenerItem(BioSensorEventListener listener, BioSensor sensor, int time) {
		this.mListener = listener;
		this.mSensor = sensor;
		this.mSampleRate = time;
	}

	public BioSensorEventListener getListener() {
		return mListener;
	}

	public BioSensor getSensor() {
		return mSensor;
	}

	public int getSampleRate() {
		return mSampleRate;
	}
	
	
	
}

package com.geminnu.hexagon;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BioSensorManagerService extends Service {

//	===========================================================================	//
//	Essential parts for the service in order to be able to bind with components	//
//	===========================================================================	//
	
	private final IBinder mBinder = new LocalBinder();
	
	public class LocalBinder extends Binder {
			
		public BioSensorManagerService getServerInstance() {
			return BioSensorManagerService.this;
	    }
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return mBinder;
	}
	
	@Override
	public void onDestroy() {
	    Log.d("HelloBindService", "onDestroy");
	}
	
//	###########################################################################	//
	
	
//	===========================================================================	//
//	Methods that provide the functionality of the service						//
//	===========================================================================	//
	
	private BioSensorEventListener mListener;
	private BioSensor mSensor;
	private int sampleRate;
	
	public void registerListener(BioSensorEventListener listener, BioSensor sensor, int sampleRate) {
		this.mListener = listener;
		this.mSensor = sensor;
		this.sampleRate = sampleRate;
		tester(mListener, mSensor, this.sampleRate );
		
	}
	
	public void unegisterListener(BioSensorEventListener listener) {
		listener = null;
	}
	
	public void tester(BioSensorEventListener listener, BioSensor sensor, int sampleRate) {
		long m = sampleRate;
		float d = (float) 2.34;
		for(int i = 0; i < 20; i++) {
			BioSensorEvent event = new BioSensorEvent(sensor, m , d+i);
			listener.onBioSensorChange(event);
		}
	}
	
	public List<BioSensor> getBioSensorsList() {
		
		final List<BioSensor> list = null;
		
		//	TODO: get the list of all the supported sensors from Arduino
		
		return list;
	}
	
	public List<BioSensor> getAvailableBioSensors() {
			
		final List<BioSensor> list = null;
			
		//	TODO: get the list of all the supported sensors from Arduino
			
		return list;
	}
}

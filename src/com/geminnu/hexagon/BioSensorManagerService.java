package com.geminnu.hexagon;

import java.util.ArrayList;
import java.util.List;





import java.util.Timer;
import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BioSensorManagerService extends Service {

//	===========================================================================	//
//	Essential parts for the service in order to be able to bind with components	//
//	===========================================================================	//
	
	private final String TAG = "BioSensorManagerService";
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
	    Log.d(TAG, "onDestroy");
	}
	
//	###########################################################################	//
	
	
//	===========================================================================	//
//	Methods that provide the functionality of the service						//
//	===========================================================================	//
	
	private Bluetooth mBluetooth;
	private BluetoothSocket mSocket;
	private Timer mScheduler = new Timer();
	
	public final static int BLUETOOTH = 0;
	public final static int WIFI = 1;
	
	
	private ArduinoReceiver mArduinoReceiver;
	
	ArrayList<BioSensorListenerItem> mRegisterdListeners = new ArrayList<BioSensorListenerItem>();
	
	public void initialise(String adress, int type) {
		
		switch(type) {
			case BLUETOOTH:
				mBluetooth = new Bluetooth(adress); 
				mSocket = mBluetooth.connection();
				mArduinoReceiver = new ArduinoReceiver(mSocket, myMessage);
				mArduinoReceiver.start();
				break;
			case WIFI:
				//	TODO: Write the wifi case
				break;
		}
	}
	
	public void registerListener(BioSensorEventListener listener, BioSensor sensor, int sampleRate) {
		
		mRegisterdListeners.add(new BioSensorListenerItem(listener, sensor, sampleRate));
	}
	
	public void unegisterListener(BioSensorEventListener listener, BioSensor sensor, int sampleRate) {
		for(int i = 0; i < mRegisterdListeners.size(); i++) {
			if(sensor.getName().equals(mRegisterdListeners.get(i).getSensor().getName())) { 
				if(sampleRate == mRegisterdListeners.get(i).getSampleRate()) {
					mRegisterdListeners.remove(i);
					
				}
			}
		}
	}
	
	public void finalise() {

		if(!mRegisterdListeners.isEmpty()) {

			Log.d(TAG, "Size: " + mRegisterdListeners.size());
			for(int i = 0; i < mRegisterdListeners.size(); i++) {
				if(i==0) {
					mScheduler.schedule(new ArduinoTransmitter("Hello Alex1", mSocket), 0, mRegisterdListeners.get(i).getSampleRate());
//					
				Log.d(TAG, "rate: " + mRegisterdListeners.get(i).getSampleRate());
				} else{
					
					mScheduler.scheduleAtFixedRate(new ArduinoTransmitter("Hello Alex2", mSocket), 200,mRegisterdListeners.get(i).getSampleRate());
					Log.d(TAG, "rate: " + mRegisterdListeners.get(i).getSampleRate());
				}
				}
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

	private MessageListener myMessage = new MessageListener() {
		
		@Override
		public void onDataReceived(String data) {
			
			float d = 0;
			if(data.equals("Hello back Alex1")) {
				d = (float) 1.0;
				Log.d(TAG, data);
				BioSensorEvent event = new BioSensorEvent(mRegisterdListeners.get(0).getSensor(),mRegisterdListeners.get(0).getSampleRate(),d);
				mRegisterdListeners.get(0).getListener().onBioSensorChange(event);
			} else {
				d = (float) 0.0;
				Log.d(TAG, data);
				BioSensorEvent event = new BioSensorEvent(mRegisterdListeners.get(1).getSensor(),mRegisterdListeners.get(1).getSampleRate(),d);
				mRegisterdListeners.get(1).getListener().onBioSensorChange(event);
			}
		}
	};
}

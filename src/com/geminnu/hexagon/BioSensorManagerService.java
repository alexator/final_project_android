package com.geminnu.hexagon;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import com.geminnu.hexagon.ArduinoService.ArduinoServiceBinder;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BioSensorManagerService extends Service {

//######################################################################################################################
//	Essential parts for the service in order to be able to be binded with components (Main Activity and ArduinoService)	
//######################################################################################################################
	
	private final String TAG = "BioSensorManagerService";
	private final IBinder mBinder = new BioSensorServiceBinder();
	boolean mBoundArduinoService = false;
	private MessageSender arduinoSender;
	
	public class BioSensorServiceBinder extends Binder {
			
		public BioSensorManagerService getServerInstance() {
			return BioSensorManagerService.this;
	    }
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "Hello from onCreate");
		if(!mBoundArduinoService) {
			Intent arduinoService = new Intent(this, ArduinoService.class);
			bindService(arduinoService, mConnectionWithArduinoService, Context.BIND_AUTO_CREATE);
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		 Log.d(TAG, "onUnbind");
		 if (mBoundArduinoService) {
//            unbindService(mConnectionWithArduinoService);
//            Intent arduinoService = new Intent(this, ArduinoService.class);
//            stopService(arduinoService);
//            mBoundArduinoService = false;
//            mScheduler.cancel();
//			 for(int i = 0; i < mArduinoTasks.size(); i++) {
//	            	mArduinoTasks.get(i).cancel();
//	            	mArduinoTasks.remove(i);
//	            	mRegisterdListeners.remove(i);
//	            }
	     }
//		return super.onUnbind(intent);
		 return true;
	}
	
	@Override
	public void onDestroy() {
	    Log.d(TAG, "onDestroy");
	    super.onDestroy();
	    
	    if (mBoundArduinoService) {
	    	unbindService(mConnectionWithArduinoService);
            Intent arduinoService = new Intent(this, ArduinoService.class);
            for(int i = 0; i < mArduinoTasks.size(); i++) {
            	mArduinoTasks.get(i).cancel();
            	mArduinoTasks.remove(i);
            	mRegisterdListeners.remove(i);
            }
            mScheduler.cancel();
            myAction = null;
            stopService(arduinoService);
        }
	}
	
	private ServiceConnection mConnectionWithArduinoService  = new ServiceConnection() {
	
		@Override
		public void onServiceDisconnected(ComponentName name) {
		
			Log.d(TAG, "Service is disconnected");
			mBoundArduinoService = false;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			
			ArduinoServiceBinder binder = (ArduinoServiceBinder) service;
			mArduinoService = binder.getServerInstance();
			mBoundArduinoService = true;
			Log.d(TAG, "Service is connected");
				
			if(mArduinoService != null && mBoundArduinoService) {
				arduinoSender = mArduinoService.getMessageSenderListener();
				mArduinoService.sendActionListener(myAction);
				schedule(arduinoSender);
			}
		}
	};
	
//####################################################################################################################
	
	
//####################################################################################################################
//	Methods that provide the functionality of the service						
//####################################################################################################################
	
	public final static int GLUCOMETER = 0;
	public final static int BODYTEMP = 1;
	public final static int BLOODPRESURE = 2;
	public final static int PULSE_OXYGEN_BLOOD = 3;
	public final static int AIRFLOW = 4;
	public final static int GALVANICSKIN = 5;
	public final static int ECG = 6;
	public final static int EMG = 7;
	public final static int PATIENTPOSITION = 8;
	
	private ArduinoService mArduinoService;
	private Timer mScheduler;
	
	ArrayList<BioSensorListenerItem> mRegisterdListeners = new ArrayList<BioSensorListenerItem>();
	ArrayList<ArduinoTask> mArduinoTasks = new ArrayList<ArduinoTask>();
	
	// Register a new BioSensor Listener to communicate with the UI  
	public void registerListener(BioSensorEventListener listener, BioSensor sensor, int sampleRate) {
		
		mRegisterdListeners.add(new BioSensorListenerItem(listener, sensor, sampleRate));
	}
	
	// Unregister the BioSensor listener
	public void unegisterListener(BioSensorEventListener listener, BioSensor sensor, int sampleRate) {
		Log.d(TAG, "unregister");
		for(int i = 0; i < mRegisterdListeners.size(); i++) {
			if(sensor.getName().equals(mRegisterdListeners.get(i).getSensor().getName())) { 
				if(sampleRate == mRegisterdListeners.get(i).getSampleRate()) {
					mRegisterdListeners.remove(i);
					mArduinoTasks.get(i).cancel();
					mArduinoTasks.remove(i);
					
				}
			}
		}
		mScheduler.cancel();
	}
	
	// Schedule an ArduinoTask for every registered listener (sensor) 
	public void schedule(MessageSender sender) {
		mScheduler = new Timer();
		if(!mRegisterdListeners.isEmpty()) {
			for(int i = 0; i < mRegisterdListeners.size(); i++) {
				mArduinoTasks.add(new ArduinoTask(sender,i));
				Log.d(TAG, "rate: " + mRegisterdListeners.get(i).getSampleRate());	
			}
			for(int i = 0; i < mArduinoTasks.size(); i++) {
//				mScheduler.scheduleAtFixedRate(new ArduinoTask(sender,i), 150 * i, mRegisterdListeners.get(i).getSampleRate());
				mScheduler.scheduleAtFixedRate(mArduinoTasks.get(i), 1000 * i, mRegisterdListeners.get(i).getSampleRate());
			}
		}
	}
	
	public void rescedule() {
		Log.d(TAG, "onrescedule");
		if(mBoundArduinoService) {
			mScheduler.cancel();
			schedule(arduinoSender);
		}
	}
	
	//	Get the a list with all the supported biosensors
	public List<BioSensor> getBioSensorsList() {
		
		final List<BioSensor> list = null;
		
		//	TODO: get the list of all the supported sensors from Arduino
		
		return list;
	}
	
	//	Get the a list with all the available biosensors
	public List<BioSensor> getAvailableBioSensors() {
			
		final List<BioSensor> list = null;
			
		//	TODO: get the list of all the supported sensors from Arduino
			
		return list;
	}
	
	// Listener that waits for messages from Arduino related to the registered biosensors
	private CoordinatorActionListener myAction = new CoordinatorActionListener() {
			
		@Override
		public void onNewAction(ArduinoMessage message) {
			
			for(int i = 0; i < mRegisterdListeners.size(); i++) {
				if (message.getSensor().equals(mRegisterdListeners.get(i).getSensor().getName())) {
					BioSensorEvent event = new BioSensorEvent(mRegisterdListeners.get(i).getSensor(),1000 + i,message.getValue());
					mRegisterdListeners.get(i).getListener().onBioSensorChange(event);
				}
			}
		}
	};
}

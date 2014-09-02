package com.geminnu.hexagon;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

import com.geminnu.hexagon.ArduinoService.ArduinoServiceBinder;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ArduinoStatusManager extends Service{
	
	private final String TAG = "ArduinoStatusManager";
	
	public final static int GETBATT = 9;
	public final static int GETAVAIL = 10;
	public final static int GETCONNE = 11;
	
	private boolean mFirstTime = false;
	
	private final IBinder mBinder = new ArduinoStatusManagerBinder();
	boolean mBoundArduinoService = false;
	private MessageSender arduinoSender; 
	private ArduinoService mArduinoService;
	private ArduinoStatusEventListener mRegisterStatusListener;
	ArrayList<ArduinoTask> mArduinoTasks = new ArrayList<ArduinoTask>();
	ArrayList<BioSensor> mAvailableSensors = new ArrayList<BioSensor>();
	private Timer mScheduler;
	private Random r = new Random(); 
	
	public class ArduinoStatusManagerBinder extends Binder {
		
		public ArduinoStatusManager getServerInstance() {
			return ArduinoStatusManager.this;
		}
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		if(!mBoundArduinoService) {
			Intent arduinoService = new Intent(this, ArduinoService.class);
			bindService(arduinoService, mConnectionWithArduinoService, Context.BIND_AUTO_CREATE);
			mFirstTime = true;
		}
	}
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;		
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		 Log.d(TAG, "onUnbind");
		 mFirstTime = false;
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
            	
            }
            mRegisterStatusListener = null;
            mScheduler.cancel();
            myStatusAction = null;
            if(isMyServiceRunning(ArduinoService.class)){
            	stopService(arduinoService);
            }
        }
	}
	
	private ServiceConnection mConnectionWithArduinoService  = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
			
				Log.d(TAG, "ArduinoService is disconnected");
				mBoundArduinoService = false;
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				
				ArduinoServiceBinder binder = (ArduinoServiceBinder) service;
				mArduinoService = binder.getServerInstance();
				mBoundArduinoService = true;
				Log.d(TAG, "Service is connected with ArduinoService");
					
				if(mArduinoService != null && mBoundArduinoService) {
					arduinoSender = mArduinoService.getMessageSenderListener();
					mArduinoService.sendActionListenerForStatus(myStatusAction);
					if(mFirstTime) {
						schedule(arduinoSender);
					}
				}
			}
		};
	
	
	
	public void registerListener(ArduinoStatusEventListener listener) {
		
		this.mRegisterStatusListener = listener;
	}
	
	public void unregisterListener(ArduinoStatusEventListener listener) {
		
		mRegisterStatusListener = null;
	}
	
	// Schedule an ArduinoTask for every registered listener (status) 
	public void schedule(MessageSender sender) {
		
		mScheduler = new Timer();
		
		if(mRegisterStatusListener != null) {
			Log.d(TAG, "Hello from status sceduler");
			mArduinoTasks.add(new ArduinoTask(sender, GETBATT, mArduinoService.getContainer()));
			mArduinoTasks.add(new ArduinoTask(sender, GETAVAIL, mArduinoService.getContainer()));
			mArduinoTasks.add(new ArduinoTask(sender, GETCONNE, mArduinoService.getContainer()));

			for(int i = 0; i < mArduinoTasks.size(); i++) {
				mScheduler.scheduleAtFixedRate(mArduinoTasks.get(i), (r.nextInt(1000 - 500 + 1) + 1000) * i, 120000);
			}
		}
	}
	
	//	Get the a list with all the available biosensors
	public ArrayList<BioSensor> getAvailableBioSensors() {
			
		return mAvailableSensors;
	}
	
	private void setAvailableBioSensors(int value) {
		String tempval = String.valueOf(value);
		String tempvalarr[] = new String[9];
		if(value >= 100000000) {
			tempvalarr[0] = "1";
			for(int i= 1; i < tempval.length(); i++) {
				tempvalarr[i] = String.valueOf(tempval.charAt(i));
			}
		} else if(value < 100000000) {
			tempvalarr[0] = "0";
			for(int i= 0; i < tempval.length(); i++) {
				tempvalarr[i+1] = String.valueOf(tempval.charAt(i));
			}
		}
		int[] values = new int[9];
		
		for(int i = 0; i < tempvalarr.length; i++) {
			values[i] = Integer.valueOf(tempvalarr[i]);
		}
		
		if(values[0] == 1) {
			mAvailableSensors.add(new BioSensor(BioSensorManagerService.GLUCOMETER, BioSensorManagerService.MEDICAL, BioSensorManagerService.DIGITAL));
		}
		if(values[1] == 1) {
			mAvailableSensors.add(new BioSensor(BioSensorManagerService.BODYTEMP, BioSensorManagerService.MEDICAL, BioSensorManagerService.ANALOG));
		}
		if(values[2] == 1) {
			mAvailableSensors.add(new BioSensor(BioSensorManagerService.BLOODPRESURE, BioSensorManagerService.MEDICAL, BioSensorManagerService.DIGITAL));
		}
		if(values[3] == 1) {
			mAvailableSensors.add(new BioSensor(BioSensorManagerService.PULSE_OXYGEN_BLOOD, BioSensorManagerService.MEDICAL, BioSensorManagerService.DIGITAL));
		}
		if(values[4] == 1) {
			mAvailableSensors.add(new BioSensor(BioSensorManagerService.AIRFLOW, BioSensorManagerService.MEDICAL, BioSensorManagerService.DIGITAL));
		}
		if(values[5] == 1) {
			mAvailableSensors.add(new BioSensor(BioSensorManagerService.GALVANICSKIN, BioSensorManagerService.MEDICAL, BioSensorManagerService.ANALOG));
		}
		if(values[6] == 1) {
			mAvailableSensors.add(new BioSensor(BioSensorManagerService.ECG, BioSensorManagerService.MEDICAL, BioSensorManagerService.ANALOG));
		}
		if(values[7] == 1) {
			mAvailableSensors.add(new BioSensor(BioSensorManagerService.EMG, BioSensorManagerService.MEDICAL, BioSensorManagerService.DIGITAL));
		}
		if(values[8] == 1) {
			mAvailableSensors.add(new BioSensor(BioSensorManagerService.PATIENTPOSITION, BioSensorManagerService.MEDICAL, BioSensorManagerService.DIGITAL));
		}
		
		
	}
	
	
	private CoordinatorActionListener myStatusAction = new CoordinatorActionListener() {
		
		@Override
		public void onNewAction(ArduinoMessage message) {
			if(mRegisterStatusListener != null) {
				if(message.getType() != 111) {
					if(message.getType() == 10) {
						Log.d(TAG, "Hello1");
						Log.d(TAG, "Hello1: " + message.getType() + " " + (int)message.getValue());
						
						setAvailableBioSensors((int)message.getValue());
						Log.d(TAG, "size of list: "+ mAvailableSensors.size());
						Log.d(TAG, "Hello2");
						mRegisterStatusListener.onStatusChangeAvailableSensors(getAvailableBioSensors());
						mAvailableSensors.clear();
					} else {
						Log.d(TAG, "hello from status coordinator");
						ArduinoStatusEvent event = new ArduinoStatusEvent(new ArduinoStatus(message.getType(), (int)message.getValue()));
						mRegisterStatusListener.onStatusChange(event);
					}
				} else { 
					ArduinoStatusEvent event = new ArduinoStatusEvent(new ArduinoStatus(GETCONNE, 1));
					mRegisterStatusListener.onStatusChange(event);
					mScheduler.cancel();
				}
			}
		}
	};

	private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

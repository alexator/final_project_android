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

public class BioSensorManagerService extends Service {

//######################################################################################################################
//	Essential parts for the service in order to be able to be binded with components (Main Activity and ArduinoService)	
//######################################################################################################################
	
	private final String TAG = "BioSensorManagerService";
	private final IBinder mBinder = new BioSensorServiceBinder();
	boolean mBoundArduinoService = false;
	private MessageSender arduinoSender;
	private boolean mFirstTime = false;
	public final static int GLUCOMETER = 0;
	public final static int BODYTEMP = 1;
	public final static int BLOODPRESURE = 2;
	public final static int PULSE_OXYGEN_BLOOD = 3;
	public final static int AIRFLOW = 4;
	public final static int GALVANICSKIN = 5;
	public final static int ECG = 6;
	public final static int EMG = 7;
	public final static int PATIENTPOSITION = 8;
	
	public final static int MEDICAL = 0;
	public final static int GENERIC = 1;
	public final static int ANALOG = 0;
	public final static int DIGITAL = 1;
	
	public final static String FIRST_NAME = "FirstName";
	public final static String LAST_NAME = "LastName";
	public final static String SEX = "Sex";
	public final static String AGE = "Age";
	public final static String USER_ID = "UserId";
	public final static String ALERTLIST = "AlerList";
	
	private String mFirstName;
	private String mLastName;
	private String mSex;
	private int mAge;
	private int mUserId;
	private Profile mProfile;
	
	private MySQLiteHelper db;
	
	private ArduinoService mArduinoService;
	private Timer mScheduler;
	private Random r = new Random();
	
	private ArrayList<BioSensorListenerItem> mRegisterdListeners = new ArrayList<BioSensorListenerItem>();
	private ArrayList<ArduinoTask> mArduinoTasks = new ArrayList<ArduinoTask>();
	private ArrayList<Alert> alertList = new ArrayList<Alert>();
	
	public class BioSensorServiceBinder extends Binder {
			
		public BioSensorManagerService getServerInstance() {
			return BioSensorManagerService.this;
	    }
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		if(!mBoundArduinoService) {
			
			db = new MySQLiteHelper(getApplicationContext());
			
			Intent arduinoService = new Intent(this, ArduinoService.class);
			bindService(arduinoService, mConnectionWithArduinoService, Context.BIND_AUTO_CREATE);
			mFirstTime = true;
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		mFirstName = intent.getStringExtra(FIRST_NAME);
		mLastName = intent.getStringExtra(LAST_NAME);
		mSex = intent.getStringExtra(SEX);
		mAge = intent.getIntExtra(AGE, 0);
		mUserId = intent.getIntExtra(USER_ID, 1);
		alertList = intent.getParcelableArrayListExtra(ALERTLIST);
		
		
		if(mFirstTime) {
			mProfile = new Profile(mFirstName, mLastName, mSex, mAge, mUserId);
			db.addProfile(mProfile);
			Log.d(TAG, db.getProfile(1).getFirstName() + " " +  db.getProfile(1).getId());
		}
		
		return START_REDELIVER_INTENT;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		 Log.d(TAG, "onUnbind");
		 if (mBoundArduinoService) {
			 mFirstTime = false;
		 }
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
            mScheduler.cancel();
            myAction = null;
            if(isMyServiceRunning(ArduinoService.class)) {
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
				mArduinoService.sendActionListenerForSensors(myAction);
				if(mFirstTime) {
					schedule(arduinoSender);
				}
			}
		}
	};
	
//####################################################################################################################
	
	
//####################################################################################################################
//	Methods that provide the functionality of the service						
//####################################################################################################################
	
	
	// Register a new BioSensor Listener to communicate with the UI  
	public void registerListener(BioSensorEventListener listener, BioSensor sensor, int sampleRate) {
		
		mRegisterdListeners.add(new BioSensorListenerItem(listener, sensor, sampleRate));
	}
	
	// Unregister the BioSensor listener
	public void unegisterListener(BioSensorEventListener listener, BioSensor sensor, int sampleRate) {
		Log.d(TAG, "unregister");
		for(int i = 0; i < mRegisterdListeners.size(); i++) {
			if(sensor.getSensorId() == mRegisterdListeners.get(i).getSensor().getSensorId()) { 
				if(sampleRate == mRegisterdListeners.get(i).getSampleRate()) {
					mRegisterdListeners.remove(i);
//					mArduinoTasks.get(i).cancel();
//					mArduinoTasks.remove(i);
					
				}
			}
		}
//		mScheduler.cancel();
	}
	
	// Schedule an ArduinoTask for every registered listener (sensor) 
	public void schedule(MessageSender sender) {
		mScheduler = new Timer();
		if(!mRegisterdListeners.isEmpty()) {
			for(int i = 0; i < mRegisterdListeners.size(); i++) {
				mArduinoTasks.add(new ArduinoTask(sender,mRegisterdListeners.get(i).getSensor().getSensorId(), mArduinoService.getContainer()));
				Log.d(TAG, "rate: " + mRegisterdListeners.get(i).getSampleRate());	
			}
			for(int i = 0; i < mArduinoTasks.size(); i++) {
				mScheduler.scheduleAtFixedRate(mArduinoTasks.get(i), (r.nextInt(2000 - 1010 + 1) + 1000) * (i + 1), mRegisterdListeners.get(i).getSampleRate());
			}
		}
	}
	
//	public void rescedule() {
//		Log.d(TAG, "onresceduleSensor");
//		if(mBoundArduinoService) {
//			mScheduler.cancel();
//			schedule(arduinoSender);
//		}
//	}
	
	//	Get the a list with all the supported biosensors
	public ArrayList<BioSensor> getBioSensorsList() {
		ArrayList<BioSensor> list = new ArrayList<BioSensor>();
		
		list.add(new BioSensor(GLUCOMETER, MEDICAL, DIGITAL));
		list.add(new BioSensor(BODYTEMP, MEDICAL, ANALOG));
		list.add(new BioSensor(BLOODPRESURE, MEDICAL, DIGITAL));
		list.add(new BioSensor(PULSE_OXYGEN_BLOOD, MEDICAL, DIGITAL));
		list.add(new BioSensor(AIRFLOW, MEDICAL, DIGITAL));
		list.add(new BioSensor(GALVANICSKIN, MEDICAL, ANALOG));
		list.add(new BioSensor(ECG, MEDICAL, ANALOG));
		list.add(new BioSensor(EMG, MEDICAL, DIGITAL));
		list.add(new BioSensor(PATIENTPOSITION, MEDICAL, DIGITAL));
		
		return list;
	}
	
	// Listener that waits for messages from Arduino related to the registered biosensors
	private CoordinatorActionListener myAction = new CoordinatorActionListener() {
			
		@Override
		public void onNewAction(ArduinoMessage message) {
			
			if(message.getType() != 111){
				if(!mRegisterdListeners.isEmpty()) {
				for(int i = 0; i < mRegisterdListeners.size(); i++) {
					if (message.getType() == mRegisterdListeners.get(i).getSensor().getSensorId()) {
						for(int j = 0; j < alertList.size(); j++) {
						if(alertList.get(j).getSensorId() == message.getType()) {
						db.addReading(new Reading(message.getValue(), mRegisterdListeners.get(i).getSensor().getSensorId(), mProfile.getUserId(), alertList.get(j).check(message.getValue())));
						Log.d(TAG, db.getUserLatestReading(mProfile.getUserId()).getTime());
						BioSensorEvent event = new BioSensorEvent(mRegisterdListeners.get(i).getSensor(), message.getValue());
						mRegisterdListeners.get(i).getListener().onBioSensorChange(event);
					}
					}
					}
				}
				} else {
					Log.d(TAG, "Hello from storing");
					for(int i = 0; i < alertList.size(); i++) {
						if(alertList.get(i).getSensorId() == message.getType()) {
							db.addReading(new Reading(message.getValue(), message.getType(), mProfile.getUserId(), alertList.get(i).check(message.getValue())));
				}
					}
				}
			} else {
				mScheduler.cancel();
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

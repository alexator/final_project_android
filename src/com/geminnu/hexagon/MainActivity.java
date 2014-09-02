package com.geminnu.hexagon;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.ls.LSInput;

import com.geminnu.hexagon.ArduinoStatusManager.ArduinoStatusManagerBinder;
import com.geminnu.hexagon.BioSensorManagerService.BioSensorServiceBinder;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

	private final String TAG = "Main Activity";
	private BioSensor mSensor1;
	private BioSensor mSensor2;
	private BioSensorManagerService mSensorManager;
	private ArduinoStatusManager mStatusManager;
	private boolean mBoundArduinoSatusManager = false;
	private boolean mBoundBioSensorManager = false;
	public TextView data1;
	public TextView data2;
	public TextView greeting;
	public TextView battery;
	public TextView connectivity;
	public TextView avail;
	public Button test;
	private StringBuilder sens = new StringBuilder();
	private ArrayList<Alert> alertList = new ArrayList<Alert>();
	private ArrayList<BioSensor> mAvailableSens = new ArrayList<BioSensor>();
	private MySQLiteHelper db;
	
//################################################################################################################
//	Handle life cycle of the application
//################################################################################################################
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        greeting = (TextView) findViewById(R.id.greeting);
        data1 = (TextView) findViewById(R.id.messages1);
        data2 = (TextView) findViewById(R.id.messages2);
        test = (Button)	findViewById(R.id.tester);
        battery = (TextView) findViewById(R.id.battery);
        connectivity = (TextView) findViewById(R.id.connectivity);
        avail = (TextView) findViewById(R.id.available);
        
        
        alertList.add(new Alert(BioSensorManagerService.ECG, 2.9, 0.));
        alertList.add(new Alert(BioSensorManagerService.EMG, 500.0, 90.0));
        
        Log.d(TAG, "Hello One");
        if(!isMyServiceRunning(ArduinoService.class)) {
        	Log.d(TAG, "Hello one");
	        Intent arduino = new Intent(this, ArduinoService.class);
	        arduino.putExtra(ArduinoService.ADDRESS, "00:07:80:6D:4C:F2");
	        arduino.putExtra(ArduinoService.COMMUNICATION_TYPE, ArduinoService.BLUETOOTH);
//	        arduino.putExtra(ArduinoService.DATA_CONTAINER, ArduinoService.XML);
	        arduino.putExtra(ArduinoService.DATA_CONTAINER, ArduinoService.JSON);
	        startService(arduino);
        }
        Log.d(TAG, "Hello Two");
        if(!isMyServiceRunning(ArduinoStatusManager.class)) {
        	Log.d(TAG, "Hello two");
        	Intent statusService  = new Intent(this, ArduinoStatusManager.class);
        	startService(statusService);
        }
        Log.d(TAG, "Hello Three");
        if(!isMyServiceRunning(BioSensorManagerService.class)) {
        	Log.d(TAG, "Hello three");
	        Intent sensorService = new Intent(this, BioSensorManagerService.class);
	        sensorService.putExtra(BioSensorManagerService.FIRST_NAME, "Alex");
	        sensorService.putExtra(BioSensorManagerService.LAST_NAME, "Georgantas");
	        sensorService.putExtra(BioSensorManagerService.SEX, "Male");
	        sensorService.putExtra(BioSensorManagerService.USER_ID, 1);
	        sensorService.putExtra(BioSensorManagerService.AGE, 28);
	        sensorService.putParcelableArrayListExtra(BioSensorManagerService.ALERTLIST, alertList);
	        startService(sensorService);
        }
        
        
        mSensor1 = new BioSensor(BioSensorManagerService.ECG, BioSensorManagerService.MEDICAL, BioSensorManagerService.ANALOG);
        mSensor2 = new BioSensor(BioSensorManagerService.EMG, BioSensorManagerService.MEDICAL, BioSensorManagerService.DIGITAL);
        db = new MySQLiteHelper(getApplicationContext());
        for(int i = 0; i< db.getUserBadReading(1).size(); i++) {
        Log.d(TAG, "size matters: " + db.getUserBadReading(1).get(i).getValue() + "  " + db.getUserBadReading(1).get(i).getType());
        }
        Log.d(TAG, "onCreate");
    }
	
	@Override
	public void onRestart() {
		super.onRestart();
		Log.d(TAG, "onRestart");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
		
		if (isMyServiceRunning(ArduinoStatusManager.class)) {
			Log.d(TAG, "onStart1");
			Log.d(TAG, "Im in onStartStatus");
			Intent statusService = new Intent(this, ArduinoStatusManager.class);
	        bindService(statusService, mConnectionArduinoStatusManager, Context.BIND_AUTO_CREATE);
	        Log.d(TAG, "onStar1");
        }
		if (isMyServiceRunning(BioSensorManagerService.class)) {
			Log.d(TAG, "onStart2");
			Log.d(TAG, "Im in onStartBioSen");
			Intent sensorService = new Intent(this, BioSensorManagerService.class);
	        bindService(sensorService, mConnectionBioSensorManager, Context.BIND_AUTO_CREATE);
	        Log.d(TAG, "onStar2");
        }
	}
    
	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		Log.d(TAG, "onStop");
		
		if (mBoundArduinoSatusManager && isMyServiceRunning(ArduinoStatusManager.class)) {
			mStatusManager.unregisterListener(myStatusListener);
        }
		if(isMyServiceRunning(ArduinoStatusManager.class)) {
			unbindService(mConnectionArduinoStatusManager);
		}
		
		if (mBoundBioSensorManager && isMyServiceRunning(BioSensorManagerService.class)) {
			mSensorManager.unegisterListener(mySensorListener, mSensor2, 20000);
			mSensorManager.unegisterListener(mySensorListener, mSensor1, 5000);
        }
		
		if(isMyServiceRunning(BioSensorManagerService.class)) {
			unbindService(mConnectionBioSensorManager);
		}
	}
    
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		if(isFinishing()) {
			if(isMyServiceRunning(ArduinoStatusManager.class)) {
				mConnectionArduinoStatusManager = null;
			}
			
			if(isMyServiceRunning(BioSensorManagerService.class)) {
				mConnectionBioSensorManager = null;
			}
			
			if(isMyServiceRunning(ArduinoService.class)) {
				Intent arduino = new Intent(this, ArduinoService.class);
				Intent satusService = new Intent(this, ArduinoStatusManager.class);
				Intent sensorService = new Intent(this, BioSensorManagerService.class);
				stopService(arduino);
				stopService(satusService);
				stopService(sensorService);
			}
		}
	}
	
//################################################################################################################


//################################################################################################################
//	Handle connections with services
//################################################################################################################	
	
	private ServiceConnection mConnectionBioSensorManager  = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "Service is disconnected");
			mBoundBioSensorManager = false;
			mSensorManager = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			
			BioSensorServiceBinder binderSen = (BioSensorServiceBinder) service;
			mSensorManager = binderSen.getServerInstance();
			mBoundBioSensorManager = true;
			Log.d(TAG, "Activity is connected with SensorService");
			
			if(mSensorManager != null && mBoundBioSensorManager) {
				Log.d(TAG, "Service is connected2");
				mSensorManager.registerListener(mySensorListener, mSensor1, 5000);
				mSensorManager.registerListener(mySensorListener, mSensor2, 20000);
//				mSensorManager.rescedule();
				Log.d(TAG, "Service is connected2");
			}
		}
		
	};
	
	private ServiceConnection mConnectionArduinoStatusManager = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mBoundArduinoSatusManager = false;
			mStatusManager = null;
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			
			ArduinoStatusManagerBinder binderStat = (ArduinoStatusManagerBinder) service;
			mStatusManager = binderStat.getServerInstance();
			mBoundArduinoSatusManager = true;
			Log.d(TAG, "Activity is connected with StatusService");
			
			if(mStatusManager != null && mBoundArduinoSatusManager) {
				Log.d(TAG, "Service is connected3");
				mStatusManager.registerListener(myStatusListener);
//				mStatusManager.rescedule();
				Log.d(TAG, "Service is connected3");
			}
			
		}
	};

//################################################################################################################
	
	
//################################################################################################################
//	Implement event listeners
//################################################################################################################
	
    private BioSensorEventListener mySensorListener = new BioSensorEventListener() {
		
		@Override
		public void onBioSensorChange(BioSensorEvent event) {
			final float value = event.getValue();
			final BioSensor sen = event.getSensor();
			Log.d(TAG, "value: " + value + ", sensor: " + sen.getName());
			//data.setText("hello");
		
			runOnUiThread(new Runnable() {
			    public void run() {
			    	if(sen.getName().equals("ECG")) {
			    	data1.setText("value: " + value + ", sensor: " + sen.getName());
			    	} else {
			    		data2.setText("value: " + value + ", sensor: " + sen.getName());
			    	}
			    }
			});
			
		}
	};
	
	private ArduinoStatusEventListener myStatusListener = new ArduinoStatusEventListener() {
		
		@Override
		public void onStatusChange(ArduinoStatusEvent event) {
			
			final int type = event.getStatus().getType();
			final int value = event.getStatus().getValue();
			Log.d(TAG, "Your type was: " + type);
			Log.d(TAG, "Your value was: " + value);
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					if(type == 9) {
						battery.setText("Your battery level is: " + value);
					}else if(type == 11) {
						if(value == 0) {
						connectivity.setText("Connected");
						} else {
							connectivity.setText("Disconnected");
						}
					}else if(type == 10) {
						avail.setText("Your available sensors are: " + value);
					}
				}
			});
			
		}

		@Override
		public void onStatusChangeAvailableSensors(ArrayList<BioSensor> list) {
			// TODO Auto-generated method stub
//			final StringBuilder sens = new StringBuilder();
			if(!list.isEmpty()){
			Log.d(TAG, "onavaila");
				mAvailableSens.clear();
				for(int i = 0; i < list.size(); i++) {
//				sens.append(list.get(i).getName());
//				sens.append(", ");
					mAvailableSens.add(list.get(i));
			}
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					avail.setText("Available sensors: " + mAvailableSens.size());
					
				}
			});
			}
			
		}
	};

//################################################################################################################	
	
	
//################################################################################################################
//	Create Options menu
//################################################################################################################
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
//################################################################################################################    
    
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

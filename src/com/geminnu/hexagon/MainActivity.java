package com.geminnu.hexagon;

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
	private boolean mBoundBioSensorManager = false;
	public TextView data1;
	public TextView data2;
	public TextView greeting;
	public Button test;
	
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
        
        Log.d(TAG, "Hello One");
        if(!isMyServiceRunning(ArduinoService.class)) {
        	Log.d(TAG, "Hello One");
	        Intent arduino = new Intent(this, ArduinoService.class);
	        arduino.putExtra(ArduinoService.ADDRESS, "00:07:80:6D:4C:F2");
	        arduino.putExtra(ArduinoService.COMMUNICATION_TYPE, ArduinoService.BLUETOOTH);
//	        arduino.putExtra(ArduinoService.DATA_CONTAINER, ArduinoService.XML);
	        arduino.putExtra(ArduinoService.DATA_CONTAINER, ArduinoService.JSON);
	        startService(arduino);
        }
        Log.d(TAG, "Hello One");
        if(!isMyServiceRunning(BioSensorManagerService.class)) {
        	Log.d(TAG, "Hello Two");
	        Intent sensorService = new Intent(this, BioSensorManagerService.class);
	        startService(sensorService);
//	        bindService(sensorService, mConnectionBioSensorManager, Context.BIND_AUTO_CREATE);
        }
        
        mSensor1 = new BioSensor("ECG", 1, 1);
        mSensor2 = new BioSensor("EMG", 1, 2);
        
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
		
//		if (!mBoundBioSensorManager && isMyServiceRunning(BioSensorManagerService.class)) {
		if (isMyServiceRunning(BioSensorManagerService.class)) {
			Log.d(TAG, "onStart2");
			Log.d(TAG, "Im in onStartBioSen");
			Intent sensorService = new Intent(this, BioSensorManagerService.class);
	        bindService(sensorService, mConnectionBioSensorManager, Context.BIND_AUTO_CREATE);
	        Log.d(TAG, "onStar3");
        }
		
//		if(!isMyServiceRunning(ArduinoService.class)) {
//			Log.d(TAG, "Im in onStartArdSer");
//	        Intent arduino = new Intent(this, ArduinoService.class);
//	        arduino.putExtra(ArduinoService.ADDRESS, "00:07:80:6D:4C:F2");
//	        arduino.putExtra(ArduinoService.COMMUNICATION_TYPE, ArduinoService.BLUETOOTH);
//	        arduino.putExtra(ArduinoService.DATA_CONTAINER, ArduinoService.XML);
//	        startService(arduino);
//        }
	}
    
	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
		if (mBoundBioSensorManager && isMyServiceRunning(BioSensorManagerService.class)) {
			Log.d(TAG, "onResume2");
//			mSensorManager.registerListener(mySensorListener, mSensor1, 5000);
//			mSensorManager.registerListener(mySensorListener, mSensor2, 20000);
//			mSensorManager.rescedule();
			Log.d(TAG, "onResume3");
        }
		Log.d(TAG, "onResum4");
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
		
		if (mBoundBioSensorManager && isMyServiceRunning(BioSensorManagerService.class)) {
			mSensorManager.unegisterListener(mySensorListener, mSensor2, 20000);
			mSensorManager.unegisterListener(mySensorListener, mSensor1, 5000);
        }
		
		if(isMyServiceRunning(BioSensorManagerService.class)) {
			unbindService(mConnectionBioSensorManager);
//			mConnectionBioSensorManager = null;
//			mSensorManager = null;
		}
	}
    
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		if(isFinishing()) {
			if(isMyServiceRunning(BioSensorManagerService.class)) {
//				unbindService(mConnectionBioSensorManager);
				mConnectionBioSensorManager = null;
			}
			
			if(isMyServiceRunning(ArduinoService.class)) {
				Intent arduino = new Intent(this, ArduinoService.class);
				Intent sensorService = new Intent(this, BioSensorManagerService.class);
				stopService(arduino);
				stopService(sensorService);
			}
		}
//		mConnectionBioSensorManager = null;
//		if(isMyServiceRunning(BioSensorManagerService.class)) {
//			unbindService(mConnectionBioSensorManager);
//			mConnectionBioSensorManager = null;
//			mSensorManager = null;
//		}
//		mConnectionBioSensorManager = null;
//		mSensorManager = null;
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
			
			BioSensorServiceBinder binder = (BioSensorServiceBinder) service;
			mSensorManager = binder.getServerInstance();
			mBoundBioSensorManager = true;
			Log.d(TAG, "Service is connected");
			
			if(mSensorManager != null && mBoundBioSensorManager) {
				Log.d(TAG, "Service is connected2");
				mSensorManager.registerListener(mySensorListener, mSensor1, 5000);
				mSensorManager.registerListener(mySensorListener, mSensor2, 20000);
				mSensorManager.rescedule();
				Log.d(TAG, "Service is connected2");
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
			final long time = event.getTimestamp();
			final BioSensor sen = event.getSensor();
			Log.d(TAG, "value: " + value + ", sensor: " + sen.getName() + ", time: " + time);
			//data.setText("hello");
		
			runOnUiThread(new Runnable() {
			    public void run() {
			    	if(sen.getName().equals("ECG")) {
			    	data1.setText("value: " + value + ", sensor: " + sen.getName() + ", time: " + time);
			    	} else {
			    		data2.setText("value: " + value + ", sensor: " + sen.getName() + ", time: " + time);
			    	}
			    }
			});
			
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
    
    @Override
    public Object onRetainNonConfigurationInstance() {
    	return(mConnectionBioSensorManager);
    }

}

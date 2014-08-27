package com.geminnu.hexagon;

import com.geminnu.hexagon.BioSensorManagerService.BioSensorServiceBinder;
import android.app.Activity;
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
        
        Intent arduino = new Intent(this, ArduinoService.class);
        arduino.putExtra(ArduinoService.ADDRESS, "00:07:80:6D:4C:F2");
        arduino.putExtra(ArduinoService.COMMUNICATION_TYPE, ArduinoService.BLUETOOTH);
        arduino.putExtra(ArduinoService.DATA_CONTAINER, ArduinoService.XML);
        startService(arduino);
        
//        Intent sensorService = new Intent(this, BioSensorManagerService.class);
//        startService(sensorService);
//        bindService(sensorService, mConnectionBioSensorManager, Context.BIND_AUTO_CREATE);
        
        mSensor1 = new BioSensor("ECG", 1, 1);
        mSensor2 = new BioSensor("EMG", 1, 2);
        
        Log.d(TAG, "onCreate");
    }
	
	@Override
	public void onRestart() {
		super.onRestart();
		Log.d(TAG, "onRestart");
		
		if (mBoundBioSensorManager) {
			Log.d(TAG, "onRestart2");
			mSensorManager.registerListener(mySensorListener, mSensor1, 5000);
			mSensorManager.registerListener(mySensorListener, mSensor2, 20000);
			mSensorManager.rescedule();
			Log.d(TAG, "onRestart3");
        }
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
		
		if (!mBoundBioSensorManager) {
			Log.d(TAG, "onStart2");
			Intent sensorService = new Intent(this, BioSensorManagerService.class);
	        bindService(sensorService, mConnectionBioSensorManager, Context.BIND_AUTO_CREATE);
	        Log.d(TAG, "onStar3");
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
		
		if (mBoundBioSensorManager) {
			mSensorManager.unegisterListener(mySensorListener, mSensor2, 20000);
			mSensorManager.unegisterListener(mySensorListener, mSensor1, 5000);
        }
	}
    
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		Intent arduino = new Intent(this, ArduinoService.class);
		Intent sensorService = new Intent(this, BioSensorManagerService.class);
		unbindService(mConnectionBioSensorManager);
		stopService(sensorService);
		stopService(arduino);
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
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			
			BioSensorServiceBinder binder = (BioSensorServiceBinder) service;
			mSensorManager = binder.getServerInstance();
			mBoundBioSensorManager = true;
			Log.d(TAG, "Service is connected");
			
			if(mSensorManager != null && mBoundBioSensorManager) {
				mSensorManager.registerListener(mySensorListener, mSensor1, 5000);
				mSensorManager.registerListener(mySensorListener, mSensor2, 20000);
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

}

package com.geminnu.hexagon;

import com.geminnu.hexagon.BioSensorManagerService.LocalBinder;

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
	BioSensorManagerService mSensorManager;
	boolean mBound = false;
	public TextView data1;
	public TextView data2;
	public TextView greeting;
	public Button test;
	
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
        
        Intent sensorService = new Intent(this, BioSensorManagerService.class);
        bindService(sensorService, mConnection, Context.BIND_AUTO_CREATE);
        
        mSensor1 = new BioSensor("ECG", 1, 1);
        mSensor2 = new BioSensor("EMG", 1, 2);
    }
    
    private ServiceConnection mConnection  = new ServiceConnection() {
		
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "Service is disconnected");
			mBound = false;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			
			LocalBinder binder = (LocalBinder) service;
			mSensorManager = binder.getServerInstance();
			mBound = true;
			Log.d(TAG, "Service is connected");
			
			if(mSensorManager != null && mBound) {
//				mSensorManager.initialise("00:07:80:6D:4C:F2", BioSensorManagerService.BLUETOOTH);
				mSensorManager.registerListener(mySensorListener, mSensor1, 5000);
				mSensorManager.registerListener(mySensorListener, mSensor2, 20000);
//				mSensorManager.finalise();
			}
		}
		
	};

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
	
	public void onStop() {
		super.onStop();
		
		Log.d(TAG, "onStop");
//		mSensorManager.unegisterListener(mySensorListener, mSensor2, 20000);
	}

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
}

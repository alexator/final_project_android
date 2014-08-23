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


public class MainActivity extends Activity {

	private final String TAG = "Main Activity";
	private BioSensor mSensor1;
	private BioSensor mSensor2;
	BioSensorManagerService mSensorManager;
	boolean mBound = false;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Intent intent1 = new Intent(this, BioSensorManagerService.class);
        bindService(intent1, mConnection, Context.BIND_AUTO_CREATE);
        
        mSensor1 = new BioSensor("ECG1", 1, 1);
        mSensor2 = new BioSensor("ECG2", 1, 1);
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
				mSensorManager.initialise("00:07:80:6D:4C:F2", BioSensorManagerService.BLUETOOTH);
				mSensorManager.registerListener(mySensorListener, mSensor1, 10000);
				mSensorManager.registerListener(mySensorListener, mSensor2, 20000);
				mSensorManager.finalise();
			}
		}
		
	};

    private BioSensorEventListener mySensorListener = new BioSensorEventListener() {
		
		@Override
		public void onBioSensorChange(BioSensorEvent event) {
			float value = event.getValue();
			long time = event.getTimestamp();
			BioSensor sen = event.getSensor();
			Log.d(TAG, "value: " + value + ", sensor: " + sen.getName() + ", time: " + time);
			
		}
	};
	
	public void onStop() {
		super.onStop();
		
		Log.d(TAG, "onStop");
		mSensorManager.unegisterListener(mySensorListener, mSensor2, 20000);
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

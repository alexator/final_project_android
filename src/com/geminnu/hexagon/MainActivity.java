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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity {

	private BioSensor mSensor1;
	private BioSensor mSensor2;
	private BioSensor mSensor3;
	BioSensorManagerService mSensorManager;
	boolean mBound = false;
	Button mButton;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mButton = (Button) findViewById(R.id.button1);
        
//      The only reason why this button exists is because the Binder call (to the service) is asynchronous so there is
//      not enough time for the callback to respond during the Activity's lifecycle.
//      TODO: Find a better way for the above problem...maybe a lock that holds the execution of Activity's 
//      lifecycle and notify the procedure to go on only when onConnected is completed   
        
        mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				mSensorManager.registerListener(mySensorListener, mSensor1, 5000);
//				mSensorManager.registerListener(mySensorListener, mSensor2, 6000);
//				mSensorManager.registerListener(mySensorListener, mSensor3, 7000);
			}
		});
        
        
        Intent intent1 = new Intent(this, BioSensorManagerService.class);
        bindService(intent1, mConnection, Context.BIND_AUTO_CREATE);
        
        mSensor1 = new BioSensor("ECG", 1, 1);
        mSensor2 = new BioSensor("EMG", 1, 1);
        mSensor3 = new BioSensor("TMP", 1, 1);
        
//        Log.d("Alex", mSensor.getName());
    }
    
    private ServiceConnection mConnection  = new ServiceConnection() {
		
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d("Service Connection", "Service is disconnected");
			mBound = false;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			
			LocalBinder binder = (LocalBinder) service;
			mSensorManager = binder.getServerInstance();
			mBound = true;
			Log.d("Service Connection", "Service is connected");
			if(mSensorManager != null) {
				mSensorManager.registerListener(mySensorListener, mSensor1, 10000);
				mSensorManager.registerListener(mySensorListener, mSensor1, 20000);
			}
		}
		
	};

    private BioSensorEventListener mySensorListener = new BioSensorEventListener() {
		
		@Override
		public void onBioSensorChange(BioSensorEvent event) {
			float value = event.getValue();
			long time = event.getTimestamp();
			BioSensor sen = event.getSensor();
			Log.d("Listener", "value: " + value + ", sensor: " + sen.getName() + ", time: " + time);
			
		}
	};

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

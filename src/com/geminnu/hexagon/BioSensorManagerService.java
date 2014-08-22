package com.geminnu.hexagon;

import java.util.ArrayList;
import java.util.List;





import java.util.Timer;

import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class BioSensorManagerService extends Service {

//	===========================================================================	//
//	Essential parts for the service in order to be able to bind with components	//
//	===========================================================================	//
	
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
	    Log.d("BioSensorManagerService", "onDestroy");
	}
	
//	###########################################################################	//
	
	
//	===========================================================================	//
//	Methods that provide the functionality of the service						//
//	===========================================================================	//
	
	private BioSensorEventListener mListener;
	private BioSensor mSensor;
	private int sampleRate;
	private Bluetooth bt = new Bluetooth("00:07:80:6D:4C:F2");;
	private BluetoothSocket socket = bt.connection();
	private Handler mHandler;
//	private Arduino ard;
	String message = "Hello Alex";
	byte[] msgBuffer = message.getBytes();
	
	
	private ConnectedThread ct;
	
	ArrayList<BioSensorListenerItem> mListeners = new ArrayList<BioSensorListenerItem>();
	
	public void registerListener(BioSensorEventListener listener, BioSensor sensor, int sampleRate) {
		this.mListener = listener;
		this.mSensor = sensor;
		this.sampleRate = sampleRate;
//		bt = new Bluetooth("00:07:80:6D:4C:F2");
//		socket = bt.connection();
		Timer timer = new Timer();
		ct = new ConnectedThread(socket, myMessage);
		ct.start();
//		ard = new Arduino("test", socket, myMessage);
		timer.scheduleAtFixedRate(new Arduino("test", socket, myMessage), 0, sampleRate);
//		exchange(socket);
		
//		mListeners.add(new BioSensorListenerItem(mListener, mSensor, sampleRate));
//		tester();
		
	}
	
	public void unegisterListener(BioSensorEventListener listener) {
		listener = null;
	}
	
//	public void tester() {
////		long m = sampleRate;
//		float d = (float) 2.34;
//		if(!mListeners.isEmpty()) {
//			for(int i = 0; i < mListeners.size(); i++) {
//				for(int j = 0; j < 10; j++) {
//					
//					BioSensorEvent event = new BioSensorEvent(mListeners.get(i).getSensor(), mListeners.get(i).getSampleRate(), d+i);
//					mListeners.get(i).getListener().onBioSensorChange(event);
//				}
//			}
//		}
//	}
	
	//	TODO: Find a better way to implement the mechanism for the requests.
	public void exchange(BluetoothSocket btsock) {
//		ct = new ConnectedThread(btsock);
//		ct.start();
//		ct.write(msgBuffer);
//		new Timer().scheduleAtFixedRate(task, delay, period);
//		mHandler = new Handler();
//		Runnable mStatusChecker = new Runnable() {
//		    @Override 
//		    public void run() {
//		    	ct.write(msgBuffer);
//		      mHandler.postDelayed(this, sampleRate);
//		    }
//		  };
//		  mStatusChecker.run();
//		
		
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
			// TODO Auto-generated method stub
			
			float d;
			if(data == "Hi Alex") {
				d = (float) 1.0;
			} else {
				d = (float) 0.0;
			}
//			ct.interrupt();
			
			BioSensorEvent event = new BioSensorEvent(mSensor, sampleRate,d);
			mListener.onBioSensorChange(event);
			
		}
	};
//	@Override
//	public void onDataReceived(String data) {
//		// TODO Auto-generated method stub
//		float d;
//		if(data == "Hi Alex") {
//			d = (float) 1.0;
//		} else {
//			d = (float) 0.0;
//		}
//		
//		BioSensorEvent event = new BioSensorEvent(mSensor, sampleRate,d);
//		mListener.onBioSensorChange(event);
//	}
}

package com.geminnu.hexagon;

import java.net.Socket;

import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ArduinoService extends Service{
	
	private final String TAG = "Arduino Service";
	public final static String ADDRESS = "Address";
	public final static String COMMUNICATION_TYPE = "Type";
	public final static String DATA_CONTAINER = "Data Container";
	public final static int BLUETOOTH = 0;
	public final static int WIFI = 1;
	public final static int XML = 0;
	public final static int JSON = 1;
	public final static int SENSOR = 0;
	public final static int STATUS = 1;
	
	
	
	private Bluetooth mBluetooth;
	private Wifi mWifi;
	private Socket mWifiSocket;
	private BluetoothSocket mSocket;
	private String mAddress;
	private int mTypeOfConnection;
	private int mDataContainer;
	private boolean mSocketSuccess = false;
	private CoordinatorActionListener mActionListenerSensors;
	private CoordinatorActionListener mActionListenerStatus;	
	
	private ArduinoReceiver mArduinoReceiver;
	private ArduinoTransmitter mArduinoTransmitter;
	private final IBinder mBinder = new ArduinoServiceBinder();
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate()");
	}
	
	
		
	public class ArduinoServiceBinder extends Binder {
			
		public ArduinoService getServerInstance() {
			return ArduinoService.this;
	    }
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		Log.d(TAG, "onStartCommand");
		mTypeOfConnection = intent.getIntExtra(COMMUNICATION_TYPE, BLUETOOTH);
		mDataContainer = intent.getIntExtra(DATA_CONTAINER, XML);
		mAddress = intent.getStringExtra(ADDRESS);
		
		
		if(mAddress != null) {
			switch(mTypeOfConnection) {
			case BLUETOOTH:
				initBluetooth();
				break;
			case WIFI:
				initWifi();
				break;
			}
		}
		
		if(mSocketSuccess) {
			switch(mTypeOfConnection) {
			case BLUETOOTH:
				mArduinoReceiver = new ArduinoReceiver(mSocket, null, myMessage1);
				mArduinoReceiver.start();
				break;
			case WIFI:
				mArduinoReceiver = new ArduinoReceiver(null, mWifiSocket, myMessage1);
				mArduinoReceiver.start();
				break;
			}
//			mArduinoReceiver = new ArduinoReceiver(mSocket, myMessage1);
//			mArduinoReceiver.start();
		}
		
		
		return START_REDELIVER_INTENT;
	}
	
	@Override
	public void onDestroy() {
		// TODO: notify all that the service / socket will close and the connection will be terminated
		Log.d(TAG, "onDestroy()");
		myMessageSend = null;
		mArduinoReceiver.interrupt();
		mArduinoTransmitter.interrupt();
		mActionListenerSensors = null;
		mActionListenerStatus = null;
		myMessage1 = null;
	}
	
	private MessageListener myMessage1 = new MessageListener() {
		
		@Override
		public void onDataReceived(String data) {
			
			Log.d(TAG, "hello from receiver");
//			Log.d(TAG, "type: " + data.getMsgType() + " value: " + data.getValue());
			if(data != null && !data.equals("problem")) {
				Coordinator coo = new Coordinator(mDataContainer, data);
				if(coo.decision() == SENSOR) {
					Log.d(TAG, "hello from receiver2");
					mActionListenerSensors.onNewAction(coo.getArduinoMessage());
					Log.d(TAG, "hello from receiver3");
				}else if(coo.decision() == STATUS) {
					Log.d(TAG, "hello from receiver4");
					if(coo.getArduinoMessage().getType() == 11 && coo.getArduinoMessage().getValue() == 1.0) {
						mActionListenerStatus.onNewAction(new ArduinoMessage(111, 111, 0));
						mActionListenerSensors.onNewAction(new ArduinoMessage(111, 111, 0));
					} else {
						mActionListenerStatus.onNewAction(coo.getArduinoMessage());
					}
					Log.d(TAG, "hello from receiver5");
				}
			
			} else if(data.equals("problem")) {
				mArduinoReceiver.interrupt();
				if(mArduinoTransmitter != null){
					mArduinoTransmitter.interrupt();
				}
				while(mActionListenerSensors == null && mActionListenerStatus == null) {
//					Log.d(TAG, "it's a trap");
				}
				if(mActionListenerSensors !=null && mActionListenerStatus != null){
				mActionListenerSensors.onNewAction(new ArduinoMessage(111, 111, 0));
				mActionListenerStatus.onNewAction(new ArduinoMessage(111, 111, 0));
				stopSelf();
				}
			}
		}
	};
	
	public MessageSender getMessageSenderListener() {
		return myMessageSend;
		
	}
	private MessageSender myMessageSend = new MessageSender() {
		
		@Override
		public void onDataSend(String message) {
			// TODO Auto-generated method stub
			Log.d(TAG, "hello from sender");
			if(message != null) {
				switch(mTypeOfConnection) {
				case BLUETOOTH:
					mArduinoTransmitter = new ArduinoTransmitter(message, mSocket, null);
					mArduinoTransmitter.start();
					break;
				case WIFI:
					mArduinoTransmitter = new ArduinoTransmitter(message, null, mWifiSocket);
					mArduinoTransmitter.start();
					break;
				}
				
//				mArduinoTransmitter = new ArduinoTransmitter(message, mSocket);
//				mArduinoTransmitter.start();
			}
		}
	};
	
	public void sendActionListenerForSensors(CoordinatorActionListener actionListener) {
		this.mActionListenerSensors = actionListener;
	}
	public void sendActionListenerForStatus(CoordinatorActionListener actionListener) {
		this.mActionListenerStatus = actionListener;
	}
	
	public int getContainer() {
		return mDataContainer;
	}
	protected void initBluetooth() {
		mBluetooth = new Bluetooth(mAddress); 
		mSocket = mBluetooth.createBlueSocket();
		mSocketSuccess = true;
	}
	
	protected void initWifi() {
		mWifi = new Wifi(mAddress);
		mWifiSocket = mWifi.createWifiSocket();
		mSocketSuccess = true;
	}
}

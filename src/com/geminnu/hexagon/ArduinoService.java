package com.geminnu.hexagon;

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
	private BluetoothSocket mSocket;
	private String mAddress;
	private int mTypeOfConnection;
	private int mDataContainer;
	private boolean mSocketSuccess = false;
	private CoordinatorActionListener mActionListener;
	
	private ArduinoReceiver mArduinoReceiver;
	private ArduinoTransmitter mArduinoTransmitter;
	private final IBinder mBinder = new ArduinoBinder();
	
	@Override
	public void onCreate() {
		//	TODO: init bluetooth or wifi for the creation of the socket.
		Log.d(TAG, "onCreate() stage");
	}
	
	
		
	public class ArduinoBinder extends Binder {
			
		public ArduinoService getServerInstance() {
			return ArduinoService.this;
	    }
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//	TODO: start an inputstream based on the socket
		
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
			mArduinoReceiver = new ArduinoReceiver(mSocket, myMessage1);
			mArduinoReceiver.start();
		}
		
		
		return START_REDELIVER_INTENT;
	}
	
	private MessageListener myMessage1 = new MessageListener() {
		
		@Override
		public void onDataReceived(String data) {
			// TODO Auto-generated method stub
			Log.d(TAG, "hello from receiver");
//			Log.d(TAG, "type: " + data.getMsgType() + " value: " + data.getValue());
			Coordinator coo = new Coordinator(mDataContainer, data);
			if(coo.decision() == SENSOR) {
				Log.d(TAG, "hello from receiver2");
				Log.d(TAG, coo.getArduinoMessage().getSensor());
				mActionListener.onNewAction(coo.getArduinoMessage());
				Log.d(TAG, "hello from receiver3");
			}else if(coo.decision() == STATUS) {
				//	TODO: implement for status manager
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
			mArduinoTransmitter = new ArduinoTransmitter(message, mSocket);
			mArduinoTransmitter.start();
			
		}
	};
	
	public void sendActionListener(CoordinatorActionListener actionListener) {
		this.mActionListener = actionListener;
	}
	
	@Override
	public void onDestroy() {
		// TODO: notify all that the service / socket will close and the connection will be terminated
		Log.d(TAG, "onDestroy() stage.Service is closing.");
	}
	
	protected void initBluetooth() {
		mBluetooth = new Bluetooth(mAddress); 
		mSocket = mBluetooth.createBlueSocket();
		mSocketSuccess = true;
	}
	
	protected void initWifi() {
		//	TODO: Implement wifi
		mSocketSuccess = true;
	}
}

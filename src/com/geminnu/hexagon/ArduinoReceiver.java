package com.geminnu.hexagon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ArduinoReceiver extends Thread{
	
	private final String TAG = "ArduinoReceiver";
	private final BluetoothSocket mSocket;
    private InputStream mInStream;
    private InputStream tmpIn = null;
    private MessageListener mMsgListener;

    public ArduinoReceiver(BluetoothSocket socket, MessageListener listener) {
        this.mSocket = socket;
        this.mMsgListener = listener;
    }

    public void run() {
    	
    	try {
            tmpIn = mSocket.getInputStream();
            mInStream = tmpIn;
        } catch (IOException e) {
            Log.d(TAG, "temp sockets not created");
        }

        Log.d(TAG, "Begin Listening");
        
        BufferedReader br = new BufferedReader(new InputStreamReader(mInStream));

        // Keep listening to the InputStream while connected
        while (true && !Thread.interrupted()) {
            try {
            	
            	String data = br.readLine();
            	
            	if(data != null) {
            		mMsgListener.onDataReceived(data);
            		data = null;
            	} 
            } catch (IOException e) {
                Log.e(TAG, "disconnected", e);
//              connectionLost();
                break;
            }
        }
        
        try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}

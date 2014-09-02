package com.geminnu.hexagon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ArduinoReceiver extends Thread{
	
	private final String TAG = "ArduinoReceiver";
	private final BluetoothSocket mSocket;
	private final Socket mWifiSocket;
    private InputStream mInStream;
    private InputStream tmpIn = null;
    private MessageListener mMsgListener;
    private boolean mConnected = false;

    public ArduinoReceiver(BluetoothSocket bluesocket, Socket wifisocket, MessageListener listener) {
        this.mSocket = bluesocket;
        this.mMsgListener = listener;
        this.mWifiSocket = wifisocket;
    }

    public void run() {
    	if(mSocket != null) {
    	try {
            tmpIn = mSocket.getInputStream();
            mInStream = tmpIn;
            mConnected = true;
        } catch (IOException e) {
            Log.d(TAG, "temp sockets not created");
            mConnected = false;
            mMsgListener.onDataReceived("problem");
        }

        Log.d(TAG, "Begin Listening");
        
        BufferedReader br = new BufferedReader(new InputStreamReader(mInStream));

        // Keep listening to the InputStream while connected
        while (mConnected && !Thread.interrupted()) {
            try {
            	
            	String data = br.readLine();
            	
            	if(data != null) {
            		mMsgListener.onDataReceived(data);
            		data = null;
            	}
            } catch (IOException e) {
                Log.d(TAG, "disconnected");
                mConnected = false;
                mMsgListener.onDataReceived("problem");
                break;
            }
        }
        
        try {
			Log.d(TAG, "Close socket");
        	br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	} else if(mWifiSocket != null){
    		try {
                tmpIn = mWifiSocket.getInputStream();
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
                    mMsgListener.onDataReceived("problem");
                    break;
                }
            }
            
            try {
    			br.close();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	} else {
    		mConnected = false;
            mMsgListener.onDataReceived("problem");
    	}
    }

}

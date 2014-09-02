package com.geminnu.hexagon;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ArduinoTransmitter extends Thread{

	final String TAG = "ArduinoTransmitter";
	private String mMessage;
	private BluetoothSocket mSocket;
	private Socket mWifiSocket;
   
    private OutputStream mmOutStream;
    private OutputStream tmpOut = null;
	byte[] msgBuffer;
	
	public ArduinoTransmitter(String msg, BluetoothSocket socket, Socket wifisocket) {
		this.mMessage = msg;
		this.mSocket = socket;
		this.mWifiSocket = wifisocket;
	}
	
	@Override
	public void run() {
		
		msgBuffer = mMessage.getBytes();
        if(mSocket != null && !Thread.interrupted()) {
	        try {
	            tmpOut = mSocket.getOutputStream();
	            mmOutStream = tmpOut;
	        } catch (IOException e) {
	            Log.d(TAG, "temp sockets not created");
	            
	            try {
					mSocket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            
	        }
			
	        write(msgBuffer);
        } else if(mWifiSocket != null && !Thread.interrupted()) {
        	try {
	            tmpOut = mWifiSocket.getOutputStream();
	            mmOutStream = tmpOut;
	        } catch (IOException e) {
	            Log.d(TAG, "temp sockets not created");
	            
	            try {
					mSocket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            
	        }
			
	        write(msgBuffer);
        }
	}
	
    /**
     * Write to the connected OutStream.
     * @param buffer  The bytes to write
     */
    public void write(byte[] buffer) {
    	
    	try {
            mmOutStream.write(buffer);
            Log.d(TAG, "String was sent");
        } catch (IOException e) {
            Log.d(TAG, "Exception during write");
            
            try {
				if(mSocket != null){
            	mSocket.close();
				} else if(mWifiSocket != null){
					mWifiSocket.close();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
    }
}

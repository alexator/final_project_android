package com.geminnu.hexagon;

import java.io.IOException;
import java.io.OutputStream;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ArduinoTransmitter extends Thread{

	final String TAG = "ArduinoTransmitter";
	private String mMessage;
	private BluetoothSocket mSocket;
   
    private OutputStream mmOutStream;
    private OutputStream tmpOut = null;
	byte[] msgBuffer;
	
	public ArduinoTransmitter(String msg, BluetoothSocket socket) {
		this.mMessage = msg;
		this.mSocket = socket;
	}
	
	@Override
	public void run() {
		
		msgBuffer = mMessage.getBytes();
        
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
				mSocket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
    }
}

package com.geminnu.hexagon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.TimerTask;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class Arduino extends TimerTask{

	private String message1;
	private ConnectedThread ct;
	private BluetoothSocket socket;
	private MessageListener mListener;
   
    private final OutputStream mmOutStream;
    private String TAG = "Connected Thread";
	String message = "Hello Alex";
	byte[] msgBuffer = message.getBytes();
	
	public Arduino(String msg, BluetoothSocket socket, MessageListener listener) {
		this.message1 = msg;
		this.socket = socket;
		this.mListener = listener;
        OutputStream tmpOut = null;
        
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "temp sockets not created", e);
        }

        mmOutStream = tmpOut;
	}
	
	@Override
	public void run() {
		 write(msgBuffer);
	}
	
    /**
     * Write to the connected OutStream.
     * @param buffer  The bytes to write
     */
    public void write(byte[] buffer) {
        try {
            mmOutStream.write(buffer);
//            mmOutStream.close();

            // Share the sent message back to the UI Activity
//            mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1, buffer)
//                    .sendToTarget();
            Log.d(TAG, "String was sent");
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }
	

}

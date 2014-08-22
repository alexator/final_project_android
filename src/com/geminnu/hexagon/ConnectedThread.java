package com.geminnu.hexagon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class ConnectedThread extends Thread{
	
	private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
//    private final OutputStream mmOutStream;
    private MessageListener mMsgListener;
    private String TAG = "Connected Thread";

    public ConnectedThread(BluetoothSocket socket, MessageListener listener) {
        Log.d(TAG, "create ConnectedThread");
        this.mmSocket = socket;
        this.mMsgListener = listener;
        InputStream tmpIn = null;
//        OutputStream tmpOut = null;

        // Get the BluetoothSocket input and output streams
        try {
            tmpIn = socket.getInputStream();
//            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "temp sockets not created", e);
        }

        mmInStream = tmpIn;
//        mmOutStream = tmpOut;
    }

    public void run() {
        Log.i(TAG, "BEGIN mConnectedThread");
        byte[] buffer = new byte[1024];
        BufferedReader br = new BufferedReader(new InputStreamReader(mmInStream));
        int bytes;

        // Keep listening to the InputStream while connected
        while (true && !Thread.interrupted()) {
            try {
                // Read from the InputStream
//                bytes = mmInStream.read(buffer);
            	String data = br.readLine();

                // Send the obtained bytes to the UI Activity
//                mHandler.obtainMessage(data).sendToTarget();
            	
            	if(data != null) {
            	    mMsgListener.onDataReceived("Hi Alex");
            		Log.d("yeah thread", data);
            		data = null;
            	}
                
                //Log.d(TAG, data);
            } catch (IOException e) {
                Log.e(TAG, "disconnected", e);
//                connectionLost();
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

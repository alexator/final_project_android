package com.geminnu.hexagon;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class Bluetooth {
	
	private String TAG = "Hexagon";
	private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private String arduinoMacAdrr;
    private BluetoothSocket btSocket = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice arduino;
	
	
	public Bluetooth(String hexAddr) {
		
		this.arduinoMacAdrr = hexAddr;
		
	}
	
	public BluetoothSocket createBlueSocket() {
			
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			arduino = mBluetoothAdapter.getRemoteDevice(arduinoMacAdrr);
			
			try {
				
	//			btSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
				btSocket = arduino.createRfcommSocketToServiceRecord(MY_UUID);
				btSocket.connect();
				Log.d(TAG, "Bluetooth socket creation completed");
				
			} catch (IOException e1) {
				
				Log.d(TAG, "Bluetooth socket creation failed");
			}
			
			if(btSocket.isConnected()) {
				return btSocket;
			} else {
				return null;
			}
		}
		
		public void closeBlueSocket() {
			
			try {
				if(btSocket.isConnected()) {
					btSocket.close();
					Log.d(TAG, "Bluetooth socket destruction completed");
				} else {
					
				}
			} catch(IOException e2) {
				Log.d(TAG, "Bluetooth socket destruction failed");
			}
		}
		
		public boolean isAlive() {
			
			if(btSocket.isConnected()) {
				return true;
			}
			
			return false;
		}	
}

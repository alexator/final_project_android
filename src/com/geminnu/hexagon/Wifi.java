package com.geminnu.hexagon;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class Wifi {

	private String TAG = "Wifi";
	
    private Socket wifiSocket = null;
    private InetAddress arduinoIpAdrr;
    private int port = 3456;
    
    public Wifi(String addr) {
    	try {
			this.arduinoIpAdrr = InetAddress.getByName(addr);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public Socket createWifiSocket() {
    	try {
			wifiSocket = new Socket(arduinoIpAdrr,port);
			Log.d(TAG, "wifi socket is created");
			return wifiSocket;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    public void closeWifiSocket() {
    	try {
			wifiSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public boolean isAlive() {
    	return wifiSocket.isConnected();
    }
}

package com.geminnu.hexagon;

import java.util.TimerTask;

import android.util.Log;

public class ArduinoTask extends TimerTask {
	
	private MessageSender mMessage;
	private int casesen;
	
	public ArduinoTask(MessageSender mSender, int casei) {
		this.mMessage = mSender;
		this.casesen = casei;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(casesen == 0){
			mMessage.onDataSend("0;0;6");
		}else if (casesen == 1) {
			mMessage.onDataSend("0;0;7");
		}
		Log.d("Android Task", "hello from task");
	}

}

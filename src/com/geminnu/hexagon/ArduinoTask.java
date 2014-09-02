package com.geminnu.hexagon;

import java.util.TimerTask;

import android.util.Log;

public class ArduinoTask extends TimerTask {
	
	public final static int GETGLUCO = 0;
	public final static int GETBODYTEMP = 1;
	public final static int GETBLOODPRES = 2;
	public final static int GETPULSOX = 3;
	public final static int GETAIRFLOW = 4;
	public final static int GETGALVAN = 5;
	public final static int GETECG = 6;
	public final static int GETEMG = 7;
	public final static int GETPOSITION = 8;
	public final static int GETBATT = 9;
	public final static int GETAVAIL = 10;
	public final static int GETCONNE = 11;
	
	private MessageSender mMessage;
	private int mCase;
	private int mContainer;
	
	
	public ArduinoTask(MessageSender mSender, int mcase, int container) {
		this.mMessage = mSender;
		this.mCase = mcase;
		this.mContainer = container;
	}
	
	@Override
	public void run() {
		
		if(mContainer == ArduinoService.XML) {
			switch(mCase) {
				case 0:
					mMessage.onDataSend("0;0;0;0");
					break;
				case 1:
					mMessage.onDataSend("0;0;1;0");
					break;
				case 2:
					mMessage.onDataSend("0;0;2;0");
					break;
				case 3:
					mMessage.onDataSend("0;0;3;0");
					break;
				case 4:
					mMessage.onDataSend("0;0;4;0");
					break;
				case 5:
					mMessage.onDataSend("0;0;5;0");
					break;
				case 6:
					mMessage.onDataSend("0;0;6;0");
					break;
				case 7:
					mMessage.onDataSend("0;0;7;0");
					break;
				case 8:
					mMessage.onDataSend("0;0;8;0");
					break;
				case 9:
					mMessage.onDataSend("1;0;0;0");
					break;
				case 10:
					mMessage.onDataSend("1;0;10;0");
					break;
				case 11:
					mMessage.onDataSend("1;0;11;0");
					break;
			}
		} else {
			switch(mCase) {
				case 0:
					mMessage.onDataSend("0;0;0;1");
					break;
				case 1:
					mMessage.onDataSend("0;0;1;1");
					break;
				case 2:
					mMessage.onDataSend("0;0;2;1");
					break;
				case 3:
					mMessage.onDataSend("0;0;3;1");
					break;
				case 4:
					mMessage.onDataSend("0;0;4;1");
					break;
				case 5:
					mMessage.onDataSend("0;0;5;1");
					break;
				case 6:
					mMessage.onDataSend("0;0;6;1");
					break;
				case 7:
					mMessage.onDataSend("0;0;7;1");
					break;
				case 8:
					mMessage.onDataSend("0;0;8;1");
					break;
				case 9:
					mMessage.onDataSend("1;0;0;1");
					break;
				case 10:
					mMessage.onDataSend("1;0;10;1");
					break;
				case 11:
					mMessage.onDataSend("1;0;11;1");
					break;
			}
		}
		Log.d("Android Task", "hello from task");
	}
}

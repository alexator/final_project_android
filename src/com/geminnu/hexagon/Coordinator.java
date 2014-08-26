package com.geminnu.hexagon;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

public class Coordinator {
	
	private int mParsingMethod;
	private String mData;
	private ArduinoMessage am;
	private ArduinoParser ap;
	
	public Coordinator(int parsingMethod, String data) {
		this.mParsingMethod = parsingMethod;
		this.mData = data;
	}
	
	public int decision() {
		if(mParsingMethod == ArduinoService.XML) {
			ap = new ArduinoParser(mData);
			try {
				am = ap.ReadData();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(am.getMsgType().equals("sensor_data")){
				return ArduinoService.SENSOR;
			} else {
				return ArduinoService.STATUS; 
			}
		} else if(mParsingMethod == ArduinoService.JSON) {
//			ap = new ArduinoParser(mData);
			try {
				am = ap.ReadData();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(am.getMsgType().equals("sensor_data")){
				return ArduinoService.SENSOR;
			} else {
				return ArduinoService.STATUS; 
			}
		}
		return -1;
	}
	
	public ArduinoMessage getArduinoMessage() {
		return am;
	}
}

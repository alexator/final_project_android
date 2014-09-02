package com.geminnu.hexagon;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

public class Coordinator {
	
	private int mParsingMethod;
	private String mData;
	private ArduinoMessage am;
	private ArduinoParser ap;
	private final int SENSORDATA = 0;
	private final int STATUSDATA = 1;
	
	
	public Coordinator(int parsingMethod, String data) {
		this.mParsingMethod = parsingMethod;
		this.mData = data;
	}
	
	public int decision() {
		if(mParsingMethod == ArduinoService.XML) {
			ap = new ArduinoParser(mData);
			try {
				am = ap.ReadDataXML();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(am.getMsgType() == SENSORDATA){
				return ArduinoService.SENSOR;
			} else if(am.getMsgType() == STATUSDATA) {
				return ArduinoService.STATUS; 
			}
		} else if(mParsingMethod == ArduinoService.JSON) {
			ap = new ArduinoParser(mData);
		
			am = ap.ReadDataJSON();
		
			if(am.getMsgType() == SENSORDATA){
				return ArduinoService.SENSOR;
			} else if(am.getMsgType() == STATUSDATA) {
				return ArduinoService.STATUS; 
			}
		}
		return -1;
	}
	
	public ArduinoMessage getArduinoMessage() {
		return am;
	}
}

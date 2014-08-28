package com.geminnu.hexagon;

import java.io.IOException;
import java.io.StringReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class ArduinoParser	{
	
	private String mMessage;
	
	public ArduinoParser(String message) {
		this.mMessage = message;
	}
	
	public ArduinoMessage ReadDataXML() throws XmlPullParserException, IOException	{
		
		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        StringReader sR = new StringReader(mMessage);
        xpp.setInput(sR);
        int eventType = xpp.getEventType();
        
        
        String message_type = null;
        String sensor = null;
        String value = null;
        float valueFromString = 0;
        
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_DOCUMENT) {
            } else if(eventType == XmlPullParser.END_DOCUMENT) {
            } else if(eventType == XmlPullParser.START_TAG) {
            	String name = xpp.getName();
    			
    			if (name.equals("message_type")) {
    				message_type = ReadMessageType(xpp, eventType);
    			} else if (name.equals("type")) {
    				sensor = ReadSensorType(xpp, eventType);
    			} else if (name.equals("value")) {
    				value = ReadValue(xpp, eventType);
    				valueFromString = Float.valueOf(value);
    			} else {
    				
    			}
            } else if(eventType == XmlPullParser.END_TAG) {
            } else if(eventType == XmlPullParser.TEXT) {
            }
            eventType = xpp.next();
           }
        
        
        return new ArduinoMessage(message_type, sensor, valueFromString);
        
	}
	private String ReadMessageType(XmlPullParser parser, int eventType) throws XmlPullParserException, IOException {
        String msg_type = readText(parser);
        return msg_type;
    }
	
	private String ReadSensorType(XmlPullParser parser, int eventType) throws XmlPullParserException, IOException {
        String sensor = readText(parser);
        return sensor;
    }
	
	private String ReadValue(XmlPullParser parser, int eventType) throws XmlPullParserException, IOException {
        String value = readText(parser);
        return value;
    }
	
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = "";
	    
		if (parser.next() == XmlPullParser.TEXT) {
	        result = parser.getText();
	    }
		
	    return result;
	}
	
	public ArduinoMessage ReadDataJSON() {
		
		String message_type = null;
        String sensor = null;
        String value = null;
        float valueFromString = 0;
       if(mMessage != null) {
        try {
			JSONObject jsonObj = new JSONObject(mMessage);
			JSONArray data = jsonObj.getJSONArray("data");
			
			for(int i = 0; i < data.length(); i++) {
				
				JSONObject d = data.getJSONObject(i);
				
				message_type = d.getString("type");
				sensor = d.getString("sensor");
				value = d.getString("value");
				valueFromString = Float.valueOf(value);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       }    
        return new ArduinoMessage(message_type, sensor, valueFromString);
	
	}
}

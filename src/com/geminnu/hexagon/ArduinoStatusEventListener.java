package com.geminnu.hexagon;

import java.util.ArrayList;

public interface ArduinoStatusEventListener {
	public void onStatusChange(ArduinoStatusEvent event);
	public void onStatusChangeAvailableSensors(ArrayList<BioSensor> list);
}

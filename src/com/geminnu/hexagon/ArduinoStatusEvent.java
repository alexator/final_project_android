package com.geminnu.hexagon;

public class ArduinoStatusEvent {
	
	private ArduinoStatus mStatus;
	
	public ArduinoStatusEvent(ArduinoStatus status) {
		this.mStatus = status;
	}
	
	public ArduinoStatus getStatus() {
		return mStatus;
	}
}

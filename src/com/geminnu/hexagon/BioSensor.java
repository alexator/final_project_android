package com.geminnu.hexagon;

public class BioSensor {
	
	//The name of the sensor
	private String mSensorName;
	
	private int mSensorId;
	private String mBase;
	
	//The type of the sensor (e.g.: generic, medical etc)
	private int mSensorType;
	
	//The signal type of the sensor (e.g.: analog or digital)
	private int mSignalType;
	
	//Constructor of the class
	public BioSensor(int sensId, int type, int signal) {
		this.mSensorId = sensId;
		this.mSensorType = type;
		this.mSignalType = signal;
		assignName(mSensorId);
		
	}
	
	private void assignName(int id) {
		switch(id) {
			case 0:
				this.mSensorName = "GLUCO";
				this.mBase = "mg/dL";
				break;
			case 1:
				this.mSensorName = "BODYTEMP";
				this.mBase = "C";
				break;
			case 2:
				this.mSensorName = "BLOOD";
				this.mBase = "%SPo2";
				break;
			case 3:
				this.mSensorName = "PULSE";
				this.mBase = "PRbpm";
				break;
			case 4:
				this.mSensorName = "AIRFLOW";
				this.mBase = "bpm";
				break;
			case 5:
				this.mSensorName = "GALVANICSKIN";
				this.mBase = "Ohm";
				break;
			case 6:
				this.mSensorName = "ECG";
				this.mBase = "V";
				break;
			case 7:
				this.mSensorName = "EMG";
				break;
			case 8:
				this.mSensorName = "PATIENTPOSITION";
				break;
				
		}
	}
	//Getters
	public String getName() {
		return mSensorName;
	}
	public String getBase() {
		return mBase;
	}
	
	public int getType() {
		return mSensorType;
	}
	public int getSensorId() {
		return mSensorId;
	}
	public int getSignal() {
		return mSignalType;
	}
}

package com.geminnu.hexagon;

import android.os.Parcel;
import android.os.Parcelable;

public class Alert implements Parcelable{
	private int mBiosensor;
	private double mMaxvalue;
	private double mMinvalue;
	
	public Alert(int sensor, double max, double min) {
		this.mBiosensor = sensor;
		this.mMaxvalue = max;
		this.mMinvalue = min;
	}
	
	public Alert(Parcel in) {
		this.mBiosensor = in.readInt();
		this.mMaxvalue = in.readDouble();
		this.mMinvalue = in.readDouble();
	}
	
	public String check(double current) {
		if(current >= mMinvalue && current <= mMaxvalue) {
			return "GOOD";
		}
		return "BAD";
	}
	
	public int getSensorId() {
		return mBiosensor;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(mBiosensor);
		dest.writeDouble(mMaxvalue);
		dest.writeDouble(mMinvalue);
	}
	
	public static final Parcelable.Creator<Alert> CREATOR= new Parcelable.Creator<Alert>() {

        @Override
        public Alert createFromParcel(Parcel in) {
                // TODO Auto-generated method stub
                return new Alert(in);  //using parcelable constructor
        }

        @Override
        public Alert[] newArray(int size) {
                // TODO Auto-generated method stub
                return new Alert[size];
        }
	};
}

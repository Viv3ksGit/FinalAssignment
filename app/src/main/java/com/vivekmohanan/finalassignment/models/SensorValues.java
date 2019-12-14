package com.vivekmohanan.finalassignment.models;

import com.google.firebase.database.IgnoreExtraProperties;

import android.os.Parcel;
import android.os.Parcelable;

@IgnoreExtraProperties
public class SensorValues implements Parcelable {


    public float getxValue() {
        return xValue;
    }

    public float getyValue() {
        return yValue;
    }

    public String getTime() {
        return time;
    }

    public float xValue;
    public float yValue;
    public String time;

    public SensorValues() {

    }

    public SensorValues(float xValue, float yValue, String time) {
        this.xValue = xValue;
        this.yValue = yValue;
        this.time = time;
    }

    protected SensorValues(Parcel in) {

        xValue = in.readFloat();
        yValue = in.readFloat();
        time = in.readString();
    }

    public static final Creator<SensorValues> CREATOR = new Creator<SensorValues>() {
        @Override
        public SensorValues createFromParcel(Parcel in) {
            return new SensorValues(in);
        }

        @Override
        public SensorValues[] newArray(int size) {
            return new SensorValues[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel endpoint, int flags) {

        endpoint.writeFloat(xValue);
        endpoint.writeFloat(yValue);
        endpoint.writeString(time);
    }


}

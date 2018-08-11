package com.satellite.takephoto.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GpsInfo implements Parcelable {
	/**
	 * GPS精度
	 * */
	private float accuracy;
	/**
	 * 经度
	 * */
	private double longitude;
	/**
	 * 纬度
	 * */
	private double latitude;
	/**
	 * 速度
	 * */
	private float speed;
	/**
	 * GPS时间
	 * */
	private long time;

	public GpsInfo() {

	}

	@Override
	public int describeContents() {
		return 0;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(accuracy);
		dest.writeDouble(longitude);
		dest.writeDouble(latitude);
		dest.writeFloat(speed);
		dest.writeLong(time);
	}

	/**
	 * 用来创建自定义的Parcelable的对象
	 * */
	public static final Creator<GpsInfo> CREATOR = new Creator<GpsInfo>() {
		public GpsInfo createFromParcel(Parcel in) {
			return new GpsInfo(in);
		}

		public GpsInfo[] newArray(int size) {
			return new GpsInfo[size];
		}
	};

	/**
	 * 读数据进行恢复
	 * */
	private GpsInfo(Parcel in) {
		accuracy = in.readFloat();
		longitude = in.readDouble();
		latitude = in.readDouble();
		speed = in.readFloat();
		time = in.readLong();
	}

	@Override
	public String toString() {
		return "GpsInfo [accuracy=" + accuracy + ", longitude=" + longitude
				+ ", latitude=" + latitude + ", speed=" + speed + ", time="
				+ time + "]";
	}

}

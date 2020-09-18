package com.example.lawrence.getconnected.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Hall implements Parcelable {
    private String name , address,HallId;

    public Hall() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHallId() {
        return HallId;
    }

    public void setHallId(String hallId) {
        HallId = hallId;
    }

    public static Creator<Hall> getCREATOR() {
        return CREATOR;
    }

    protected Hall(Parcel in) {
        name = in.readString();
        address = in.readString();
        HallId = in.readString();
    }

    public static final Creator<Hall> CREATOR = new Creator<Hall>() {
        @Override
        public Hall createFromParcel(Parcel in) {
            return new Hall(in);
        }

        @Override
        public Hall[] newArray(int size) {
            return new Hall[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(HallId);
    }
}

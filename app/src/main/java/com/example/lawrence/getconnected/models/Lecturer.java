package com.example.lawrence.getconnected.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Lecturer implements Parcelable {
    private  String name ,username,password, lecturerId;
    private Long rating;

    public Lecturer() {
    }

    protected Lecturer(Parcel in) {
        name = in.readString();
        username = in.readString();
        password = in.readString();
        lecturerId = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readLong();
        }
    }

    public static final Creator<Lecturer> CREATOR = new Creator<Lecturer>() {
        @Override
        public Lecturer createFromParcel(Parcel in) {
            return new Lecturer(in);
        }

        @Override
        public Lecturer[] newArray(int size) {
            return new Lecturer[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    public String getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(String lecturerId) {
        this.lecturerId = lecturerId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(lecturerId);
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(rating);
        }
    }
}

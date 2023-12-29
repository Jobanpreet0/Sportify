package uk.ac.aston.cs3mdd.sportify000.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SportItem implements Parcelable {

    private String name;
    private int imageResourceId;
    private double latitude;
    private double longitude;

    protected SportItem(Parcel in) {
        name = in.readString();
        imageResourceId = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<SportItem> CREATOR = new Creator<SportItem>() {
        @Override
        public SportItem createFromParcel(Parcel in) {
            return new SportItem(in);
        }

        @Override
        public SportItem[] newArray(int size) {
            return new SportItem[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }



    public SportItem(String name, int imageResourceId, double latitude, double longitude) {
        this.name = name;
        this.imageResourceId = imageResourceId;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public SportItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(imageResourceId);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }
}

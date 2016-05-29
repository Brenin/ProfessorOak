package com.example.eirikur.professoroak;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eirikur on 25/05/2016.
 */
public class Pokemon implements Parcelable{
    private String id;
    private String name;
    private Float lat = 0.0f;
    private Float lng = 0.0f;

    public Pokemon(String id, String name, String lat, String lng){
        setId(id);
        setName(name);
        setLat(lat);
        setLng(lng);
    }

    public Pokemon(String id, String name){
        setId(id);
        setName(name);
    }

    protected Pokemon(Parcel in) {
        id = in.readString();
        name = in.readString();
    }

    public static final Creator<Pokemon> CREATOR = new Creator<Pokemon>() {
        @Override
        public Pokemon createFromParcel(Parcel in) {
            return new Pokemon(in);
        }

        @Override
        public Pokemon[] newArray(int size) {
            return new Pokemon[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = Float.parseFloat(lat);
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = Float.parseFloat(lng);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }
}

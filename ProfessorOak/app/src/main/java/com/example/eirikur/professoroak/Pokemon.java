package com.example.eirikur.professoroak;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Eirikur on 25/05/2016.
 */
public class Pokemon {
    private String id;
    private String name;
    private String lat;
    private String lng;

    public Pokemon(String id, String name, String lat, String lng){
        setId(id);
        setName(name);
        setLat(lat);
        setLng(lng);
    }

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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String toString(){
        return getName();
    }
}

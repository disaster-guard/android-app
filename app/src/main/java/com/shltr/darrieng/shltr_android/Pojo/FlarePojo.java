package com.shltr.darrieng.shltr_android.Pojo;

/**
 * Basic POJO used to contain information about user location for "Flare" feature.
 */
public class FlarePojo {
    double longitude;

    double lat;

    int user_id;

    public FlarePojo(double longitude, double lat, int user_id) {
        this.longitude = longitude;
        this.lat = lat;
        this.user_id = user_id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}

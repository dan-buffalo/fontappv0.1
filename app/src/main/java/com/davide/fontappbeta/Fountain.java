package com.davide.fontappbeta;

import android.media.Image;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

public class Fountain {

    private GeoPoint geo_point;
    private Image image;
    private @ServerTimestamp String timeStamp;

    public Fountain(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public Fountain(){

    }

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}

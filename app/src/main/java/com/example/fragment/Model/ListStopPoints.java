package com.example.fragment.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ListStopPoints {

    @SerializedName("tourId")
    @Expose
    public String tourId;
    @SerializedName("stopPoints")
    @Expose
    public ArrayList<StopPoint>  stopPoints=new ArrayList<>();

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public ArrayList<StopPoint> getStopPoints() {
        return stopPoints;
    }

    public void setStopPoints(ArrayList<StopPoint> stopPoints) {
        this.stopPoints.addAll(stopPoints);
    }
}

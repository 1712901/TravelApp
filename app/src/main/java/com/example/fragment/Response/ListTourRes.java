package com.example.fragment.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListTourRes {
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("tours")
    @Expose
    private List<TourRes> tours = null;


    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<TourRes> getTours() {
        return tours;
    }

    public void setTours(List<TourRes> tours) {
        this.tours = tours;
    }
}

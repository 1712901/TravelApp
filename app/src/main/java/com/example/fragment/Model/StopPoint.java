package com.example.fragment.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StopPoint implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    private String name;
    @SerializedName("address")
    private String address;
    @SerializedName("lat")
    private double lat;
    @SerializedName("long")
    private double longitude;
    @SerializedName("arrivalAt")
    private long arrivalAt;
    @SerializedName("leaveAt")
    private long leaveAt;
    @SerializedName("serviceTypeId")
    private int serviceTypeId;
    @SerializedName("provinceId")
    private int provinceId;
    @SerializedName("minCost")
    @Expose
    private String minCost;
    @SerializedName("maxCost")
    @Expose
    private String maxCost;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @SerializedName("avatar")
    @Expose
    private String avatar;

    public StopPoint(String name, String address, double lat, double longitude, long arrivalAt, long leaveAt, int serviceTypeId, int provinceId, String minCost, String maxCost) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.longitude = longitude;
        this.arrivalAt = arrivalAt;
        this.leaveAt = leaveAt;
        this.serviceTypeId = serviceTypeId;
        this.provinceId = provinceId;
        this.minCost = minCost;
        this.maxCost = maxCost;
    }

    public String getMinCost() {
        return minCost;
    }

    public void setMinCost(String minCost) {
        this.minCost = minCost;
    }

    public String getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(String maxCost) {
        this.maxCost = maxCost;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getArrivalAt() {
        return arrivalAt;
    }

    public void setArrivalAt(long arrivalAt) {
        this.arrivalAt = arrivalAt;
    }

    public long getLeaveAt() {
        return leaveAt;
    }

    public void setLeaveAt(long leaveAt) {
        this.leaveAt = leaveAt;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}

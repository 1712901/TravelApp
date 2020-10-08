package com.example.fragment.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecoveryReq {
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("value")
    @Expose
    private String value;

    public RecoveryReq(String type, String value) {
        this.type = type;
        this.value = value;
    }
}

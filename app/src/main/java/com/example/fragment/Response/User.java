package com.example.fragment.Response;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("userId")
    private String id;

    @SerializedName("token")
    private String token;

    @SerializedName("message")
    private String message;

    public String getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }
}

package com.example.fragment.Model;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class GlobalClass extends Application {

    private String authorization;
    private String Id;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }
}

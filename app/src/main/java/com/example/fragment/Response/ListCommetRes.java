package com.example.fragment.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListCommetRes {
    @SerializedName("reviewList")
    @Expose
    private List<Comment> reviewList = null;

    public List<Comment> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Comment> reviewList) {
        this.reviewList = reviewList;
    }
}

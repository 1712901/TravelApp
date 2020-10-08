package com.example.fragment.network;

import android.widget.ImageButton;

import com.example.fragment.Model.CoordList;
import com.example.fragment.Model.ListStopPoints;
import com.example.fragment.Model.RecoveryReq;
import com.example.fragment.Model.StopPoint;
import com.example.fragment.Response.CreateTourRes;
import com.example.fragment.Response.InforTourRes;
import com.example.fragment.Response.ListCommetRes;
import com.example.fragment.Response.ListTourRes;
import com.example.fragment.Response.RecoveryRes;
import com.example.fragment.Response.User;
import com.example.fragment.Response.UserInforRes;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIService {

    @POST("/user/login")
    @FormUrlEncoded
    Call<User> login(@Field("emailPhone") String username,
                     @Field("password") String password );

    @GET("/tour/list")
    Call<ListTourRes> getListTour(@Header("Authorization") String Authorization,
                                  @Query("rowPerPage") Integer rowPerPage,
                                  @Query("pageNum") Integer pageNum);
    @GET("/tour/history-user")
    Call<ListTourRes> getListMyTour(@Header("Authorization") String Authorization,
                                    @Query("pageSize") Integer pageSize,
                                    @Query("pageIndex") Integer pageIndex);
    @POST("/user/register")
    @FormUrlEncoded
    Call<JSONObject> register(@Field("password") String password,
                              @Field("fullName") String fullName,
                              @Field("email") String email,
                              @Field("phone") String phone );
    @POST("/tour/create")
    @FormUrlEncoded
    Call<CreateTourRes> createTour(@Header("Authorization") String Authorization,
                                   @Field("name") String name,
                                   @Field("startDate") Number startDate,
                                   @Field("endDate") Number endDate,
                                   @Field("sourceLat") Number sourceLat,
                                   @Field("sourceLong") Number sourceLong,
                                   @Field("desLat") Number desLat,
                                   @Field("desLong") Number desLong,
                                   @Field("isPrivate") boolean isPrivate,
                                   @Field("adults") Number adults,
                                   @Field("childs") Number childs,
                                   @Field("minCost") Number minCost,
                                   @Field("maxCost") Number maxCost);

    @GET("/tour/info")
    Call<InforTourRes> getTourInfo(@Header("Authorization") String Authorization,
                                   @Query("tourId") Integer tourId);
    @POST("/tour/update-tour")
    @FormUrlEncoded
    Call<JSONObject> updateInfTour(@Header("Authorization") String Authorization,
                                   @Field("id") Integer tourId,
                                   @Field("name") String name,
                                   @Field("startDate") Number startDate,
                                   @Field("endDate") Number endDate,
                                   @Field("minCost") Number minCost,
                                   @Field("maxCost") Number maxCost,
                                   @Field("isPrivate") boolean isPrivate);

    @POST("/tour/update-tour")
    @FormUrlEncoded
    Call<JSONObject> deleteTour(@Header("Authorization") String Authorization,
                                   @Field("id") Integer tourId,
                                   @Field("status") Integer status);

    @GET("/tour/remove-stop-point")
    Call<JsonObject> removeStopPoint(@Header("Authorization") String Authorization,
                                     @Query("stopPointId") Integer stopPointId);
    @GET("/user/info")
    Call<UserInforRes> getUserInfor(@Header("Authorization") String Authorization);

    @POST("/tour/clone")
    @FormUrlEncoded
    Call<JSONObject> cloneTour(@Header("Authorization") String Authorization,
                                   @Field("tourId") Integer tourId);
    @POST("/user/update-password")
    @FormUrlEncoded
    Call<JSONObject> updatePassword(@Header("Authorization") String Authorization,
                                    @Field("userId") Integer userId,
                                    @Field("currentPassword") String currentPassword,
                                    @Field("newPassword") String newPassword);

    @POST("/user/edit-info")
    @FormUrlEncoded
    Call<JSONObject> updateInfoUser(@Header("Authorization") String Authorization,
                                    @Field("fullName") String fullName,
                                    @Field("email") String email,
                                    @Field("phone") String phone,
                                    @Field("gender") Number gender,
                                    @Field("dob") Date dob);


    @POST("/tour/set-stop-points")
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<JSONObject> addStopPoint(@Header("Authorization") String Authorization,
                                  @Body ListStopPoints stopPoints);
    @POST("/tour/set-stop-points")
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<JSONObject> updateStopPoint(@Header("Authorization") String Authorization,
                                  @Body ListStopPoints stopPoints);

    @POST("/tour/update-stop-point")
    @FormUrlEncoded
    Call<JSONObject> updateStopPoint(@Header("Authorization") String Authorization,
                                     @Field("id") String id,
                                     @Field("name") String name,
                                     @Field("startDate") Number startDate,
                                     @Field("endDate") Number endDate,
                                     @Field("lat") Number Lat,
                                     @Field("long") Number Long,
                                     @Field("serviceTypeId") Number serviceTypeId,
                                     @Field("minCost") Number minCost,
                                     @Field("maxCost") Number maxCost);

    @POST("user/request-otp-recovery")
    Call<RecoveryRes> recoveryPassword(@Header("Content-Type") String content,@Body RecoveryReq recoveryReq);

    @POST("/user/verify-otp-recovery")
    @FormUrlEncoded
    Call<JSONObject> verifyOtp(@Field("userId") Number id,
                                @Field("newPassword") String newPassword,
                                @Field("verifyCode") String OTP);

    @GET("/tour/get/review-list")
    Call<ListCommetRes> getListComment(@Header("Authorization") String Authorization,
                                       @Query("tourId") Integer tourId,
                                       @Query("pageIndex") Integer pageIndex,
                                       @Query("pageSize") Integer pageSize);
    @POST("/tour/add/review")
    @FormUrlEncoded
    Call<JSONObject> sendComment(@Header("Authorization") String Authorization,
                                 @Field("tourId") Integer tourId,
                                 @Field("point") Integer point,
                                 @Field("review") String comment);


    @POST("/tour/suggested-destination-list")
    @FormUrlEncoded
    Call<JSONObject> suggestDestinationsOne(@Header("Authorization") String Authorization,
                                         @Field("hasOneCoordinate") Boolean hasOneCoordinate,//false
                                         @Field("coordList") CoordList coordList);
    @POST("/tour/suggested-destination-list")
    @FormUrlEncoded
    Call<JSONObject> suggestDestinations(@Header("Authorization") String Authorization,
                                            @Field("hasOneCoordinate") Boolean hasOneCoordinate,//true
                                            @Field("coordList") CoordList.Coordinate coordinate);

}

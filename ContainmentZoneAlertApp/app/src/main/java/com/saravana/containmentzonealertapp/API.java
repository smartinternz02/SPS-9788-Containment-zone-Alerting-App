package com.saravana.containmentzonealertapp;

import com.saravana.containmentzonealertapp.models.AuthResponse;
import com.saravana.containmentzonealertapp.models.LocationResponse;
import com.saravana.containmentzonealertapp.models.User;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface API {
    @POST("/user/register")
    Call<ResponseBody> createUser(@Body User user);

    @POST("/user/authenticate")
    Call<AuthResponse> loginUser(@Body User user);


    @GET("/user/hello")
    Call<ResponseBody> helloWorld(@Header("Authorization") String authToken);


    @POST("/user/update")
    Call<Map<String,String>> updateAndCheckIfInsideCZone(@Header("Authorization") String authToken, @Query("lat") double lat, @Query("lng") double lng);

    @GET("/user/getAllCZone")
    Call<List<LocationResponse>> getAllCZones(@Header("Authorization") String authToken);

}

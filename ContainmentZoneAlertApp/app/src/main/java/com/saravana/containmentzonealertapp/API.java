package com.saravana.containmentzonealertapp;

import com.saravana.containmentzonealertapp.models.AuthResponse;
import com.saravana.containmentzonealertapp.models.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface API {
    @POST("register")
    Call<ResponseBody> createUser(@Body User user);

    @POST("authenticate")
    Call<AuthResponse> loginUser(@Body User user);

    @GET("hello")
    Call<ResponseBody> helloWorld(@Header("Authorization") String authToken);
}

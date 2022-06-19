package com.example.bankclientmvvm.api;

import com.example.bankclientmvvm.Account;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    //Link API: http://192.168.1.102:8081

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    //add ConverterFactory gson
    //add ConverterFactory scalar (other type)
    ApiService apiService = new Retrofit.Builder()
            .baseUrl("http://192.168.1.102:8081")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @POST("/login")
    Call<String> login(@Body String accountStrArrList);

    @POST("/loginsuccess")
    Call<String> loginSuccess(@Body ArrayList<String> accountStrArrList);

    @POST("/autologin")
    Call<String> autologin(@Body ArrayList<String> accountStrArrList);

    @POST("/account")
    Call<Account> getAccount(@Body String accountID);

    @POST("/editprofile")
    Call<String> editProfile(@Body ArrayList<String> accountStrArrList);

    @POST("/transfermoney")
    Call<Account> moneyTransfer(@Body ArrayList<String> accountStrArrList);

    @POST("/register")
    Call<Boolean> register(@Body Account account);

    //Link có tham số dạng động
    @GET("/checkaccountid/{accountID}")
    Call<Boolean> checkAccountID(@Path("accountID") String accountID);

    @GET("/checkemail/{email}")
    Call<Boolean> checkEmail(@Path("email") String email);
}

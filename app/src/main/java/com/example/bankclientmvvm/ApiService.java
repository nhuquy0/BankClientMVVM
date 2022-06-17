package com.example.bankclientmvvm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiService {

    //Link API: https://api.nationalize.io/?name=nathaniel

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

    //Get tách link
    @POST("/login")
    Call<String> login(@Body String accountStrArrList);

    @POST("/loginSuccess")
    Call<String> loginSuccess(@Body ArrayList<String> accountStrArrList);

    @POST("/autologin")
    Call<String> autologin(@Body ArrayList<String> accountStrArrList);

    //Get full link
//    @GET("/?name=nathaniel")
//    Call<User> getUser1();

    //Get full link
//    @GET("/")
//    Call<User> getUser2(@QueryMap Map<String, String> options);

    //Link API: https://api.nationalize.io/list/
//    @GET(/list/)
//    Call<User> getUser1();


    //Link có tham số dạng động
    //Link API: https://api.nationalize.io/group/1/users
//    @GET(/group/{id}/users)
//    Call<User> getUser1(@Path("id") int groupID);

    //Link có tham số dạng động và tĩnh
    //Link API: https://api.nationalize.io/group/1/users?sort=desc
//    @GET(/group/{id}/users)
//    Call<User> getUser1(@Path("id") int groupID,
//    @Query("sort") String sort);
}

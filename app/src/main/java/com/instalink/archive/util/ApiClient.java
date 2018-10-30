package com.instalink.archive.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.instalink.archive.model.MSG;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Shaon on 11/7/2016.
 */

public class ApiClient {

    public static final String BASE_URL = "http://website.com";
    public static final String INSTA_BASE = "https://api.instagram.com/";
    private static Retrofit retrofit = null;
    private static Retrofit retrofitInsta = null;



    public static Retrofit getClient() {
        if (retrofit==null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getInstaUser(){
        if (retrofitInsta==null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();


            retrofitInsta = new Retrofit.Builder()
                    .baseUrl(INSTA_BASE)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofitInsta;
    }
}


package com.instalink.archive.util;

import com.instalink.archive.model.InstaModel;
import com.instalink.archive.model.MSG;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {


    @FormUrlEncoded
    @POST("view/Signup.php")
    Call<MSG> userSignUp(@Field("email") String email,
                         @Field("password") String password);

    @FormUrlEncoded
    @POST("view/Login.php")
    Call<MSG> userLogIn(@Field("email") String email,
                        @Field("password") String password);

    @FormUrlEncoded
    @POST("view/Recovery.php")
    Call<MSG> passRecovery(@Field("email") String email);



}

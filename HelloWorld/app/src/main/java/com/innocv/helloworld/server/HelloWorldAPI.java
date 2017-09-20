package com.innocv.helloworld.server;

import com.innocv.helloworld.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 *
 * Hello World API interface
 *
 * @author Javier Fernández Riolobos
 * @version 1.0
 * @date 20/09/2017
 */
public interface HelloWorldAPI {

    @GET("getall")
    Call<List<User>> getAll();

    @GET("get/{id}")
    Call<User> get(@Path("id") int id);

    @POST("create")
    Call<User> create(@Body User user);

    @POST("update")
    Call<User> update(@Body User user);

    @GET("remove/{id}")
    Call<Void> remove(@Path("id") int id);

}

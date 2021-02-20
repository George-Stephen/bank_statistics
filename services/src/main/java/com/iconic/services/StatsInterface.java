package com.iconic.services;

import com.iconic.services.models.Branch;
import com.iconic.services.models.Country;
import com.iconic.services.models.Issue;
import com.iconic.services.models.Manager;
import com.iconic.services.models.Order;
import com.iconic.services.models.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface StatsInterface {
    @FormUrlEncoded
    @POST("api/manager/new")
    Call<Manager> add_manager(@Field("full_name") String full_name, @Field("phone_number") String phone_number, @Field("email_address") String email_address,@Field("password") String password);

    @GET("api/managers")
    Call<List<Manager>> get_manager(@Query("email_address") String email_address,@Query("password") String password);

    @GET("api/countries")
    Call<List<Country>> get_country();

    @GET("api/branches")
    Call<List<Branch>> get_branch();

    @GET("api/branches")
    Call<List<Branch>> get_branch_code(@Query("branch_name") String branch_name);

    @GET("api/cards")
    Call<List<Product>> get_product(@Query("product_code") String product_code );

    @GET("api/issues")
    Call<List<Issue>> get_issue(@Query("order_ref") String order_ref);

    @GET("api/orders")
    Call<List<Order>> get_issued_orders(@Query("branch_code") String branch_code,@Query("country_code") String country_code,@Query("status") String status);

    @GET("api/orders")
    Call<List<Order>> get_orders(@Query("branch_code") String branch_code,@Query("country_code") String country_code);

    @GET("api/issues")
    Call<List<Issue>> get_issues(@Query("branch_code") String branch_code,@Query("country_code") String country_code);

    @GET("api/orders")
    Call<List<Order>> get_order(@Query("product_code") String product_code);
}

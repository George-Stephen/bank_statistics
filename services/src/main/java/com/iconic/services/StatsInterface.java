package com.iconic.services;

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

    @GET("api/orders")
    Call<List<Order>> get_order( @Query("product_code") String product_code,@Query("country_code") String country_code);

    @GET("api/issues")
    Call<List<Issue>> get_issues(@Query("product_code") String product_code);

    @GET("api/cards")
    Call<List<Product>> get_cards();

    @GET("api/cards")
    Call<List<Product>> get_card(@Query("product_name") String product_name);

    @FormUrlEncoded
    @POST("api/manager/new")
    Call<Manager> add_manager(@Field("full_name") String full_name,@Field("phone_number") String phone_number,@Field("email_address") String email_address,@Field("card_center") String card_center);
}

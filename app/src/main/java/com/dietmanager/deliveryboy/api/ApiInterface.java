package com.dietmanager.deliveryboy.api;

/**
 * Created by santhosh@appoets.com on 03-10-17.
 */

import com.dietmanager.deliveryboy.model.Message;
import com.dietmanager.deliveryboy.model.Notice;
import com.dietmanager.deliveryboy.model.Order;
import com.dietmanager.deliveryboy.model.Profile;
import com.dietmanager.deliveryboy.model.RegisterResponse;
import com.dietmanager.deliveryboy.model.Shift;
import com.dietmanager.deliveryboy.model.Token;
import com.dietmanager.deliveryboy.model.Otp;
import com.dietmanager.deliveryboy.model.Vehicle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiInterface {


    @GET("api/chef/profile")
    Call<Profile> getProfile(@Header("Authorization") String authorization, @QueryMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/chef/register/otp")
    Call<Otp> postOtp(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/chef/register")
    Call<RegisterResponse> postRegister(@FieldMap HashMap<String, String> params);


    @FormUrlEncoded
    @POST("api/chef/profile")
    Call<Profile> updateProfile(@Header("Authorization") String authorization, @FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/chef/profile/location")
    Call<Profile> updateLocation(@Header("Authorization") String authorization, @FieldMap HashMap<String, Double> params);

    @Multipart
    @POST("api/chef/profile")
    Call<Profile> updateProfileWithImage(@Header("Authorization") String authorization, @PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part filename);

    @FormUrlEncoded
    @POST("api/chef/login")
    Call<Otp> getOtp(@Field("phone") String mobile);

    @GET("api/chef/logout")
    Call<Message> logout(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("api/chef/verify/otp")
    Call<Token> postLogin(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/chef/oauth/token")
    Call<Profile> login(@FieldMap HashMap<String, String> params);

    @GET("api/chef/shift")
    Call<List<Shift>> getShift(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("api/chef/shift")
    Call<List<Shift>> shiftStart(@Header("Authorization") String authorization, @FieldMap HashMap<String, String> params);

    @DELETE("api/chef/shift/{id}")
    Call<List<Shift>> shiftEnd(@Header("Authorization") String authorization, @Path("id") int shiftId);

    @FormUrlEncoded
    @POST("api/chef/shift/timing")
    Call<List<Shift>> shiftBreakStart(@Header("Authorization") String authorization, @FieldMap HashMap<String, String> params);

    @DELETE("api/chef/shift/timing/{id}")
    Call<List<Shift>> shiftBreakEnd(@Header("Authorization") String authorization, @Path("id") int breakId);

    @GET("api/chef/vehicles")
    Call<List<Vehicle>> getVehicles(@Header("Authorization") String authorization);

    @GET("api/chef/order")
    Call<List<Order>> getOrder(@Header("Authorization") String authorization);

    @GET("api/chef/history")
    Call<List<Order>> getCompletedOrder(@Header("Authorization") String authorization, @Query("type") String type);

    @FormUrlEncoded
    @PATCH("api/chef/order/{id}")
    Call<Order> updateStatus(@Path("id") int id, @FieldMap HashMap<String, String> params, @Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("api/chef/rating")
    Call<Message> rateUser(@Header("Authorization") String authorization, @FieldMap HashMap<String, String> params);

    @GET("api/chef/notice")
    Call<List<Notice>> getNoticeBoard(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("api/chef/request/order")
    Call<Order> acceptRequest(@Header("Authorization") String authorization, @FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/chef/request/order")
    Call<Message> rejectRequest(@Header("Authorization") String authorization, @FieldMap HashMap<String, String> params);
}

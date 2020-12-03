package com.dietmanager.chef.api;

/**
 * Created by santhosh@appoets.com on 03-10-17.
 */

import com.dietmanager.chef.model.ForgotPasswordResponse;
import com.dietmanager.chef.model.Message;
import com.dietmanager.chef.model.Notice;
import com.dietmanager.chef.model.Order;
import com.dietmanager.chef.model.Profile;
import com.dietmanager.chef.model.RegisterResponse;
import com.dietmanager.chef.model.Shift;
import com.dietmanager.chef.model.Token;
import com.dietmanager.chef.model.Otp;
import com.dietmanager.chef.model.Vehicle;
import com.dietmanager.chef.model.WalletHistory;
import com.dietmanager.chef.model.orderrequest.OrderRequestItem;
import com.dietmanager.chef.model.orderrequest.OrderRequestResponse;

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
    @POST("api/chef/chat/push")
    Call<Object> chatPost(@FieldMap HashMap<String, String> paramss);

    @GET("api/chef/wallet/transaction")
    Call<List<WalletHistory>> getWalletHistory(@Header("Authorization") String authorization);


    @FormUrlEncoded
    @POST("api/chef/order/{id}")
    Call<OrderRequestItem> updateOrder(@Header("Authorization") String authorization, @Path("id") int id,@FieldMap HashMap<String, String> params);


    @Multipart
    @POST("api/chef/order/{id}")
    Call<OrderRequestItem> updateOrderWithImage(@Header("Authorization") String authorization, @Path("id") int id, @PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part filename);

    @FormUrlEncoded
    @POST("api/chef/forgot/password")
    Call<ForgotPasswordResponse> forgotPassword(@FieldMap HashMap<String, String> params);

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

    @GET("api/chef/incoming/order")
    Call<OrderRequestResponse> getIncomingRequest(@Header("Authorization") String authorization, @Query("latitude") double latitude, @Query("longitude") double longitude, @Query("address") String address);

    @GET("api/chef/order/{id}")
    Call<OrderRequestItem> getOrderDetailById(@Header("Authorization") String authorization,@Path("id")int id);

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

    @GET("api/chef/order")
    Call<List<OrderRequestItem>> getHistory(@Header("Authorization") String authorization,@Query("status") String status);
}

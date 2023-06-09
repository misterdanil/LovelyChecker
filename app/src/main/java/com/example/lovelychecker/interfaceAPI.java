package com.example.lovelychecker;

import com.example.lovelychecker.tovar.Magazins;
import com.example.lovelychecker.tovar.Product;
import com.example.lovelychecker.tovar.ResourceReview;
import com.example.lovelychecker.tovar.ReviewRequest;
import com.example.lovelychecker.tovar.ReviewResponse;

import java.util.List;

import javax.xml.transform.Result;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Header;
import retrofit2.Call;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface interfaceAPI {

    @POST("/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("/signupf")
    Call<LoginResponse> signUp(@Body SignupRequest signupRequest);

    @POST("/accountVerification/{confirmToken}")
    Call<Post> confirm(@Path(value = "confirmToken") String confirmToken);

    @GET
    Call<LoginResponse> finishOAuth2(@Url String url, @Header("Cookie") String jsessionId);

    @GET("/product/smartphones")
    Call<List<Product>> getProducts(@Query(value = "text") String text, @Query("brands") List<String> brands,
                                    @Query("rams") List<String> rams, @Query("sort") String sort,
                                    @Query("fromPrice") Double fromPrice, @Query("toPrice") Double toPrice);

    @GET("/product/smartphones")
    Call<List<Product>> getProducts(@Query(value = "text") String text, @Query("brands") List<String> brands,
                                    @Query("rams") List<String> rams, @Query("sort") String sort,
                                    @Query("fromPrice") Double fromPrice, @Query("toPrice") Double toPrice,
                                    @Query("start") Integer skip, @Query("limit") Integer limit);

    @GET("/product/smartphone/{id}")
    Call<Product> getProduct(@Path("id") String id);

    @POST("/product/smartphone/{id}/review")
    Call<Void> saveReview(@Path("id") String id, @Body ReviewRequest reviewRequest, @Header("Authorization") String accessToken);

    @POST("/product/smartphone/{id}/review/{reviewId}/like")
    Call<Void> like(@Path("id") String id, @Path("reviewId") String reviewId, @Header("Authorization") String token);

    @POST("/product/smartphone/{id}/review/{reviewId}/dislike")
    Call<Void> dislike(@Path("id") String id, @Path("reviewId") String reviewId, @Header("Authorization") String token);

    @GET("/product/smartphone/{id}/reviews")
    Call<List<ReviewResponse>> getReviews(@Path("id") String id);

    @GET("/product/smartphone/{productId}/user/{userId}/review")
    Call<ReviewResponse> getReview(@Path("productId") String productId, @Path("userId")String userId);

    @GET("/user/reviews")
    Call<List<ReviewResponse>> getOwnReviews(@Header("Authorization") String token);

    @GET("/product/smartphone/{id}/resources")
    Call<List<Magazins>> getResources(@Path("id") String id);


    @GET("/chat/{chatId}/messages")
    Call<List<Message>> getMessages(@Path(value = "chatId") String chatId);

    @Multipart
    @POST("/chat/{chatId}/message")
    Call<Void> sendMessage(@Path(value = "chatId") String chatId, @Part MultipartBody.Part text, @Part(value = "voice") RequestBody voice, @Header(value = "Authorization") String accessToken);

    @GET("/chats")
    Call<List<Chat>> getChats(@Header(value = "Authorization") String token);

    @POST("/chat")
    Call<ResponseBody> createChat(@Header(value = "Authorization") String token, @Body ChatRequest chat);

    @GET("/product/smartphone/{id}/resources/{type}/reviews")
    Call<List<ResourceReview>> getResourcesReviews(@Path("id") String id, @Path("type")String type);

    @GET("/chats/{id}")
    Call<Chat> getChat(@Path("id") String id);
    @GET("login/oauth2/{service}")
    Call<Void> oauth2(@Path(value="service") String service);
//    @GET("/somewhere")
//    Call<Post> something(String something);

    @PUT("/user/username")
    Call<ResponseBody> updateUsername(@Body UpdateUsernameRequest usernameRequest, @Header("Authorization") String token);

    @Multipart
    @PUT("/user/avatar")
    Call<Void> updateAvatar(@Part MultipartBody.Part image, @Header("Authorization") String token);
}

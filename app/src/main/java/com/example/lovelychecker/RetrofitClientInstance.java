package com.example.lovelychecker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

private static Retrofit retrofit = null;
    public static final String BASE_URL = "http://10.131.60.207:8080";
    public static final String SOCKET_URL = "ws://10.131.60.207:8080";
    public static String JSESSION_ID;

    public static String USER_ID = null;

    public static String USERNAME = null;
    public static String ACCESS_TOKEN = null;


    public static interfaceAPI getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(interfaceAPI.class);
    }
}

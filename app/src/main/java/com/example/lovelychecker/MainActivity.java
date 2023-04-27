package com.example.lovelychecker;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lovelychecker.tovar.Tovar_Activity;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;


    private Button login_btn;
    private EditText emailTxt;
    private EditText passTxt;
    private String email, password;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        drawerLayout = findViewById(R.id.drawer_layout);
//
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
//        drawerLayout.addDrawerListener(toggle);
//        toggle.syncState();

//        if(savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//            navigationView.setCheckedItem(R.id.for_home);
//        }


        View login_button_header;
//        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header);
//        login_button_header = headerLayout.findViewById(R.id.login_button_header);

//        login_button_header.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Fragment fragment = new LoginFragment();
//                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace((R.id.fragment_container), fragment).commit();
//
//            }
//        });

        if(RetrofitClientInstance.ACCESS_TOKEN == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            LoginFragment loginFragment = new LoginFragment();
            fragmentTransaction.replace(R.id.fragment_container, loginFragment);
            fragmentTransaction.commit();
        }
        else {
            Intent intent = new Intent(this, Tovar_Activity.class);
            startActivity(intent);
        }

//        login_btn = loginFragment.getView().findViewById(R.id.login_button);
//        emailTxt = loginFragment.getView().findViewById(R.id.login_email);
//        passTxt = loginFragment.getView().findViewById(R.id.login_password);



//        login_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                email = emailTxt.getText().toString();
//                password = passTxt.getText().toString();
//
//                String authToken = createAuthToken(email, password);
//                checkLoginDetails(authToken);
//            }
//        });

    }
//    private void checkLoginDetails(String authToken) {
//        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
//        final interfaceAPI api = retrofit.create(interfaceAPI.class);
//
//        Call<String> call = api.checkLogin(authToken);
//
//        call.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                if(response.isSuccessful()) {
//                    if(response.body().matches("success")) {
//                        Toast.makeText(getApplicationContext(), "Successfuly logged in!", Toast.LENGTH_LONG).show();
//                    }
//                    else {
//                        Toast.makeText(getApplicationContext(), "Invalid credentials.", Toast.LENGTH_LONG).show();
//                    }
//                }
//                else {
//                    // handle error
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Log.e("TAG", t.toString());
//                t.printStackTrace();
//            }
//        });
//    }

//    private String createAuthToken(String email, String password) {
//        byte [] data = new byte[0];
//        try {
//            data = (email + ": " + password).getBytes("UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.for_home:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//                break;
//            case R.id.nav_settings:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
//                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Object o = getIntent();
        Uri uri = getIntent().getData();

        if(uri != null && uri.getQueryParameter("code") != null) {
            String path = uri.getEncodedPath();
            String fragment = uri.getFragment();
            String query = uri.getEncodedQuery();
            String uri2 = uri.toString();

            String request = path + "?" + query;
            interfaceAPI apiService = RetrofitClientInstance.getInstance();

            Call<LoginResponse> call = apiService.finishOAuth2(request, RetrofitClientInstance.JSESSION_ID);

            Context context = this;

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                    if (response.isSuccessful()) {
                        LoginResponse userInfo = response.body();

                        RetrofitClientInstance.USER_ID = userInfo.getId();
                        RetrofitClientInstance.USERNAME = userInfo.getUsername();
                        RetrofitClientInstance.ACCESS_TOKEN = userInfo.getAccessToken();

                        Intent intent = new Intent(context, Tovar_Activity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        System.out.println("No");
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    System.out.println();
                }
            });
            System.out.println();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
package com.example.lovelychecker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lovelychecker.tovar.Tovar_Activity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginFragment extends Fragment {

    private EditText loginEmail;
    private EditText loginPassword;
    private Button loginButton;
    private LinearLayout googleButton;
    private LinearLayout vkButton;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginEmail = view.findViewById(R.id.login_email);
        loginPassword = view.findViewById(R.id.login_password);
        loginButton = view.findViewById(R.id.login_button);
        googleButton = view.findViewById(R.id.google);
        vkButton = view.findViewById(R.id.vk);

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceRedirect("google");
            }
        });
        vkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serviceRedirect("vk");
            }
        });

        //КНОПКА LOGIN
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });


        //ПЕРЕХОД НА SIGNUP
        TextView signUpTextView = view.findViewById(R.id.signupTextView);
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment signUpFragment = new SignupFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, signUpFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void loginUser() {
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();

        interfaceAPI apiService = RetrofitClientInstance.getInstance();
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.loginUser(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "On response: Success " + response.code(), Toast.LENGTH_LONG).show();
                    LoginResponse userInfo = response.body();
                    loginEmail.setText("");
                    loginPassword.setText("");

                    RetrofitClientInstance.USER_ID = userInfo.getId();
                    RetrofitClientInstance.USERNAME = userInfo.getUsername();
                    RetrofitClientInstance.ACCESS_TOKEN = userInfo.getAccessToken();

                    Intent intent = new Intent(getContext(), Tovar_Activity.class);
                    startActivity(intent);
                    getActivity().finish();

                } else {
                    // Обработка ошибки сервера
                    try {

                        String body = response.errorBody().string();
                        System.out.println(body);
                        JsonNode node = objectMapper.readValue(body, JsonNode.class);
                        String emailError = node.get("body").get("fieldErrors").path("email").asText(null);
                        String passwordError = node.get("body").get("fieldErrors").path("password").asText(null);

                        if(emailError != null)  {
                            Toast.makeText(getActivity(), emailError, Toast.LENGTH_LONG).show();
                        }
                        else if (passwordError != null) {
                            Toast.makeText(getActivity(), passwordError, Toast.LENGTH_LONG).show();
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //Toast.makeText(getActivity(), "On response: Failure " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Обработка ошибки сети или других ошибок
                Toast.makeText(getActivity(), "On Failure: Faiure", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void serviceRedirect(String service) {
        interfaceAPI apiService = RetrofitClientInstance.getInstance();

        Request request = new Request.Builder().url(RetrofitClientInstance.BASE_URL + "/login/oauth2/" + service).build();
        OkHttpClient client = new OkHttpClient.Builder().followRedirects(false).build();
        okhttp3.Call call = client.newCall(request);

        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                List<String> cookies = response.headers().values("Set-Cookie");
                if(!cookies.isEmpty()) {
                    RetrofitClientInstance.JSESSION_ID = (cookies.get(0).split(";"))[0];
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.headers().get("Location")));
                startActivity(browserIntent);
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {

            }
        });
    }
}
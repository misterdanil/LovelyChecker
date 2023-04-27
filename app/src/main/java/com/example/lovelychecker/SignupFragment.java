package com.example.lovelychecker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.w3c.dom.Text;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignupFragment extends Fragment {

    private Button signupButton;
    private EditText signupUsername;
    private EditText signupEmail;
    private EditText signupPassword;

    private ObjectMapper objectMapper = new ObjectMapper();
    private TextView usernameTextView;
    private TextView emailTextView;
    private TextView passwordTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        signupButton = view.findViewById(R.id.signup_button);
        signupUsername = view.findViewById(R.id.signup_username);
        signupEmail = view.findViewById(R.id.signup_email);
        signupPassword = view.findViewById(R.id.signup_password);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        passwordTextView = view.findViewById(R.id.passwordTextView);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });



        //ПЕРЕХОД НА LOGIN
        TextView signInTextView = view.findViewById(R.id.loginTextView);
        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment loginFragment = new LoginFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, loginFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;

    }

    private void signUp() {
        String username = signupUsername.getText().toString();
        String email = signupEmail.getText().toString();
        String password = signupPassword.getText().toString();

        interfaceAPI apiService = RetrofitClientInstance.getInstance();
        SignupRequest signupRequest = new SignupRequest(username, email, password);
        Call<LoginResponse> call = apiService.signUp(signupRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse userInfo = response.body();

                    RetrofitClientInstance.USER_ID = userInfo.getId();
                    RetrofitClientInstance.USERNAME = userInfo.getUsername();
                    RetrofitClientInstance.ACCESS_TOKEN = userInfo.getAccessToken();

                    Fragment loginFragment = new ConfirmFragment();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, loginFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    // Обработка ошибки сервера
                    try {

                        String body = response.errorBody().string();
                        System.out.println(body);
                        JsonNode node = objectMapper.readValue(body, JsonNode.class);
                        String usernameError = node.get("body").get("fieldErrors").path("username").asText(null);
                        String emailError = node.get("body").get("fieldErrors").path("email").asText(null);
                        String passwordError = node.get("body").get("fieldErrors").path("password").asText(null);

                        if(usernameError != null) {
                            usernameTextView.setText(usernameError);
                        }
                        if(emailError != null)  {
                            emailTextView.setText(emailError);
                        }
                        if (passwordError != null) {
                            passwordTextView.setText(passwordError);
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    //Toast.makeText(getActivity(), "On response: Failure " + response.code(), Toast.LENGTH_LONG).show();
                }

            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "On Failure: Failure", Toast.LENGTH_LONG).show();

            }
        });
    }
}
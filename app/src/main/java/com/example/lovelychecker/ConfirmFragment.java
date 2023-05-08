package com.example.lovelychecker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lovelychecker.tovar.Tovar_Activity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ConfirmFragment extends Fragment {

    private EditText confirm_text;
    private Button confirm_button;
    private ObjectMapper mapper = new ObjectMapper();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm, container, false);

        confirm_text = view.findViewById(R.id.login_confirm);
        confirm_button = view.findViewById(R.id.confirm_button);

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        return view;
    }

    private void confirm() {
        String confirm_code = confirm_text.getText().toString();

        interfaceAPI apiService = RetrofitClientInstance.getInstance();

        Call<Post> call = apiService.confirm(confirm_code);

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(getContext(), Tovar_Activity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    String body = null;
                    try {
                        body = response.errorBody().string();
                        System.out.println(body);
                        JsonNode node = mapper.readValue(body, JsonNode.class);
                        String error = node.get("body").get("fieldErrors").path("token").asText(null);

                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                System.out.println("fail");
            }
        });

    }
}
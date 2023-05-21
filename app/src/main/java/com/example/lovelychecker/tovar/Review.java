package com.example.lovelychecker.tovar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lovelychecker.R;
import com.example.lovelychecker.RetrofitClientInstance;
import com.example.lovelychecker.interfaceAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Review extends AppCompatActivity {

    private String id;
    Button Makereview;
    EditText first, second;
    RatingBar rating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("id");
        setContentView(R.layout.activity_review);
        Makereview = (Button) findViewById(R.id.MakeReview);
        first = findViewById(R.id.first);
        second = findViewById(R.id.second);
        rating = findViewById(R.id.rating);

        interfaceAPI interfaceApi = RetrofitClientInstance.getInstance();
        Call<ReviewResponse> callReview = interfaceApi.getReview(id, RetrofitClientInstance.USER_ID);
        callReview.enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if(response.isSuccessful()) {
                    ReviewResponse review = response.body();
                    rating.setRating(review.getRating());
                    first.setText(review.getTitle());
                    second.setText(review.getText());
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {

            }
        });
        Makereview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String zag = String.valueOf(first.getText());//Заголовок
                String opis = String.valueOf(second.getText());//Описание
                float rat = rating.getRating();//Оценка

                ReviewRequest reviewRequest = new ReviewRequest();
                reviewRequest.setTitle(zag);
                reviewRequest.setText(opis);
                reviewRequest.setRating((int)rat);

                interfaceAPI interfaceApi = RetrofitClientInstance.getInstance();
                Call<Void> call = interfaceApi.saveReview(id, reviewRequest, "Bearer " + RetrofitClientInstance.ACCESS_TOKEN);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.isSuccessful()) {
                            Intent intent = new Intent(Review.this, ItemScreen.class);
                            intent.putExtra("id", id);
                            startActivity(intent);
                            finish();
                        }
                        else {

                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        System.err.println("fail");
                    }
                });
            }
        });
    }
}
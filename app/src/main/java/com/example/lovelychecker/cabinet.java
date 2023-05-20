package com.example.lovelychecker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lovelychecker.tovar.ProductAdapter;
import com.example.lovelychecker.tovar.ReviewAdapter;
import com.example.lovelychecker.tovar.ReviewResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class cabinet extends AppCompatActivity {

    private Button change_name, change_photo;
    private TextView txtname;

    private ImageView imageView;

    private static final int PICK_FROM_GALLERY = 1;
    private RecyclerView list;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cabinet);
        imageView = (ImageView) findViewById(R.id.imageView2);
        change_name = (Button) findViewById(R.id.nameChange);
        change_photo = (Button) findViewById(R.id.change_ph);
        txtname = (TextView) findViewById(R.id.uName);//Cюда имя пользователя
        txtname.setText(RetrofitClientInstance.USERNAME);
        list = (RecyclerView) findViewById(R.id.revList); //Сюда выводи отзывы

        findViewById(R.id.arrow_icon).setOnClickListener(e -> {
            finish();
        });

        Context context = this;

        change_name.setOnClickListener(new View.OnClickListener() {
            private EditText newName;

            @Override
            public void onClick(View view) {
                newName = (EditText) findViewById(R.id.editName);
                if (newName.getVisibility() == View.GONE) {
                    newName.setVisibility(View.VISIBLE);
                    change_name.setText("Сохранить");
                } else {
                    interfaceAPI apiService = RetrofitClientInstance.getInstance();
                    UpdateUsernameRequest usernameRequest = new UpdateUsernameRequest();
                    usernameRequest.setUsername(newName.getText().toString());
                    Call<ResponseBody> call = apiService.updateUsername(usernameRequest,
                            "Bearer " + RetrofitClientInstance.ACCESS_TOKEN);
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(!response.isSuccessful()) {
                                ObjectMapper mapper = new ObjectMapper();
                                JsonNode node;
                                try {
                                    node = mapper.readTree(response.errorBody().string());
                                } catch (IOException e) {
                                    throw new RuntimeException("Couldn't transform response string to json node", e);
                                }
                                if(node.has("body")) {
                                    String error = node.get("body").get("fieldErrors").get("username").asText();
                                    Snackbar.make(newName, error, Snackbar.LENGTH_LONG)
                                            .show();
                                }
                            }
                            else {
                                txtname.setText(newName.getText());
                                RetrofitClientInstance.USERNAME = newName.getText().toString();
                                newName.setVisibility(View.GONE);
                                change_name.setText("Изменить имя");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });


                }
            }
        });
        change_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

//        ReviewResponse resp1 = new ReviewResponse();
//        resp1.setRating(4);
//        resp1.setUserId("test");
//        resp1.setTitle("Отличный самртфон");
//        resp1.setText("Ну вот такое себе");
//        resp1.setUserId("james_darmody");
//        ReviewResponse resp2 = new ReviewResponse();
//        resp2.setRating(4);
//        resp2.setUserId("test");
//        resp2.setTitle("Отличный самртфон");
//        resp2.setText("Ну вот такое себе");
//        resp2.setUsername("james_darmody");

//        setReviewRecylder(Arrays.asList(resp1, resp2)); //где Лист это отзывы с нашего сайта

        interfaceAPI interfaceApi = RetrofitClientInstance.getInstance();
        Call<List<ReviewResponse>> call = interfaceApi.getOwnReviews("Bearer " + RetrofitClientInstance.ACCESS_TOKEN);

        call.enqueue(new Callback<List<ReviewResponse>>() {
            @Override
            public void onResponse(Call<List<ReviewResponse>> call, Response<List<ReviewResponse>> response) {
                if(response.isSuccessful()) {
                    List<ReviewResponse> reviews = response.body();
                    setReviewRecylder(reviews);
                }
            }

            @Override
            public void onFailure(Call<List<ReviewResponse>> call, Throwable t) {
                System.err.println("fail");
            }
        });

        new ImageBitmapUriTask(this, imageView).execute(RetrofitClientInstance.BASE_URL + "/user/avatar");

    }

    public static class ImageBitmapUriTask extends AsyncTask<String, Void, Void> {

        private ImageView imageView;
        private Activity activity;

        public ImageBitmapUriTask(Activity acitivty, ImageView imageView) {
            this.activity = acitivty;
            this.imageView = imageView;
        }

        @Override
        protected Void doInBackground(String... params) {

            Bitmap myBitmap = null;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", "Bearer " + RetrofitClientInstance.ACCESS_TOKEN);
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);
            }
            catch(FileNotFoundException e) {
                Drawable drawable = ResourcesCompat.getDrawable(activity.getResources(),
                        R.drawable.baseline_person_24, activity.getTheme());
                if (drawable instanceof BitmapDrawable) {
                    myBitmap = ((BitmapDrawable)drawable).getBitmap();
                }
                else {
                    myBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(myBitmap);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            activity.runOnUiThread(new ProductAdapter.SetImageTask(imageView, myBitmap));
            return null;

        }
    }

    private void setReviewRecylder(List<ReviewResponse> item) {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        RecyclerView reviewList = findViewById(R.id.revList);
        reviewList.setLayoutManager(layoutManager);

        ReviewAdapter reviewAdapter = new ReviewAdapter(this, this, item);
        reviewList.setAdapter(reviewAdapter);
    }
    private void imageChooser()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }

    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        try {
                            Bitmap selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            selectedImageBitmap.recycle();
                        RequestBody requestFile =
                                RequestBody.create(
                                        MediaType.parse(getContentResolver().getType(selectedImageUri)),
                                        byteArray
                                );

                        // MultipartBody.Part is used to send also the actual file name
                        MultipartBody.Part body =
                                MultipartBody.Part.createFormData("avatar", "avatar", requestFile);

                        interfaceAPI apiService = RetrofitClientInstance.getInstance();
                        Call<Void> call = apiService.updateAvatar(body,
                                "Bearer " + RetrofitClientInstance.ACCESS_TOKEN);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.isSuccessful()) {
                                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
                                }
                                else {
                                    System.out.println("Error: " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                System.out.println("fail to save image");
                            }
                        });
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
}
package com.example.lovelychecker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelychecker.tovar.Product;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private static final String TAG = "MainActivity";
    RecyclerView prodList;
    MessageAdapter messageAdapter;

    ImageButton find;
    EditText finder;

    public static boolean IS_FROM_REVIEWS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_window);

        interfaceAPI apiService = RetrofitClientInstance.getInstance();

        Activity activity = this;

        String chatId = getIntent().getExtras().getString("id");
        String productId = getIntent().getExtras().getString("productId");

        findViewById(R.id.back_arrow).setOnClickListener(e -> {
            if(IS_FROM_REVIEWS) {
                IS_FROM_REVIEWS = false;
                finish();
                Intent intent = new Intent(this, ChatsFragment.class);
                startActivity(intent);
            }
            else {
                finish();
            }
        });

        Call<Product> callProduct = apiService.getProduct(productId);
        callProduct.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Product product = response.body();
                    ((TextView)findViewById(R.id.chat_image_productName)).setText(product.getTitle());
                    new ImageBitmapUriTask(activity, findViewById(R.id.product_image)).execute(RetrofitClientInstance.BASE_URL + "/" + "product/smartphones/" + product.getId() + "/image");
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {

            }
        });


        View view = findViewById(R.id.button_gchat_send);
        view.setOnClickListener((e) -> {
            EditText messageEditText = (EditText)findViewById(R.id.edit_gchat_message);

            String text = messageEditText.getText().toString();

            Call<Void> call = apiService.sendMessage(chatId, MultipartBody.Part.createFormData("text", text), null, RetrofitClientInstance.ACCESS_TOKEN);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()) {
                        messageEditText.setText("");
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Возникла ошибка, попробуйте позже",
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Не удалось отправить сообщение, " +
                            "проверьте соединение с интернетом", Toast.LENGTH_LONG).show();
                }
            });
        });

        List<Message> item = new ArrayList<>();

//        Message message = new Message();
//        message.setText("Xiaomi note 5");
//        message.setUser(new User("6435cd643572c97e7243c5cb", "John", "Cena", "Star"));
//        item.add(message);
//
//        Message message1 = new Message();
//        message1.setText("Xiaomi note 5");
//        message1.setUser(new User("6435cd043572c97e7243c5c9", "John", "Cena", "Star"));
//        item.add(message1);
//
//        Message message3 = new Message();
//        message3.setText("Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5" +
//                "Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5" +
//                "Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5" +
//                "Xiaomi note 5Xiaomi note 5");
//        message3.setUser(new User("6435cd643572c97e7243c5cb", "John", "Cena", "Star"));
//        item.add(message3);
//
//        Message message4 = new Message();
//        message4.setText("Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5" +
//                "Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5" +
//                "Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5" +
//                "Xiaomi note 5Xiaomi note 5");
//        message4.setUser(new User("6435cd643572c97e7243c5cb", "John", "Cena", "Star"));
//        item.add(message4);
//
//        Message message5 = new Message();
//        message5.setText("Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5" +
//                "Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5" +
//                "Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5" +
//                "Xiaomi note 5Xiaomi note 5");
//        message5.setUser(new User("6435cd043572c97e7243c5c9", "John", "Cena", "Star"));
//        item.add(message5);
//
//        Message message6 = new Message();
//        message6.setText("Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5" +
//                "Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5" +
//                "Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5Xiaomi note 5" +
//                "Xiaomi note 5Xiaomi note 5");
//        message6.setUser(new User("6435cd043572c97e7243c5c9", "John", "Cena", "Star"));
//        item.add(message6);
//
//        setProductRecylder(item);

        Call<List<Message>> call = apiService.getMessages(chatId);
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if(response.isSuccessful()) {
                    List<Message> messages = response.body();
                    messages.forEach(message2 -> {
                        item.add(message2);
                    });

                    setProductRecylder(item, chatId);

                }
                else {
                    System.out.println(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                System.err.println("fail");
            }
        });
    }

    private void setProductRecylder(List<Message> item, String chatId) {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        prodList = findViewById(R.id.prodList);
        prodList.setLayoutManager(layoutManager);

        messageAdapter = new MessageAdapter(this, item, prodList);

        messageAdapter.new RetrieveFeedTask(this, messageAdapter).execute(chatId);

        prodList.setAdapter(messageAdapter);

        prodList.scrollToPosition(item.size() - 1);
    }

    public void find(View view){
        String text = finder.getText().toString();
        //Вставить функцию поиска через сервер
    }
    public static class SetImageTask implements Runnable {
        private ImageView imageView;
        private Bitmap bitmap;
        public SetImageTask(ImageView imageView, Bitmap bitmap) {
            this.imageView = imageView;
            this.bitmap = bitmap;
        }

        @Override
        public void run() {
            imageView.setImageBitmap(bitmap);
        }
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

            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                activity.runOnUiThread(new SetImageTask(imageView, myBitmap));
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    }
}

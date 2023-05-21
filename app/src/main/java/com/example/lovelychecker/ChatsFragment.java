package com.example.lovelychecker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelychecker.tovar.Product;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsFragment extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_chats);

        findViewById(R.id.arrow_icon).setOnClickListener(e -> {
            finish();
        });

        new cabinet.ImageBitmapUriTask(this, findViewById(R.id.account_icon)).execute(RetrofitClientInstance.BASE_URL + "/user/avatar");

        recyclerView = findViewById(R.id.chats_recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatList = new ArrayList<>();

        loadChats();
    }

    private void loadChats() {
        interfaceAPI apiInterface = RetrofitClientInstance.getInstance();
        Call<List<Chat>> call = apiInterface.getChats(RetrofitClientInstance.ACCESS_TOKEN);

        Context context = this;
        Activity activity = this;
        List<Chat> chats = new ArrayList<>();
//        chats.add(new Chat("1", new User("1", "Себастьян", "Бах", "SebBach"), new Message("1", "lol", new User("1", "Эксл", "Роуз", "AxlRrr")), new Product("3", "Смартфон Apple IPhone 13 Pro Max Dual Slim 256 GB FWAWADSD")));
//        chats.add(new Chat("1", new User("1", "Себастьян", "Бах", "SebBach"), new Message("1", "lol", new User("1", "Эксл", "Роуз", "AxlRrr")), new Product("3", "Смартфон Apple IPhone 13 Pro Max Dual Slim 256 GB FWAWADSD")));
//        chats.add(new Chat("1", new User("1", "Себастьян", "Бах", "SebBach"), new Message("1", "lol", new User("1", "Эксл", "Роуз", "AxlRrr")), new Product("3", "Смартфон Apple IPhone 13 Pro Max Dual Slim 256 GB FWAWADSD")));
//        chats.add(new Chat("1", new User("1", "Себастьян", "Бах", "SebBach"), new Message("1", "lol", new User("1", "Эксл", "Роуз", "AxlRrr")), new Product("3", "Смартфон Apple IPhone 13 Pro Max Dual Slim 256 GB FWAWADSD")));
//        chats.add(new Chat("1", new User("1", "Себастьян", "Бах", "SebBach"), new Message("1", "lol", new User("1", "Эксл", "Роуз", "AxlRrr")), new Product("3", "Смартфон Apple IPhone 13 Pro Max Dual Slim 256 GB FWAWADSD")));
//        chatAdapter = new ChatAdapter(getContext(), chats);
//        recyclerView.setAdapter(chatAdapter);
        call.enqueue(new Callback<List<Chat>>() {
            @Override
            public void onResponse(Call<List<Chat>> call, Response<List<Chat>> response) {
               if(response.isSuccessful()) {
                   chatList = response.body();
//                   chatList.add(new Chat("1", new User("1", "Себастьян", "Бах", "SebBach"), new Message("1", "Добрый день, да, фокусировка у фотоаппарата есть", new User("1", "Эксл", "Роуз", "AxlRrr"), new Date()), new Product("3", "Смартфон Apple IPhone 13 Pro Max Dual Slim 256 GB FWAWADSD Смартфон Apple IPhone 13 Pro Max Dual Slim 256 GB FWAWADSD Смартфон Apple IPhone 13 Pro Max Dual Slim 256 GB FWAWADSD Смартфон Apple IPhone 13 Pro Max Dual Slim 256 GB FWAWADSD Смартфон Apple IPhone 13 Pro Max Dual Slim 256 GB FWAWADSD")));
                   chatAdapter = new ChatAdapter(context, chatList, activity);
                   recyclerView.setAdapter(chatAdapter);
               }
               else {
                   System.err.println("Couldn't get chats - " + response.code());
               }
            }
            @Override
            public void onFailure(Call<List<Chat>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
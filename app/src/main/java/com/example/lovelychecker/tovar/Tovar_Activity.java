package com.example.lovelychecker.tovar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelychecker.ChatsFragment;
import com.example.lovelychecker.MainActivity;
import com.example.lovelychecker.R;
import com.example.lovelychecker.RetrofitClientInstance;
import com.example.lovelychecker.interfaceAPI;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tovar_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    Button filter;

    private String sort;

    private static final String TAG = "MainActivity";
    RecyclerView prodList;
    ProductAdapter productAdapter;

    ImageButton find;
    EditText finder;
    private DrawerLayout drawerLayout;

    private ArrayList<String> brands;
    private ArrayList<String> rams;
    private Double priceFrom;
    private Double priceTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_tovar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout = findViewById(R.id.drawer_layout);

        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header);
        ((TextView)headerView.findViewById(R.id.name)).setText(RetrofitClientInstance.USERNAME);

        finder = (EditText) findViewById(R.id.finder);
        find = (ImageButton) findViewById(R.id.find);
        filter = (Button) findViewById(R.id.filt);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Tovar_Activity.this, Fitler.class);
                intent.putStringArrayListExtra("brands", brands);
                intent.putStringArrayListExtra("rams", rams);
                if(priceFrom != null) {
                    intent.putExtra("priceFrom", priceFrom);
                }
                if(priceTo != null) {
                    intent.putExtra("priceTo", priceTo);
                }
                startActivity(intent);
            }
        });

        getProducts(false);
    }

    public void getProducts(boolean isSort) {
        List<Product> item = new ArrayList<>();

        Product product1 = new Product();
        product1.setTitle("Xiaomi note 5");
        product1.setFromPrice(15000.0);
//        item.add(product1);

        Product product2 = new Product();
        product2.setTitle("Xiaomi note 5");
        product2.setToPrice(15000.0);
//        item.add(product2);

        interfaceAPI apiService = RetrofitClientInstance.getInstance();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null && !isSort) {
            brands = bundle.getStringArrayList("brands");
            rams = bundle.getStringArrayList("rams");
            priceFrom = (Double)bundle.get("priceFrom");
            priceTo = (Double)bundle.get("priceTo");
        }

        String text = finder.getText().toString();
        Call<List<Product>> call = apiService.getProducts(text, brands, rams, sort, priceFrom, priceTo);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()) {
                    List<Product> products = response.body();
                    System.out.println(products);
                    products.forEach(product -> {
                        item.add(product);
                    });

                    setProductRecylder(item);
                }
                else {
                    System.out.println(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                System.err.println("fail");
            }
        });
    }

    private void setProductRecylder(List<Product> item) {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        prodList = findViewById(R.id.prodList);
        prodList.setLayoutManager(layoutManager);

        productAdapter = new ProductAdapter(this, this, item);
        prodList.setAdapter(productAdapter);
    }

    public void find(View view){
        getProducts(true);
    }

    public void sort(View view) {
        ToggleButton tb = (ToggleButton) view;

        if(tb.isChecked()) {
            sort = "CHEAP";
        }
        else {
            sort = "EXPENSIVE";
        }

        getProducts(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.for_home:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//                break;
            case R.id.nav_logout:
                RetrofitClientInstance.USER_ID = null;
                RetrofitClientInstance.USERNAME = null;
                RetrofitClientInstance.ACCESS_TOKEN = null;
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            case R.id.chats:
                Intent intent2 = new Intent(this, ChatsFragment.class);
                startActivity(intent2);
                finish();
        }

//        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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

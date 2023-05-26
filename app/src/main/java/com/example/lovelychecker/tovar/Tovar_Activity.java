package com.example.lovelychecker.tovar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import com.example.lovelychecker.PaginationScrollListener;
import com.example.lovelychecker.R;
import com.example.lovelychecker.RetrofitClientInstance;
import com.example.lovelychecker.cabinet;
import com.example.lovelychecker.interfaceAPI;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Tovar_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    Button filter;

    private String sort = "CHEAP";

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
    private ProgressBar progressBar;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private int limit_products = 25;

    private View headerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_tovar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        AppCompatActivity activity = this;

        new cabinet.ImageBitmapUriTask(activity, findViewById(R.id.account_icon)).execute(RetrofitClientInstance.BASE_URL + "/user/avatar");

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                new cabinet.ImageBitmapUriTask(activity, findViewById(R.id.menu_avatar)).execute(RetrofitClientInstance.BASE_URL + "/user/avatar");
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        progressBar = findViewById(R.id.progressbar);

        headerView = navigationView.inflateHeaderView(R.layout.nav_header);
        ((TextView)headerView.findViewById(R.id.name)).setText(RetrofitClientInstance.USERNAME);
        isLastPage = false;
        isLoading = false;
        currentPage = 1;

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
        Call<List<Product>> call = apiService.getProducts(text, brands, rams, sort, priceFrom, priceTo,
                (currentPage - 1) * limit_products, limit_products);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()) {
                    List<Product> products = response.body();
                    System.out.println(products);
//                    products.forEach(product -> {
//                        item.add(product);
//                    });
                    if(currentPage == 1) {
                        setProductRecylder(products);
                        progressBar.setVisibility(View.GONE);
                    }
                    else {
                        productAdapter.removeLoadingFooter();
                        productAdapter.addAll(products);
                    }

                    if(products.size() == limit_products) {
                        productAdapter.addLoadingFooter();
                    }
                    else {
                        isLastPage = true;
                    }

                    isLoading = false;
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
        prodList.addOnScrollListener(new PaginationScrollListener((LinearLayoutManager) layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                System.err.println("more");
                currentPage += 1;
                getProducts(true);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    public void find(View view){
        if(productAdapter != null) {
            productAdapter.removeLoadingFooter();
            productAdapter.removeAll();
        }
        isLastPage = false;
        isLoading = false;
        currentPage = 1;

        if(brands != null) {
            brands.clear();
        }
        if(rams != null) {
            rams.clear();
        }
        priceFrom = null;
        priceTo = null;

        getProducts(true);
    }

    public void sort(View view) {
        ToggleButton tb = (ToggleButton) view;

        if(tb.isChecked()) {
            sort = "EXPENSIVE";
        }
        else {
            sort = "CHEAP";
        }

        if(productAdapter != null) {
            productAdapter.removeAll();
        }
        isLastPage = false;
        isLoading = false;
        currentPage = 1;

        getProducts(true);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent currentIntent = getIntent();
        ArrayList<String> brandList = new ArrayList<>();

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
                break;
            case R.id.chats:
                Intent intent2 = new Intent(this, ChatsFragment.class);
                startActivity(intent2);
                break;
            case R.id.nav_settings:
                Intent intent3 = new Intent(this, cabinet.class);
                startActivity(intent3);
                break;
            case R.id.apple:
                brandList.add("Apple");
                currentIntent.putStringArrayListExtra("brands", brandList);
                finish();
                startActivity(currentIntent);
                break;
            case R.id.samsung:
                brandList.add("Samsung");
                currentIntent.putStringArrayListExtra("brands", brandList);
                finish();
                startActivity(currentIntent);
                break;
            case R.id.huawei:
                brandList.add("HUAWEI");
                currentIntent.putStringArrayListExtra("brands", brandList);
                finish();
                startActivity(currentIntent);
                break;
            case R.id.xiaomi:
                brandList.add("Xiaomi");
                currentIntent.putStringArrayListExtra("brands", brandList);
                finish();
                startActivity(currentIntent);
                break;
            case R.id.honor:
                brandList.add("Honor");
                currentIntent.putStringArrayListExtra("brands", brandList);
                finish();
                startActivity(currentIntent);
                break;
            case R.id.vivo:
                brandList.add("vivo");
                currentIntent.putStringArrayListExtra("brands", brandList);
                finish();
                startActivity(currentIntent);
                break;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        ((TextView)headerView.findViewById(R.id.name)).setText(RetrofitClientInstance.USERNAME);
        new cabinet.ImageBitmapUriTask(this, findViewById(R.id.account_icon)).execute(RetrofitClientInstance.BASE_URL + "/user/avatar");
        new cabinet.ImageBitmapUriTask(this, headerView.findViewById(R.id.menu_avatar)).execute(RetrofitClientInstance.BASE_URL + "/user/avatar");
    }
}

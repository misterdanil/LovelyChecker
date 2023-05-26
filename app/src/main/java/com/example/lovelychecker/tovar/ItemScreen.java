package com.example.lovelychecker.tovar;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelychecker.R;
import com.example.lovelychecker.RetrofitClientInstance;
import com.example.lovelychecker.cabinet;
import com.example.lovelychecker.interfaceAPI;
import com.google.gson.internal.LinkedTreeMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemScreen extends AppCompatActivity {
    private TextView name, marks;

    private ReviewAdapter reviewAdapter;
    private RecyclerView reviewList;
    private RatingBar ratingBar;
    private ImageView image;
    private Button toOtziv, nash_site, ne_nash;

    private Product loadedProduct;
    private boolean isHidden = true;

    private List<Magazins> magazinsList = new ArrayList<>();
    MagazAdapter magazAdapter;
    private RecyclerView magazlist;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_screen);

        new cabinet.ImageBitmapUriTask(this, findViewById(R.id.account_icon)).execute(RetrofitClientInstance.BASE_URL + "/user/avatar");

        LinearLayout linearLayout = findViewById(R.id.characteristic_button);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isHidden) {
                    findViewById(R.id.characteristicsTable).setVisibility(View.VISIBLE);
                    ((ImageView) findViewById(R.id.characteristicArrow)).setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
                    isHidden = false;
                }
                else {
                    findViewById(R.id.characteristicsTable).setVisibility(View.GONE);
                    ((ImageView) findViewById(R.id.characteristicArrow)).setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
                    isHidden = true;
                }

            }
        });

        magazlist = findViewById(R.id.magazlist);
        magazinsList.add(new Magazins("MVIDEO", "photo", 14000));
        magazinsList.add(new Magazins("ELDORADO", "photo", 14000));
//        setMagRecylder(magazinsList);

        toOtziv = findViewById(R.id.Review);
        name = findViewById(R.id.ScreenName);
        image = findViewById(R.id.ScreenImage);
        ratingBar = findViewById(R.id.ratingItem);
        reviewList = findViewById(R.id.reviewList);
        reviewList.setVisibility(View.GONE);
        marks = findViewById(R.id.textMark);
        ne_nash = findViewById(R.id.button);
        nash_site = findViewById(R.id.button2);

        ne_nash.setTextColor(
                Color.parseColor("#696C71"));
        ne_nash.setBackground(Drawable.createFromPath("@drawable/ramk_disabled"));
        nash_site.setTextColor(Color.parseColor("#696C71"));
        nash_site.setBackground(Drawable.createFromPath("@drawable/ramk_disabled"));

        findViewById(R.id.arrow_icon).setOnClickListener(e -> {
            finish();
        });


        String id = getIntent().getExtras().getString("id");
        interfaceAPI apiService = RetrofitClientInstance.getInstance();

        Call<Product> request = apiService.getProduct(id);

        Activity activity = this;
        request.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if(response.isSuccessful()) {
                    Product product = response.body();
                    loadedProduct = product;
                    LinkedTreeMap<String, Object> characteristics = (LinkedTreeMap<String, Object>)product.getCharacteristics();
                    product.getCharacteristics();

                    marks.setText("Среднее " + BigDecimal.valueOf(product.getAverageRating())
                            .setScale(2, RoundingMode.HALF_UP).toString()); //Сюда пихать среднюю оценку
                    name.setText(product.getTitle()); //Сюда название товара
                    new ProductAdapter.ImageBitmapUriTask(activity,image).execute(RetrofitClientInstance.BASE_URL + "/" + "product/smartphones/" + product.getId() + "/image"); // Сюда картинку
                    ratingBar.setRating(product.getAverageRating().floatValue()); //Сюда так же рейтинг, но он будет отображаться звёздочками

                    TableLayout tl = findViewById(R.id.characteristicsTable);
                    TableRow.LayoutParams rowParams = new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
                    Resources r = getResources();
                    rowParams.bottomMargin =  (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            5,
                            r.getDisplayMetrics());

                    TableRow basicRow = getTitle("Общие параметры");
                    tl.addView(basicRow);
                    TableRow brandRow = getParameter("Бренд", (String)characteristics.get("brand"));
                    tl.addView(brandRow);
                    TableRow seriesText = getParameter("Серия", (String)characteristics.get("series"));
                    tl.addView(seriesText);
                    if(characteristics.containsKey("os")) {
                        TableRow osText = getParameter("Операционная система", (String) characteristics.get("os"));
                        tl.addView(osText);
                    }

                    LinkedTreeMap<String, Object> screen = (LinkedTreeMap<String, Object>) characteristics.get("screen");
                    TableRow screenRow = getTitle("Экран");
                    tl.addView(screenRow);
                    if(screen.containsKey("diagonal")) {
                        TableRow diagonalRow = getParameter("Диагональ", String.valueOf(screen.get("diagonal")));
                        tl.addView(diagonalRow);
                    }
                    if(screen.containsKey("resolution")) {
                        TableRow resolutionRow = getParameter("Разрешение", (String) screen.get("resolution"));
                        tl.addView(resolutionRow);
                    }
                    if(screen.containsKey("type")) {
                        TableRow screenTypeRow = getParameter("Тип экрана", (String) screen.get("type"));
                        tl.addView(screenTypeRow);
                    }
                    if(screen.containsKey("frequency")) {
                        TableRow frequencyRow = getParameter("Частота обновления", (String) screen.get("frequency") + " " + (String) screen.get("frequence_measure"));
                        tl.addView(frequencyRow);
                    }
                    if(screen.containsKey("brightless")) {
                        TableRow brightlessRow = getParameter("Яркость", (String) screen.get("brightless"));
                        tl.addView(brightlessRow);
                    }

                    LinkedTreeMap<String, Object> processor = (LinkedTreeMap<String, Object>) characteristics.get("processor");
                    TableRow processorRow = getTitle("Процессор");
                    tl.addView(processorRow);
                    if(processor.containsKey("value")) {
                        TableRow processorModelRow = getParameter("Модель", (String) processor.get("value"));
                        tl.addView(processorModelRow);
                    }
                    if(processor.containsKey("cores")) {
                        TableRow coresRow = getParameter("Количество ядер", (String) processor.get("cores"));
                        tl.addView(coresRow);
                    }

                    LinkedTreeMap<String, Object> ram = (LinkedTreeMap<String, Object>) characteristics.get("ram");
                    TableRow ramRow = getTitle("Оперативная память");
                    tl.addView(ramRow);
                    if(ram.containsKey("value")) {
                        TableRow ramValueRow = getParameter("Объём", (String) ram.get("value") + " " + (String) ram.get("measure"));
                        tl.addView(ramValueRow);
                    }

                    LinkedTreeMap<String, Object> rom = (LinkedTreeMap<String, Object>) characteristics.get("rom");
                    TableRow romRow = getTitle("Встроенная память");
                    tl.addView(romRow);
                    if(rom.containsKey("value")) {
                        TableRow romValueRow = getParameter("Объём", ((String) rom.get("value")) + " " + ((String) rom.get("measure")));
                        tl.addView(romValueRow);
                    }

                    LinkedTreeMap<String, Object> backCamera = (LinkedTreeMap<String, Object>) characteristics.get("back_camera");
                    TableRow backCameraRow = getTitle("Задняя камера");
                    tl.addView(backCameraRow);
                    if(backCamera.containsKey("mpics")) {
                        TableRow mpicsRow = getParameter("Мегапиксели", (String) backCamera.get("mpics"));
                        tl.addView(mpicsRow);
                    }
                    if(backCamera.containsKey("count")) {
                        TableRow countCameraRow = getParameter("Количество камер", (String) backCamera.get("count"));
                        tl.addView(countCameraRow);
                    }
                    if(backCamera.containsKey("resolution")) {
                        TableRow resolutionBackCameraRow = getParameter("Разрешение", (String) backCamera.get("resolution"));
                        tl.addView(resolutionBackCameraRow);
                    }
                    if(backCamera.containsKey("zoom")) {
                        TableRow zoomRow = getParameter("Зум", (String) backCamera.get("zoom"));
                        tl.addView(zoomRow);
                    }
                    if(backCamera.containsKey("flash")) {
                        TableRow flashRow = getParameter("Вспышка", ((boolean) backCamera.get("flash")) == true ? "Да" : "Нет");
                        tl.addView(flashRow);
                    }

                    LinkedTreeMap<String, Object> frontCamera = (LinkedTreeMap<String, Object>) characteristics.get("front_camera");
                    TableRow frontCameraRow = getTitle("Фронтальная камера");
                    tl.addView(frontCameraRow);
                    if(frontCamera.containsKey("mpics")) {
                        TableRow mpicsFrontRow = getParameter("Мегапиксели", (String) frontCamera.get("mpics"));
                        tl.addView(mpicsFrontRow);
                    }
                    if(frontCamera.containsKey("count")) {
                        TableRow countFrontCameraRow = getParameter("Количество камер", (String) frontCamera.get("count"));
                        tl.addView(countFrontCameraRow);
                    }

                    LinkedTreeMap<String, Object> sd = (LinkedTreeMap<String, Object>) characteristics.get("sd");
                    TableRow sdRow = getTitle("Карта памяти");
                    tl.addView(sdRow);
                    if(sd.containsKey("max")) {
                        TableRow sdValueRow = getParameter("Объём", (String) sd.get("max") + " " + (String) sd.get("measure"));
                        tl.addView(sdValueRow);
                    }
                    if(sd.containsKey("type")) {
                        TableRow sdTypeRow = getParameter("Тип", (String) sd.get("type"));
                        tl.addView(sdTypeRow);
                    }

                    if(characteristics.containsKey("mpics")) {
                        TableRow simRow = getTitle("Сим-карта");
                        tl.addView(simRow);
                        TableRow simValueRow = getParameter("Тип", (String) characteristics.get("sim"));
                        tl.addView(simValueRow);
                    }

                    LinkedTreeMap<String, Object> wireless = (LinkedTreeMap<String, Object>) characteristics.get("wireless");
                    TableRow wirelessRow = getTitle("Беспроводные интерфейсы");
                    tl.addView(wirelessRow);
                    if(wireless.containsKey("mpics")) {
                        TableRow mobileRow = getParameter("Мобильная сеть", (String) wireless.get("mobile_wireless"));
                        tl.addView(mobileRow);
                    }
                    if(wireless.containsKey("type")) {
                        TableRow wifiRow = getParameter("WiFi", (String) wireless.get("type"));
                        tl.addView(wifiRow);
                    }
                    if(wireless.containsKey("nfc_support")) {
                        TableRow nfcRow = getParameter("NFC", ((boolean) wireless.get("nfc_support") == true) ? "Да" : "Нет");
                        tl.addView(nfcRow);
                    }
                    if(wireless.containsKey("bluetooth")) {
                        TableRow bluetoothRow = getParameter("Bluetooth", (String) wireless.get("bluetooth"));
                        tl.addView(bluetoothRow);
                    }
                    if(wireless.containsKey("gps_support")) {
                        TableRow gpsRow = getParameter("GPS", ((boolean) wireless.get("gps_support") == true) ? "Да" : "Нет");
                        tl.addView(gpsRow);
                    }
                    if(wireless.containsKey("miracast_support")) {
                        TableRow miracastRow = getParameter("Miracast", ((boolean) wireless.get("miracast_support") == true) ? "Да" : "Нет");
                        tl.addView(miracastRow);
                    }
                    if(wireless.containsKey("glonass_support")) {
                        TableRow glonassSupport = getParameter("ГЛОНАСС", ((boolean) wireless.get("glonass_support") == true) ? "Да" : "Нет");
                        tl.addView(glonassSupport);
                    }

                    LinkedTreeMap<String, Object> security = (LinkedTreeMap<String, Object>) characteristics.get("security");
                    TableRow securityRow = getTitle("Безопасность");
                    tl.addView(securityRow);
                    if(security.containsKey("face_id")) {
                        TableRow faceRow = getParameter("Сканер лица", (boolean) security.get("face_id") == true ? "Да" : "Нет");
                        tl.addView(faceRow);
                    }
                    if(security.containsKey("touch_id")) {
                        TableRow touchRow = getParameter("Отпечаток пальца", (boolean) security.get("touch_id") == true ? "Да" : "Нет");
                        tl.addView(touchRow);
                    }

                    LinkedTreeMap<String, Object> interfaceConn = (LinkedTreeMap<String, Object>) characteristics.get("interface");
                    TableRow interfaceRow = getTitle("Интерфейсы");
                    tl.addView(interfaceRow);
                    if(interfaceConn.containsKey("connection")) {
                        TableRow connectionRow = getParameter("Подключение", (String) interfaceConn.get("connection"));
                        tl.addView(connectionRow);
                    }
                    if(interfaceConn.containsKey("headphones")) {
                        TableRow headphonesRow = getParameter("Наушники", (String) interfaceConn.get("headphones"));
                        tl.addView(headphonesRow);
                    }

                    if(characteristics.containsKey("material")) {
                        TableRow materialRow = getTitle("Материал");
                        tl.addView(materialRow);
                        TableRow materialValueRow = getParameter("Материал", (String) characteristics.get("material"));
                        tl.addView(materialValueRow);
                    }

                    LinkedTreeMap<String, Object> battery = (LinkedTreeMap<String, Object>) characteristics.get("battery");
                    TableRow batteryRow = getTitle("Батарея");
                    tl.addView(batteryRow);
                    if(battery.containsKey("type")) {
                        TableRow typeBatteryRow = getParameter("Тип", (String) battery.get("type"));
                        tl.addView(typeBatteryRow);
                    }
                    if(battery.containsKey("capacity")) {
                        TableRow capacityRow = getParameter("Ёмкость", (String) battery.get("capacity"));
                        tl.addView(capacityRow);
                    }
                    if(battery.containsKey("fast_charge_support")) {
                        TableRow fastChargeRow = getParameter("Быстрая зарядка", (boolean) battery.get("fast_charge_support") == true ? "Да" : "Нет");
                        tl.addView(fastChargeRow);
                    }

                    LinkedTreeMap<String, Object> appearance = (LinkedTreeMap<String, Object>) characteristics.get("appearance");
                    TableRow appearanceRow = getTitle("Внешний вид");
                    tl.addView(appearanceRow);
                    if(appearance.containsKey("weight")) {
                        TableRow weightRow = getParameter("Вес", (String) appearance.get("weight") + " " + appearance.get("measure"));
                        tl.addView(weightRow);
                    }
                    if(appearance.containsKey("dimensions")) {
                        TableRow dimensionsRow = getParameter("Размеры", (String) appearance.get("dimensions"));
                        tl.addView(dimensionsRow);
                    }
                }
                else {
                    System.out.println(response.code());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                System.err.println("fail");
            }
        });
        toOtziv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemScreen.this, Review.class);
                intent.putExtra("id", id);
                startActivity(intent);
                finish();
            }
        });
        nash_site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ne_nash.setTextColor(Color.parseColor("#696C71"));
                ne_nash.setBackground(Drawable.createFromPath("@drawable/ramk_disabled"));
                nash_site.setTextColor(Color.parseColor("#748DBB"));
                nash_site.setBackground(Drawable.createFromPath("@drawable/ramk_enabled"));

                ReviewResponse resp1 = new ReviewResponse();
                resp1.setRating(4);
                resp1.setTitle("Отличный самртфон");
                resp1.setText("Ну вот такое себе");
                resp1.setUsername("james_darmody");
                ReviewResponse resp2 = new ReviewResponse();
                resp2.setRating(4);
                resp2.setTitle("Отличный самртфон");
                resp2.setText("Ну вот такое себе");
                resp2.setUsername("james_darmody");

//                setReviewRecylder(Arrays.asList(resp1, resp2)); //где Лист это отзывы с нашего сайта

                interfaceAPI interfaceApi = RetrofitClientInstance.getInstance();
                Call<List<ReviewResponse>> call = interfaceApi.getReviews(loadedProduct.getId());

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
                
                reviewList.setVisibility(View.VISIBLE);
            }
        });
        ne_nash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nash_site.setTextColor(Color.parseColor("#696C71"));
                nash_site.setBackground(Drawable.createFromPath("@drawable/ramk_disabled"));
                ne_nash.setTextColor(Color.parseColor("#748DBB"));
                ne_nash.setBackground(Drawable.createFromPath("@drawable/ramk_enabled"));

                ResourceReview resp1 = new ResourceReview();
                resp1.setRating(4);
                resp1.setAdvantages("Отличный самртфон");
                resp1.setDisadvantages("Оперативка кал");
                resp1.setText("Ну вот такое себе");
                resp1.setSender("james_darmody");
                ResourceReview resp2 = new ResourceReview();
                resp2.setRating(4);
                resp2.setText("Ну вот такое себе");
                resp2.setSender("james_darmody");

//                setResourceReviewRecylder(Arrays.asList(resp1, resp2)); //где Лист это отзывы с нашего сайта

                interfaceAPI interfaceApi = RetrofitClientInstance.getInstance();
                Call<List<ResourceReview>> call = interfaceApi.getResourcesReviews(loadedProduct.getId(), "MVIDEO");

                call.enqueue(new Callback<List<ResourceReview>>() {
                    @Override
                    public void onResponse(Call<List<ResourceReview>> call, Response<List<ResourceReview>> response) {
                        if(response.isSuccessful()) {
                            List<ResourceReview> reviews = response.body();
                            setResourceReviewRecylder(reviews);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ResourceReview>> call, Throwable t) {
                        System.err.println("fail");
                    }
                });

                reviewList.setVisibility(View.VISIBLE);
            }
        });

        Call<List<Magazins>> call = apiService.getResources(id);

        call.enqueue(new Callback<List<Magazins>>() {
            @Override
            public void onResponse(Call<List<Magazins>> call, Response<List<Magazins>> response) {
                if(response.isSuccessful()) {
                    List<Magazins> resources = response.body();
                    List<Magazins> resources2 = new ArrayList<>();

                    resources.forEach(resource -> {
                        if(resource.getColors() != null) {
                            for (Map.Entry<String, String> e : resource.getColors().entrySet()) {
                                Magazins product = new Magazins(resource.getType(), e.getKey() + " " + resource.getName(), resource.getPrice());
                                product.setLink(e.getValue());

                                resources2.add(product);
                            }
                        }
                        else {
                            resources2.add(resource);
                        }
                    });

                    setMagRecylder(resources2);
                }
                else {
                    System.err.println(response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Magazins>> call, Throwable t) {

            }
        });

    }
    private void setReviewRecylder(List<ReviewResponse> item) {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        reviewList = findViewById(R.id.reviewList);
        reviewList.setLayoutManager(layoutManager);

        reviewAdapter = new ReviewAdapter(this, this, item);
        reviewList.setAdapter(reviewAdapter);
    }

    private void setResourceReviewRecylder(List<ResourceReview> item) {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        reviewList = findViewById(R.id.reviewList);
        reviewList.setLayoutManager(layoutManager);

        ResourceReviewAdapter resourceReviewAdapter = new ResourceReviewAdapter(this, this, item);
        reviewList.setAdapter(resourceReviewAdapter);
    }

    private TableRow getParameter(String key, String value) {
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        Resources r = getResources();
        rowParams.bottomMargin =  (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5,
                r.getDisplayMetrics());

        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(rowParams);

        TextView keyView = new TextView(this);

        SpannableString content = new SpannableString(key);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

        keyView.setText(content);
        keyView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                5,
                r.getDisplayMetrics());
//        lp.rightMargin = (int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP,
//                5,
//                r.getDisplayMetrics());
        lp.weight = 2;
        keyView.setLayoutParams(lp);
        tableRow.addView(keyView);

        TextView valueView = new TextView(this);
        valueView.setText(value);
        valueView.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.gothampro);
        valueView.setTypeface(typeface);
        TableRow.LayoutParams lp2 = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1;
        valueView.setLayoutParams(lp2);
        tableRow.addView(valueView);

        return tableRow;
    }

    private TableRow getTitle(String value) {
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        Resources r = getResources();

        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(rowParams);

        TextView titleView = new TextView(this);
        titleView.setText(value);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.gothampro_medium);
        titleView.setTypeface(typeface);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10,
                r.getDisplayMetrics());
        lp.weight = 1;
        titleView.setLayoutParams(lp);
        tableRow.addView(titleView);

        return tableRow;
    }

    private void setMagRecylder(List<Magazins> item) {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        magazlist = findViewById(R.id.magazlist);
        magazlist.setLayoutManager(layoutManager);

        magazAdapter = new MagazAdapter(this, item);
        magazlist.setAdapter(magazAdapter);
    }
}
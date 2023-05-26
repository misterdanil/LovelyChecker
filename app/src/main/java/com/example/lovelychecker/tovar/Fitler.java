package com.example.lovelychecker.tovar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelychecker.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Fitler extends AppCompatActivity {

    private Button okey;

    private final Set<String> brand = new HashSet<>();

    private final Set<String> ozy = new HashSet<>();
    private FilterAdapter brandAdapter;
    private FilterAdapter ramAdapter;

    private RecyclerView brandList;

    public Fitler() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitler);
        okey = (Button) findViewById(R.id.set);
        brand.add("Xiaomi");
        brand.add("Apple");
        brand.add("Samsung");
        brand.add("HUAWEI");
        brand.add("Honor");
        brand.add("vivo");
        brand.add("Tecno");
        brand.add("realme");
        brand.add("OPPO");
        brand.add("POCO");
        brand.add("Infinix");
        ozy.add("4 GB");
        ozy.add("6 GB");
        ozy.add("2 GB");
        ozy.add("8 GB");

        Bundle bundle = getIntent().getExtras();

        ArrayList<String> brands = null;
        ArrayList<String> rams = null;
        Double priceFrom = null;
        Double priceTo = null;
        if(bundle != null) {
            brands = bundle.getStringArrayList("brands");
            rams = bundle.getStringArrayList("rams");
            priceFrom = (Double)bundle.get("priceFrom");
            priceTo = (Double)bundle.get("priceTo");
        }

        if(priceFrom != null) {
            ((EditText)findViewById(R.id.priceFrom)).setText(String.valueOf(priceFrom));
        }
        if(priceTo != null) {
            ((EditText)findViewById(R.id.priceTo)).setText(String.valueOf(priceTo));
        }

        okey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Fitler.this, Tovar_Activity.class);
                intent.putStringArrayListExtra("brands", brandAdapter.getFilters());
                intent.putStringArrayListExtra("rams", ramAdapter.getFilters());
                String priceFrom = ((EditText)findViewById(R.id.priceFrom)).getText().toString();
                String priceTo = ((EditText)findViewById(R.id.priceTo)).getText().toString();
                if(!priceFrom.isEmpty()) {
                    intent.putExtra("priceFrom", Double.parseDouble(priceFrom));
                }
                if(!priceTo.isEmpty()) {
                    intent.putExtra("priceTo", Double.parseDouble(priceTo));
                }

                startActivity(intent);
                finish();
            }
        });
        setBrandRecylder(brand, brands);
        setOzyRecylder(ozy, rams);
    }
    private void setBrandRecylder(Set<String> set, ArrayList<String> brands) {
        List<String> item = new ArrayList<String>(set);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        brandList = findViewById(R.id.brandList);
        brandList.setLayoutManager(layoutManager);

        brandAdapter  = new FilterAdapter(this, item, "brand", brands);
        brandList.setAdapter(brandAdapter);
    }
    private void setOzyRecylder(Set<String> set, ArrayList<String> rams) {
        List<String> item = new ArrayList<String>(set);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        brandList = findViewById(R.id.ozy);
        brandList.setLayoutManager(layoutManager);

        ramAdapter  = new FilterAdapter(this, item, "ram", rams);
        brandList.setAdapter(ramAdapter);
    }

    public void accept(View view) {
        List<String> rams = ramAdapter.getFilters();
        List<String> brands = brandAdapter.getFilters();
    }
}
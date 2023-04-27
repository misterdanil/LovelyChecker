package com.example.lovelychecker.tovar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelychecker.R;
import com.example.lovelychecker.RetrofitClientInstance;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE = 1;
    private final Context context;

    private final Activity activity;
    private final List<Product> listRecyclerItem;

    public ProductAdapter(Activity acitivity, Context context, List<Product> listRecyclerItem) {
        this.activity = acitivity;
        this.context = context;
        this.listRecyclerItem = listRecyclerItem;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView name;

        private TextView price;

        private TextView score;

        private ImageView image;

        private LinearLayout container;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            score = itemView.findViewById(R.id.score);
            name = itemView.findViewById(R.id.prodName);
            price = itemView.findViewById(R.id.prodPrice);
            image = itemView.findViewById(R.id.tovarImage);
            container = itemView.findViewById(R.id.container);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i) {
            case TYPE:

            default:

                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);

                return new ItemViewHolder((layoutView));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        int viewType = getItemViewType(i);

        switch (viewType) {
            case TYPE:
            default:

                ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
                Product product = listRecyclerItem.get(i);
                new ImageBitmapUriTask(activity, itemViewHolder.image).execute(RetrofitClientInstance.BASE_URL + "/" + "product/smartphones/" + product.getId() + "/image");
                itemViewHolder.name.setText(product.getTitle());//Имя сюда
                itemViewHolder.score.setText(String.valueOf(product.getAverageRating()));//Сюда среднюю оценку
                itemViewHolder.price.setText(String.format(String.valueOf(product.getFromPrice())+"₽ - "+product.getToPrice()+"₽"));//Сюда цену
                itemViewHolder.container.setOnClickListener(e -> {
                    Intent intent = new Intent(context, ItemScreen.class);
                    intent.putExtra("id", product.getId());
                    context.startActivity(intent);
                });
        }

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

    @Override
    public int getItemCount() {
        return listRecyclerItem.size();
    }
}
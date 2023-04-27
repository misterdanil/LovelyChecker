package com.example.lovelychecker.tovar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelychecker.R;

import java.util.List;

public class MagazAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE = 1;
    private final Context context;
    private final List<Magazins> listRecyclerItem;

    public MagazAdapter(Context context, List<Magazins> listRecyclerItem) {
        this.context = context;
        this.listRecyclerItem = listRecyclerItem;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView name;

        private Button price;
        private ImageView magazImage;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tov_name);
            price = itemView.findViewById(R.id.price_button);
            magazImage = itemView.findViewById(R.id.magaz_image);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i) {
            case TYPE:

            default:

                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.magazine_item, viewGroup, false);

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
                Magazins Magazins = listRecyclerItem.get(i);
                itemViewHolder.name.setText(Magazins.getName());
                itemViewHolder.price.setText(String.valueOf(Magazins.getPrice()));
                if (Magazins.getType().equals("MVIDEO")) {
                    itemViewHolder.magazImage.setImageResource(R.drawable.mvideo2);
                }
                else {
                    itemViewHolder.magazImage.setImageResource(R.drawable.eldorado);
                }

                itemViewHolder.price.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Magazins.getLink()));
                        context.startActivity(browserIntent);
                    }
                });
        }

    }

    @Override
    public int getItemCount() {
        return listRecyclerItem.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
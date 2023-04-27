package com.example.lovelychecker.tovar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelychecker.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE = 1;
    private final Context context;
    private final List<String> listRecyclerItem;

    private String type;
    private ArrayList<String> filters;

    public FilterAdapter(Context context, List<String> listRecyclerItem, String type, ArrayList<String> filters) {
        this.context = context;
        this.listRecyclerItem = listRecyclerItem;
        this.type = type;
        this.filters = filters;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private CheckBox box;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.brand);
            box  = itemView.findViewById(R.id.checkBox3);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i) {
            case TYPE:

            default:

                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.brand_item, viewGroup, false);

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

                itemViewHolder.name.setText(listRecyclerItem.get(i));//Имя сюда

                if(filters != null && filters.contains(listRecyclerItem.get(i))) {
                    itemViewHolder.box.setChecked(true);
                }

                if(type.equals("ram")) {
                    String value = itemViewHolder.name.getText().toString();
                    if(filters != null && filters.contains(value.split(" ")[0])) {
                        itemViewHolder.box.setChecked(true);
                    }
                    itemViewHolder.box.setOnClickListener(v -> addFilter(v, value.split(" ")[0]));
                }
                else {
                    itemViewHolder.box.setOnClickListener(v -> addFilter(v, itemViewHolder.name.getText().toString()));

                }
        }

    }

    @Override
    public int getItemCount() {
        return listRecyclerItem.size();
    }

    public void addFilter(View view, String value) {
        CheckBox box = (CheckBox) view;
        if (box.isChecked()) {
            if(filters == null) {
                filters = new ArrayList<>();
            }
            filters.add(value);
        }
        else {
            filters.remove(value);
        }
    }

    public ArrayList<String> getFilters() {
        return filters;
    }
}
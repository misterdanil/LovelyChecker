package com.example.lovelychecker.tovar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovelychecker.Chat;
import com.example.lovelychecker.ChatActivity;
import com.example.lovelychecker.ChatRequest;
import com.example.lovelychecker.R;
import com.example.lovelychecker.RetrofitClientInstance;
import com.example.lovelychecker.interfaceAPI;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResourceReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int TYPE = 1;
    private final Context context;
    private final Activity activity;
    private final List<ResourceReview> listRecyclerItem;
    private final interfaceAPI apiService = RetrofitClientInstance.getInstance();

    public ResourceReviewAdapter(Context context, Activity activity, List<ResourceReview> listRecyclerItem) {
        this.context = context;
        this.activity = activity;
        this.listRecyclerItem = listRecyclerItem;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private String id;
        private TextView nickname;

        ImageView image;

        private TextView rat;

        private TextView advantages;
        private TextView disadvantages;

        private TextView opisanie;
        private ImageView like;
        private TextView likeCount;

        private ImageView dislike;
        private TextView dislikeCount;

        private ImageView logo;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.avatar);
            nickname = itemView.findViewById(R.id.nick);
            rat = itemView.findViewById(R.id.rati);
            advantages = itemView.findViewById(R.id.advantage);
            disadvantages = itemView.findViewById(R.id.disadvantage);
            opisanie = itemView.findViewById(R.id.op);
            like = itemView.findViewById(R.id.like);
            likeCount = itemView.findViewById(R.id.likeCount);
            dislike = itemView.findViewById(R.id.dislike);
            dislikeCount = itemView.findViewById(R.id.dislikeCount);
            logo = itemView.findViewById(R.id.logo);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i) {
            case TYPE:

            default:

                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_shop_review, viewGroup, false);

                return new ItemViewHolder((layoutView));
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        int viewType = getItemViewType(i);

        switch (viewType) {
            case TYPE:
            default:

                ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
                ResourceReview review = listRecyclerItem.get(i);
                //Сюды писать отзывы
                itemViewHolder.rat.setText(review.getRating() + "/5"); //Вот сюда пихать рейтин, на который оценил пользователь
                itemViewHolder.opisanie.setText(review.getText()); //Вот сюда пихать описание товара, которое написал пользователь
                if(review.getType().equals("MVIDEO")) {
                    itemViewHolder.advantages.setText(review.getAdvantages()); //Вот сюда пихать заголовок( текст который был выделен)
                    itemViewHolder.disadvantages.setText(review.getDisadvantages());
                    itemViewHolder.likeCount.setText("(" + listRecyclerItem.get(i).getLikesCount() + ")");
                    itemViewHolder.dislikeCount.setText("(" + listRecyclerItem.get(i).getDislikesCount() + ")");
                    itemViewHolder.logo.setImageResource(R.drawable.mvideo2);
                }
                else if(review.getType().equals("ELDORADO")) {
                    itemViewHolder.logo.setImageResource(R.drawable.eldorado);
                    itemViewHolder.likeCount.setVisibility(View.GONE);
                    itemViewHolder.dislikeCount.setVisibility(View.GONE);
                    itemViewHolder.like.setVisibility(View.GONE);
                    itemViewHolder.dislike.setVisibility(View.GONE);
                    itemViewHolder.advantages.setVisibility(View.GONE);
                    itemViewHolder.disadvantages.setVisibility(View.GONE);
                }
                itemViewHolder.nickname.setText(review.getSender()); //Вот сюда пихать никнейм пользователя
                itemViewHolder.image.setImageResource(R.drawable.baseline_person_24); //Сюда пихать аватарку пользователя
        }

    }
    @Override
    public int getItemCount() {
        return listRecyclerItem.size();
    }
}

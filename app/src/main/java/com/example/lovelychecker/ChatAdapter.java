package com.example.lovelychecker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<Chat> chats;
    private Context context;
    private Activity activity;

    public ChatAdapter(Context context, List<Chat> chats, Activity activity) {
        this.context = context;
        this.chats = chats;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chats.get(position);

        holder.itemView.setOnClickListener(e -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("id", chat.getId());
            intent.putExtra("productId", chat.getProduct().getId());
            context.startActivity(intent);
        });
        if(chat.getUser().getFirstName() != null && !chat.getUser().getFirstName().isEmpty()) {
            holder.title.setText(chat.getUser().getFirstName());
        }
        else {
            holder.title.setText(chat.getUser().getUsername());
        }
        new ChatActivity.ImageBitmapUriTask(activity, holder.image).execute(RetrofitClientInstance.BASE_URL + "/" + "product/smartphones/" + chat.getProduct().getId() + "/image");
        if(chat.getLastMessage() != null) {
            holder.message.setText(chat.getLastMessage().getText());

            Date createdOn = chat.getLastMessage().getCreatedOn();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(createdOn);
            String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("ru", "RU"));
            String dayAndMonth = calendar.get(Calendar.DAY_OF_MONTH) + " " + month;

            holder.messageDate.setText(dayAndMonth);
        }
        holder.product.setText(chat.getProduct().getTitle());

        holder.new RetrieveFeedTask(activity).execute(chat.getId());

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView message;
        TextView product;
        TextView messageDate;
        ImageView image;

        class SocketTask implements Runnable {
            private TextView message;
            private TextView messageDate;
            private Message payloadMessage;

            public SocketTask(TextView message, TextView messageDate, Message payloadMessage) {
                this.message = message;
                this.messageDate = messageDate;
                this.payloadMessage = payloadMessage;
            }

            @Override
            public void run() {
                if (message != null) {
                    message.setText(payloadMessage.getText());

                    Date createdOn = payloadMessage.getCreatedOn();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(createdOn);
                    String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("ru", "RU"));
                    String dayAndMonth = calendar.get(Calendar.DAY_OF_MONTH) + " " + month;

                    messageDate.setText(dayAndMonth);
                }

            }
        }
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewName);
            messageDate = itemView.findViewById(R.id.textViewDate);
            image = itemView.findViewById(R.id.imageViewChat);
            message = itemView.findViewById(R.id.textViewLastMsg);
            product = itemView.findViewById(R.id.textViewProduct);
        }
        private class RetrieveFeedTask extends AsyncTask<String, Void, Void> {

            private Exception exception;
            private Activity activity;

            public RetrieveFeedTask(Activity activity) {
                this.activity = activity;
            }

            protected Void doInBackground(String... ids) {
                StompSessionHandler sessionHandler = new StompSessionHandler() {

                    public void handleFrame(StompHeaders headers, Object payload) {
                        activity.runOnUiThread(new SocketTask(message, messageDate, (Message) payload));
                    }

                    public Type getPayloadType(StompHeaders headers) {
                        return Message.class;
                    }

                    public void handleTransportError(StompSession session, Throwable exception) {
                        System.out.println(exception);

                    }

                    public void handleException(StompSession session, StompCommand command, StompHeaders headers,
                                                byte[] payload, Throwable exception) {
                        System.out.println(exception);

                    }

                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        session.subscribe("/topic/" + ids[0] + "/messages", this);
                        System.out.println("it connected");
                    }
                };

                WebSocketStompClient client = new WebSocketStompClient(new StandardWebSocketClient());
                client.setMessageConverter(new MappingJackson2MessageConverter());
                client.connect(RetrofitClientInstance.SOCKET_URL + "/chatwww", sessionHandler);
                return null;
            }

            protected void onPostExecute(Void feed) {
                // TODO: check this.exception
                // TODO: do something with the feed
            }
        }
    }
}

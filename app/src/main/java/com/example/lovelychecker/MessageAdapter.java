package com.example.lovelychecker;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE = 1;
    private final Context context;
    private final List<Message> listRecyclerItem;
    private RecyclerView recyclerView;

    public MessageAdapter(Context context, List<Message> listRecyclerItem, RecyclerView recyclerView) {
        this.context = context;
        this.listRecyclerItem = listRecyclerItem;
        this.recyclerView = recyclerView;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView messageView;
        TextView dateView;
        TextView nameView;
        TextView globalDateView;
        boolean setdDate;
        public ItemViewHolder(@NonNull View itemView, TextView messageView, TextView dateView, TextView nameView, TextView globalDateView, boolean setDate) {
            super(itemView);
            this.messageView = messageView;
            this.dateView = dateView;
            this.nameView = nameView;
            this.globalDateView = globalDateView;
            this.setdDate = setDate;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView;
        TextView messageView;
        TextView dateView;
        TextView globalDateView;
        TextView nameView = null;

        switch (i) {
            case TYPE:

            default:
                if(listRecyclerItem.get(i).getUser().getId().equals(RetrofitClientInstance.USER_ID)) {
                    layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_me_fragment, viewGroup, false);
                    messageView = layoutView.findViewById(R.id.text_gchat_message_me);
                    dateView = layoutView.findViewById(R.id.text_gchat_timestamp_me);
                    globalDateView = layoutView.findViewById(R.id.text_gchat_date_me);
                }
                else {
                    layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_from_fragment, viewGroup, false);
                    messageView = layoutView.findViewById(R.id.text_gchat_message_other);
                    dateView = layoutView.findViewById(R.id.text_gchat_timestamp_other);
                    nameView = layoutView.findViewById(R.id.text_gchat_user_other);
                    globalDateView = layoutView.findViewById(R.id.text_gchat_date_other);
                }

                boolean set = false;

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(listRecyclerItem.get(i).getCreatedOn().getTime());

                if(i > 0) {
                    Calendar previousMessageCal = Calendar.getInstance();
                    previousMessageCal.setTimeInMillis(listRecyclerItem.get(i - 1).getCreatedOn().getTime());

                    if(previousMessageCal.get(Calendar.DAY_OF_MONTH) < calendar.get(Calendar.DAY_OF_MONTH)) {
                        set = true;
                    }
                }
                else {
                    set = true;
                }

                return new ItemViewHolder(layoutView, messageView, dateView, nameView, globalDateView, set);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        int viewType = getItemViewType(i);

        switch (viewType) {
            case TYPE:
            default:

                ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
                Message message = listRecyclerItem.get(i);

                if(!itemViewHolder.setdDate) {
                    itemViewHolder.globalDateView.setVisibility(View.GONE);
                }
                else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(message.getCreatedOn());
                    String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("ru", "RU"));
                    String dayAndMonth = calendar.get(Calendar.DAY_OF_MONTH) + " " + month;
                    itemViewHolder.globalDateView.setText(dayAndMonth);
                }

                itemViewHolder.messageView.setText(message.getText());

                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                calendar.setTimeInMillis(message.getCreatedOn().getTime());

                String time = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

                itemViewHolder.dateView.setText(time);
                if (itemViewHolder.nameView != null) {
                    itemViewHolder.nameView.setText(message.getUser().getFirstName());
                }
        }

    }

    class SocketTask implements Runnable {
        private Message payloadMessage;
        private MessageAdapter adapter;
        private RecyclerView recyclerView;

        public SocketTask(Message payloadMessage, MessageAdapter adapter, RecyclerView recyclerView) {
            this.payloadMessage = payloadMessage;
            this.adapter = adapter;
            this.recyclerView = recyclerView;
        }

        @Override
        public void run() {
            adapter.listRecyclerItem.add(payloadMessage);

            adapter.notifyDataSetChanged();

            recyclerView.scrollToPosition(adapter.listRecyclerItem.size() - 1);
        }
    }

    public class RetrieveFeedTask extends AsyncTask<String, Void, Void> {

        private Exception exception;
        private Activity activity;
        private MessageAdapter messageAdapter;

        public RetrieveFeedTask(Activity activity, MessageAdapter messageAdapter) {
            this.activity = activity;
            this.messageAdapter = messageAdapter;
        }

        protected Void doInBackground(String... ids) {
            StompSessionHandler sessionHandler = new StompSessionHandler() {

                public void handleFrame(StompHeaders headers, Object payload) {
                    activity.runOnUiThread(new SocketTask((Message) payload, messageAdapter, recyclerView));
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

    @Override
    public int getItemCount() {
        return listRecyclerItem.size();
    }
}

package com.example.telegram.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telegram.R;
import com.example.telegram.models.ChatMessage;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MessageAdapter extends FirebaseRecyclerAdapter<ChatMessage, MessageAdapter.MessageViewHolder> {


    Context context;
    ArrayList<ChatMessage> messages;

    private final FirebaseAuth auth= FirebaseAuth.getInstance();
    private final FirebaseUser user= auth.getCurrentUser();
    boolean spaceType=false;
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public MessageAdapter(Context context, ArrayList<ChatMessage> messages, @NonNull FirebaseRecyclerOptions<ChatMessage> options)
    {
        super(options);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getUserUID().equals(user.getUid())){
            spaceType=true;
            return MSG_TYPE_RIGHT;
        } else {
            spaceType=false;
            return MSG_TYPE_LEFT;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View messageItems;
        if (viewType == MSG_TYPE_RIGHT)
        {
            messageItems = LayoutInflater.from(context).inflate(R.layout.item_message_right, parent, false);
        }
        else
        {
            messageItems = LayoutInflater.from(context).inflate(R.layout.item_message_left, parent, false);
        }
        return new MessageViewHolder(messageItems);
    }


    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull ChatMessage model) {
        if(spaceType)
        {
            holder.message.setText(model.getUserMessage() + "\u2007\u2007\u2007\u2007\u2007\u2007");
        }
        else
        {
            holder.message.setText(model.getUserMessage()+"\u2007\u2007\u2007\u2007");
        }
        holder.time.setText(DateFormat.format("HH:mm", messages.get(position).getMessageTime()));
    }

    @Override
    public int getItemCount() {

        return messages.size();
    }

    public static final class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView message;
        TextView time;
        public MessageViewHolder(@NonNull View itemView) {

            super(itemView);
            time = itemView.findViewById(R.id.message_time);
            message = itemView.findViewById(R.id.user_message_text);
        }
    }
}

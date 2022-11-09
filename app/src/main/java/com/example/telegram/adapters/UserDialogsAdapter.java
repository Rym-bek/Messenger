package com.example.telegram.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.telegram.R;
import com.example.telegram.models.UserDialogs;
import com.example.telegram.ui.UserMessagePage;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class UserDialogsAdapter extends RecyclerView.Adapter<UserDialogsAdapter.UserDialogsViewHolder> {

    Context context;
    ArrayList<UserDialogs> userDialogsList;

    public UserDialogsAdapter(Context context, ArrayList<UserDialogs> userDialogs)
    {
        this.context=context;
        this.userDialogsList = userDialogs;
    }

    @NonNull
    @Override
    public UserDialogsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View userDialogsItems= LayoutInflater.from(context).inflate(R.layout.item_dialog, parent, false);
        return new UserDialogsAdapter.UserDialogsViewHolder(userDialogsItems);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDialogsViewHolder holder, int position) {
        UserDialogs userDialogs=userDialogsList.get(position);
        holder.textView_dialogHeader.setText(userDialogs.getName());
        holder.textView_dialogMessage.setText(userDialogs.getUserMessage());

        Glide.with(context)
                .load(userDialogs.getUserPhoto())
                .signature(new ObjectKey(userDialogs.getKeyPhoto()))
                .into(holder.imageView_dialogPhoto);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, UserMessagePage.class);
                intent.putExtra("uid", userDialogs.getUid());
                intent.putExtra("photo", userDialogs.getUserPhoto().toString());
                intent.putExtra("name",userDialogs.getName());
                intent.putExtra("nickname", userDialogs.getNickname());
                intent.putExtra("photoKey", userDialogs.getKeyPhoto());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userDialogsList.size();
    }

    public static final class UserDialogsViewHolder extends RecyclerView.ViewHolder
    {
        ShapeableImageView imageView_dialogPhoto;
        MaterialTextView textView_dialogHeader, textView_dialogMessage;
        public UserDialogsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView_dialogPhoto=itemView.findViewById(R.id.imageView_dialogPhoto);
            textView_dialogHeader=itemView.findViewById(R.id.textView_dialogHeader);
            textView_dialogMessage=itemView.findViewById(R.id.textView_dialogMessage);
        }
    }
}

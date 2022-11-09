package com.example.telegram.adapters;

import static com.mikepenz.iconics.Iconics.getApplicationContext;

import android.annotation.SuppressLint;
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
import com.example.telegram.models.UserDataShort;
import com.example.telegram.ui.UserMessagePage;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FindUsersAdapter extends RecyclerView.Adapter<FindUsersAdapter.FindUsersViewHolder> {


    Context context;
    List<UserDataShort> users;
    FirebaseStorage storage=FirebaseStorage.getInstance();
    StorageReference storageReference=storage.getReference();
    public FindUsersAdapter(Context context, List<UserDataShort> users) {
        this.context = context;
        this.users = users;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<UserDataShort> filterList) {
        users = filterList;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        if(users!=null)
        {
            users.clear();
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public FindUsersAdapter.FindUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View findUsersItems= LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new FindUsersAdapter.FindUsersViewHolder(findUsersItems);
    }

    @Override
    public void onBindViewHolder(@NonNull FindUsersAdapter.FindUsersViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(getApplicationContext())
                .load(storageReference.child("users").child(users.get(position).getUid()).child("userPhoto"))
                .placeholder(R.drawable.ic_launcher_background)
                .signature(new ObjectKey(users.get(position).getPhoto().hashCode()))
                .into(holder.imageView_item_findUser);
        holder.textView_item_findUser_nickName.setText(users.get(position).getNickname());
        holder.textView_item_findUser_name.setText(users.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, UserMessagePage.class);
                intent.putExtra("uid",users.get(position).getUid());
                intent.putExtra("photo",users.get(position).getPhoto());
                intent.putExtra("name",users.get(position).getName());
                intent.putExtra("nickname",users.get(position).getNickname());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(users!=null)
        {
            return users.size();
        }
        else
        {
            return 0;
        }
    }

    public static final class FindUsersViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView imageView_item_findUser;
        MaterialTextView textView_item_findUser_nickName, textView_item_findUser_name;
        public FindUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView_item_findUser=itemView.findViewById(R.id.imageView_item_findUser);
            textView_item_findUser_nickName=itemView.findViewById(R.id.textView_item_findUser_nickName);
            textView_item_findUser_name=itemView.findViewById(R.id.textView_item_findUser_name);
        }
    }
}

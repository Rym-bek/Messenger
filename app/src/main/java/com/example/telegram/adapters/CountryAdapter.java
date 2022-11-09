package com.example.telegram.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telegram.R;
import com.example.telegram.models.Country;
import com.example.telegram.ui.LoginPage;

import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {

    Context context;
    List<Country> list_country;

    public CountryAdapter(Context context, List<Country> list_country) {
        this.context = context;
        this.list_country = list_country;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<Country> filterllist) {
        list_country = filterllist;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View countryItems= LayoutInflater.from(context).inflate(R.layout.item_country, parent, false);
        return new CountryViewHolder(countryItems);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.country_flag.setText(list_country.get(position).getCountry_flag());
        holder.country_name.setText(list_country.get(position).getCountry_name());
        holder.country_code.setText("+"+list_country.get(position).getCountry_code());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, LoginPage.class);
                intent.putExtra("getId", list_country.get(position).getId());
                intent.putExtra("getCountry_flag", list_country.get(position).getCountry_flag());
                intent.putExtra("getCountry_name", list_country.get(position).getCountry_name());
                intent.putExtra("getCountry_code", list_country.get(position).getCountry_code());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list_country.size();
    }

    public static final class CountryViewHolder extends RecyclerView.ViewHolder{
        TextView country_flag, country_name, country_code;
        public CountryViewHolder(@NonNull View itemView) {
            super(itemView);
            country_flag=itemView.findViewById(R.id.textView_item_country_flag);
            country_name=itemView.findViewById(R.id.textView_item_country_name);
            country_code=itemView.findViewById(R.id.textView_item_country_code);
        }
    }
}

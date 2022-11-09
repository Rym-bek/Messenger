package com.example.telegram.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.telegram.R;
import com.example.telegram.models.Slide;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {

    private final Context context;
    private ArrayList<Slide> list_slider;

    public SliderAdapter(Context context, ArrayList<Slide> sliderDataArrayList) {
        this.context = context;
        this.list_slider = sliderDataArrayList;
    }

    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider, null);
        return new SliderAdapterViewHolder(inflate);
    }
    private Drawable[] backgroundsDrawableArrayForTransition;
    private TransitionDrawable transitionDrawable;
    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, int position)
    {
        int image_id = context.getResources().getIdentifier("ic_"+ list_slider.get(position).getSlide_image(), "drawable", context.getPackageName());
        viewHolder.imageView_item_slider.setImageResource(image_id);
        viewHolder.textView_item_slider_main.setText(list_slider.get(position).getSlide_main());
        viewHolder.textView_item_slider_description.setText(list_slider.get(position).getSlide_description());

    }

    @Override
    public int getCount() {
        return list_slider.size();
    }

    class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
        View itemView;

        ImageView imageView_item_slider;
        TextView textView_item_slider_main;
        TextView textView_item_slider_description;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageView_item_slider = itemView.findViewById(R.id.imageView_item_slider);
            textView_item_slider_main = itemView.findViewById(R.id.textView_item_slider_main);
            textView_item_slider_description = itemView.findViewById(R.id.textView_item_slider_description);
            this.itemView = itemView;
        }
    }

}

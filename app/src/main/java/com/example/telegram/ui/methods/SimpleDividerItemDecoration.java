package com.example.telegram.ui.methods;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.telegram.R;
import com.example.telegram.database.DatabaseHelper;
import com.example.telegram.models.Country;

import java.io.IOException;
import java.util.List;

public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;
    List<Country> list_country;
    List list_positions;
    public SimpleDividerItemDecoration(Context context, List<Country> list_country) {
        mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        this.list_country = list_country;
    }
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        final int left = parent.getPaddingLeft()+70;
        final int right = parent.getWidth() -70;
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount-1; i++) {
            Log.d("SUKA_AAA",String.valueOf(i));
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);

        }
    }
}
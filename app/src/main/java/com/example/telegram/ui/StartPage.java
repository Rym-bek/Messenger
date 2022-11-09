package com.example.telegram.ui;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.telegram.R;
import com.example.telegram.adapters.SliderAdapter;
import com.example.telegram.database.DatabaseHelper;
import com.example.telegram.models.Slide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StartPage extends AppCompatActivity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    ImageView imageView_StartPage;
    ImageView imageView_shine;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);

        FirebaseApp.initializeApp(StartPage.this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance());

        ArrayList<Slide> sliderDataArrayList = new ArrayList<>();

        //База данных
        mDBHelper = new DatabaseHelper(this);
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        //открытие базы данных
        mDBHelper.openDataBase();

        sliderDataArrayList.addAll(mDBHelper.get_slider_info());
        //закрытие базы данных
        mDBHelper.close();

        SliderAdapter sliderAdapter = new SliderAdapter(this, sliderDataArrayList);
        SliderView sliderView = findViewById(R.id.imageSlider);
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.startAutoCycle();

        imageView_StartPage=findViewById(R.id.imageView_StartPage);
        imageView_shine=findViewById(R.id.imageView_shine);

        //Start the animations preoidically by calling 'shineStart' method with ScheduledExecutorService
        ScheduledExecutorService executorService =
                Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shineStart();
                    }
                });
            }
        }, 3,3, TimeUnit.SECONDS);


        //прослушиватель кнопки
        imageView_StartPage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StartPage.this, LoginPage.class);
                intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }


    private void shineStart() {
        Animation animation = new TranslateAnimation(
                0,imageView_StartPage.getWidth()+imageView_shine.getWidth(),0,0);
        animation.setDuration(1100);
        animation.setFillAfter(false);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        imageView_shine.startAnimation(animation);
    }
}

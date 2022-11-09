package com.example.telegram.ui;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.example.telegram.R;
import com.example.telegram.adapters.ViewPagerFragmentAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class MailAuth extends AppCompatActivity {
    TabLayout tabLayout_mailRegistration;
    ViewPager2 viewPager_mailRegistration;
    Toolbar toolbar_MailRegistration;
    ViewPagerFragmentAdapter viewPagerFragmentAdapter;

    private final String[] labels = new String[]{"Вход", "Регистрация"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_home_auth);

        tabLayout_mailRegistration = findViewById(R.id.tabLayout_mailRegistration);
        viewPager_mailRegistration = findViewById(R.id.viewPager_mailRegistration);
        toolbar_MailRegistration = findViewById(R.id.toolbar_MailRegistration);

        boolean changeEmail=getIntent().getBooleanExtra("change_email",false);
        boolean addEmail=getIntent().getBooleanExtra("add_email",false);
        if(changeEmail)
        {
            viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(this,true);
            viewPager_mailRegistration.setUserInputEnabled(false);
        }
        else if(addEmail)
        {
            viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(this,true);
            viewPager_mailRegistration.setUserInputEnabled(false);
        }
        else
        {
            viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(this,false);
        }



        viewPager_mailRegistration.setAdapter(viewPagerFragmentAdapter);

        // bind and set tabLayout to viewPager2 and set labels for every tab
        new TabLayoutMediator(tabLayout_mailRegistration, viewPager_mailRegistration, (tab, position) -> {
            tab.setText(labels[position]);
            if(addEmail || changeEmail)
            {
                tab.view.setClickable(false);
            }
            if(addEmail)
            {
                viewPager_mailRegistration.setCurrentItem(1);
            }
        }).attach();



        setSupportActionBar(toolbar_MailRegistration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_MailRegistration.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });

    }


}

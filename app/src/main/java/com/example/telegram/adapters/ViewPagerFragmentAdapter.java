package com.example.telegram.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.telegram.ui.fragments.FragmentMailLogin;
import com.example.telegram.ui.fragments.FragmentMailRegistration;

import java.util.ArrayList;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> fragments;
    private final boolean flagAction;
    public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity, boolean flagAction) {
        super(fragmentActivity);
        this.flagAction=flagAction;
    }
    Bundle args = new Bundle();
    // return fragments at every position
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                args.putBoolean("change_email", flagAction);
                Fragment fragmentMailLogin = new FragmentMailLogin();
                fragmentMailLogin.setArguments(args);
                return fragmentMailLogin;
            case 1:
                args.putBoolean("add_email", flagAction);
                Fragment fragmentMailRegistration = new FragmentMailRegistration();
                fragmentMailRegistration.setArguments(args);
                return fragmentMailRegistration;
        }
        return new FragmentMailLogin();
    }




    @Override
    public int getItemCount() {
        return 2;
    }
}

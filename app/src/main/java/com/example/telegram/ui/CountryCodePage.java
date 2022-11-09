package com.example.telegram.ui;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telegram.R;
import com.example.telegram.adapters.CountryAdapter;
import com.example.telegram.database.DatabaseHelper;
import com.example.telegram.models.Country;
import com.example.telegram.ui.methods.SimpleDividerItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CountryCodePage extends AppCompatActivity {
    RecyclerView countryRecycler;
    CountryAdapter countryAdapter;
    static List<Country> country_list = new ArrayList<>();
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.country_code_page);

        //верхняя панель
        Toolbar toolbar_CountryCodePage = findViewById(R.id.toolbar_CountryCodePage);
        setSupportActionBar(toolbar_CountryCodePage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar_CountryCodePage.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });

        //база данных
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
        mDBHelper.openDataBase();
        country_list.clear();
        country_list.addAll(mDBHelper.get_country_info());
        mDBHelper.close();

        setCountryRecycler(country_list);

    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    private void setCountryRecycler(List<Country> country_list) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        countryRecycler = findViewById(R.id.countryRecycler);
        countryRecycler.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext(),country_list
        ));
        countryRecycler.setLayoutManager(layoutManager);
        countryAdapter = new CountryAdapter(this, country_list);
        countryRecycler.setAdapter(countryAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_view, menu);
        MenuItem searchItem = menu.findItem(R.id.toolbar_searchView);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Поиск");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Country> filteredList = new ArrayList<>();
                for (int i = 0;i < country_list.size();i++)
                {
                    if (country_list.get(i).getCountry_name().toLowerCase().contains(newText.toLowerCase()))
                    {
                        filteredList.add(country_list.get(i));
                    }
                }
                //вызов функции, которая принимает изменения в адаптере с новым списком
                countryAdapter.filterList(filteredList);
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onDestroy() {
        country_list.clear();
        super.onDestroy();
    }
}

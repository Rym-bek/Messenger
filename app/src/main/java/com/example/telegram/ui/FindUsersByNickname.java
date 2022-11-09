package com.example.telegram.ui;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telegram.R;
import com.example.telegram.adapters.FindUsersAdapter;
import com.example.telegram.models.UserData;
import com.example.telegram.models.UserDataShort;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FindUsersByNickname extends AppCompatActivity {
    Toolbar toolbar_findUsersByNickname;
    RecyclerView recyclerView_findUsersByNickname;
    FindUsersAdapter findUsersAdapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("users"),userRefUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_user_by_nickname);
        toolbar_findUsersByNickname = findViewById(R.id.toolbar_findUsersByNickname);
        recyclerView_findUsersByNickname = findViewById(R.id.recyclerView_findUsersByNickname);

        setSupportActionBar(toolbar_findUsersByNickname);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar_findUsersByNickname.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setFindUsersRecycler();
    }

    private void setFindUsersRecycler() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView_findUsersByNickname.setLayoutManager(layoutManager);
        findUsersAdapter = new FindUsersAdapter(FindUsersByNickname.this, null);
        recyclerView_findUsersByNickname.setAdapter(findUsersAdapter);
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
                List<UserDataShort> filteredList = new ArrayList<>();
                if(!TextUtils.isEmpty(newText))
                {
                    Query userQuery = userRef.orderByChild("nickname").startAt(newText).endAt(newText + "\uf8ff");
                    userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    UserData user = dataSnapshot.getValue(UserData.class);
                                    filteredList.add(new UserDataShort(user.getUid(), user.getPhoto(), user.getNickname(), user.getName()));
                                }
                                findUsersAdapter.filterList(filteredList);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("filteredList", error.getMessage());
                        }
                    });
                }
                else
                {
                    findUsersAdapter.clear();
                }
                return false;
            }
        });
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

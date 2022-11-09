package com.example.telegram.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.telegram.R;
import com.example.telegram.adapters.UserDialogsAdapter;
import com.example.telegram.models.ChatMessage;
import com.example.telegram.models.UserData;
import com.example.telegram.models.UserDialogs;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String TAG = "homeDialogs";
    private Toolbar toolbar_HomePage;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TextView textView_sidePanel_number,textView_sidePanel_nickName;
    private ImageView imageView_sidePanel_avatar;
    private List<UserData> listUser = new ArrayList<>();

    private FirebaseUser user;
    private FirebaseAuth auth;


    private StorageReference storageReference=FirebaseStorage.getInstance().getReference();
    private StorageReference mountainStorageReference=storageReference.child("users");
    private StorageReference currentStorageReference,anotherStorageReference;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference messagesRef = database.getReference("messages");
    private DatabaseReference anotherUserRef = database.getReference("users");
    private DatabaseReference allDialogRef;
    private Query query;

    private RecyclerView allDialogsRecycler;


    private UserDialogsAdapter userDialogsAdapter;
    private ArrayList<UserDialogs> dialogsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        checkCurrentUser();

        navigationView=findViewById(R.id.nav_view);
        drawerLayout=findViewById(R.id.drawer_layout);

        allDialogsRecycler=findViewById(R.id.allDialogsRecycler);
        toolbar_HomePage=findViewById(R.id.toolbar_HomePage);


        textView_sidePanel_number = (TextView) navigationView.getHeaderView(0).findViewById(R.id.textView_sidePanel_number);
        textView_sidePanel_nickName=(TextView) navigationView.getHeaderView(0).findViewById(R.id.textView_sidePanel_nickName);
        imageView_sidePanel_avatar=(ImageView) navigationView.getHeaderView(0).findViewById(R.id.imageView_sidePanel_avatar);
        //логика меню
        setSupportActionBar(toolbar_HomePage);

        ActionBarDrawerToggle actionBarDrawerToggle= new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar_HomePage,
                R.string.OpenNavDrawer,
                R.string.CloseNavDrawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        setQuery();
        messagesRef.child(user.getUid()).addChildEventListener(new ChildEventListener()  {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey()!=null)
                {
                    Query localQuery=allDialogRef.child(dataSnapshot.getKey())
                            .orderByKey()
                            .limitToLast(1);
                    localQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot childDataSnapshot) {
                            for (DataSnapshot child: childDataSnapshot.getChildren()) {
                                ChatMessage chatMessage = child.getValue(ChatMessage.class);

                                anotherUserRef.child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot nameSnapshot) {
                                        if (nameSnapshot.exists()) {
                                            UserData userData = nameSnapshot.getValue(UserData.class);
                                            anotherStorageReference = mountainStorageReference.child(userData.getUid()).child("userPhoto");
                                            for (int i = 0; i < dialogsArrayList.size(); i++) {
                                                if(dialogsArrayList.get(i).getUid().contains(dataSnapshot.getKey()))
                                                {
                                                    dialogsArrayList.remove(i);
                                                    userDialogsAdapter.notifyItemRemoved(i);
                                                }
                                            }
                                            dialogsArrayList.add(0, new UserDialogs(userData.getUid(), userData.getName(), userData.getNickname(), chatMessage.getUserMessage(), anotherStorageReference, userData.getPhoto().hashCode(), 100));
                                            userDialogsAdapter.notifyItemInserted(0);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        throw error.toException();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            throw databaseError.toException();
                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
            }


            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });

        imageView_sidePanel_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, EditUserProfile.class);
                startActivity(intent);
            }
        });

        setDialogsRecycler();
    }

    private void setDialogsRecycler() {
        allDialogsRecycler.setLayoutManager(new LinearLayoutManager(this));

        userDialogsAdapter = new UserDialogsAdapter(this, dialogsArrayList);
        allDialogsRecycler.setAdapter(userDialogsAdapter);
    }

    private FirebaseRecyclerOptions<UserDialogs> setOptions()
    {
        FirebaseRecyclerOptions<UserDialogs> options
                = new FirebaseRecyclerOptions.Builder<UserDialogs>()
                .setQuery(query, UserDialogs.class)
                .build();
        return options;
    }

    @Override
    protected void onStop() {
        super.onStop();

        //закрыть бокове меню
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserInfo();
    }

    private void checkCurrentUser()
    {
        if(user==null)
        {
            Intent intent = new Intent(HomeActivity.this, StartPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void setQuery()
    {
        if(user!=null)
        {
            allDialogRef=messagesRef.child(user.getUid());
            query=allDialogRef.limitToLast(100);
        }
    }

    void setUserInfo()
    {
        if (user != null) {
            if(storageReference!=null)
            {
                currentStorageReference=mountainStorageReference.child(user.getUid()).child("userPhoto");
            }
            String name = user.getDisplayName();
            String email = user.getEmail();
            String phoneNumber = user.getPhoneNumber();

            setUserPhoto();

            if(name!=null && !name.equals(""))
            {
                textView_sidePanel_nickName.setText(name);
            }
            if(phoneNumber!=null && !phoneNumber.equals(""))
            {
                textView_sidePanel_number.setText(phoneNumber);
            }
            else
            {
                if (email != null && !email.equals(""))
                {
                    textView_sidePanel_number.setText(email);
                }
            }
        }
    }

    private void setUserPhoto()
    {
        if(user.getPhotoUrl()!=null)
        {
            Glide.with(getApplicationContext())
                    .load(currentStorageReference)
                    .signature(new ObjectKey(user.getPhotoUrl().hashCode()))
                    .into(imageView_sidePanel_avatar);
        }
    }


    //если выбран из пунктов меню
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.navigation_view_find_users:
                startActivity(new Intent(this, FindUsersByNickname.class));
                break;
            case R.id.navigation_view_settings:
                startActivity(new Intent(this, EditUserProfile.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
package com.example.telegram.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.telegram.R;
import com.example.telegram.adapters.MessageAdapter;
import com.example.telegram.models.ChatMessage;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class UserMessagePage extends AppCompatActivity {

    String TAG = "main_message";
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference messagesRef = database.getReference("messages");
    DatabaseReference currentUserRef,anotherUserRef;
    Query query;

    TextInputLayout textInputLayout_userMessagePage;
    TextInputEditText textInputEditText_userMessagePage;
    MaterialToolbar toolbar_UserMessagePage;
    ShapeableImageView imageView_Toolbar_UserMessagePage;
    MaterialTextView textView_Toolbar_Title_UserMessagePage, textView_Toolbar_Subtitle_UserMessagePage;

    private FirebaseAuth auth;
    private FirebaseUser user;

    RecyclerView messageRecycler;
    MessageAdapter messageAdapter;
    ArrayList<ChatMessage> messageArrayList = new ArrayList<>();

    String anotherUid, anotherPhoto, anotherName, anotherNickname;
    int anotherPhotoKey;

    StorageReference storageReference;
    StorageReference mountainStorageReference;
    FirebaseStorage storage;

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_message_page);

        textInputLayout_userMessagePage=findViewById(R.id.textInputLayout_userMessagePage);
        textInputEditText_userMessagePage=findViewById(R.id.textInputEditText_userMessagePage);
        messageRecycler = findViewById(R.id.chatRecycler);
        imageView_Toolbar_UserMessagePage=findViewById(R.id.imageView_Toolbar_UserMessagePage);
        textView_Toolbar_Title_UserMessagePage =findViewById(R.id.textView_Toolbar_Title_UserMessagePage);
        textView_Toolbar_Subtitle_UserMessagePage =findViewById(R.id.textView_Toolbar_Subtitle_UserMessagePage);

        //убирает баг и анимацию
        messageRecycler.setItemAnimator(null);

        anotherUid=getIntent().getStringExtra("uid");
        anotherPhoto=getIntent().getStringExtra("photo");
        anotherName=getIntent().getStringExtra("name");
        anotherNickname=getIntent().getStringExtra("nickname");
        anotherPhotoKey=getIntent().getIntExtra("photoKey",0);

        //хранилище
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //облако
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        setQuery();


        //верхняя панель
        toolbar_UserMessagePage = findViewById(R.id.toolbar_UserMessagePage);
        setSupportActionBar(toolbar_UserMessagePage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_UserMessagePage.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(anotherName!=null)
        {
            textView_Toolbar_Title_UserMessagePage.setText(anotherName);
        }
        if(anotherNickname!=null)
        {
            textView_Toolbar_Subtitle_UserMessagePage.setText(anotherNickname);
        }

        if(anotherPhoto!=null)
        {
            setUserPhoto();
        }


        //отправка сообщения
        textInputEditText_userMessagePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = Objects.requireNonNull(textInputEditText_userMessagePage.getText()).toString();
                if(!message.isEmpty())
                {
                    ChatMessage chatMessage = new ChatMessage(message, System.currentTimeMillis(), user.getUid());
                    currentUserRef.push().setValue(chatMessage);
                    if(anotherUserRef!=null)
                    {
                        anotherUserRef.push().setValue(chatMessage);
                    }
                    textInputEditText_userMessagePage.getText().clear();
                }
            }
        });

        query.addChildEventListener(new ChildEventListener()  {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                messageArrayList.add(chatMessage);
                messageAdapter.notifyItemInserted(messageArrayList.size()-1);
                messageRecycler.smoothScrollToPosition(messageArrayList.size()-1);
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

        //заполнение ресайлкера
        setMessageRecycler();
    }

    private void setQuery()
    {
        if(user!=null)
        {
            if(storageReference!=null && anotherUid!=null)
            {
                mountainStorageReference=storageReference.child("users").child(anotherUid).child("userPhoto");
                anotherUserRef=messagesRef.child(anotherUid).child(user.getUid());
                currentUserRef=messagesRef.child(user.getUid()).child(anotherUid);
            }
            else
            {
                currentUserRef=messagesRef.child(user.getUid());
            }
            query=currentUserRef.limitToLast(100);
        }
    }

    private void setUserPhoto() {
        Log.d("USER_MESSAGE_PAGE_anotherPhoto",mountainStorageReference.toString());
        Glide.with(getApplicationContext())
                .load(mountainStorageReference)
                .signature(new ObjectKey(anotherPhotoKey))
                .into(imageView_Toolbar_UserMessagePage);
    }

    private FirebaseRecyclerOptions<ChatMessage> setOptions()
    {
        FirebaseRecyclerOptions<ChatMessage> options
                = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .build();
        return options;
    }

    private void setMessageRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        layoutManager.setStackFromEnd(true);
        messageRecycler.setLayoutManager(layoutManager);

        messageAdapter = new MessageAdapter(this, messageArrayList,setOptions());
        messageRecycler.setAdapter(messageAdapter);
        messageRecycler.smoothScrollToPosition(Objects.requireNonNull(messageRecycler.getAdapter()).getItemCount());
    }

    @Override protected void onStart()
    {
        super.onStart();
        messageAdapter.startListening();
    }

    @Override protected void onStop()
    {
        super.onStop();
        messageAdapter.stopListening();
    }

}

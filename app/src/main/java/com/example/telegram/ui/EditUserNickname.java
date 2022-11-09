package com.example.telegram.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.telegram.R;
import com.example.telegram.models.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class EditUserNickname extends AppCompatActivity {
    Toolbar toolbar_editUserNickname;
    EditText editText_editUserNickname;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("users"),userRefUid;

    FirebaseUser user;
    FirebaseAuth auth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_nickname);

        toolbar_editUserNickname = findViewById(R.id.toolbar_editUserNickname);
        editText_editUserNickname = findViewById(R.id.editText_editUserNickname);

        //облако
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if(user!=null)
        {
            userRefUid=userRef.child(user.getUid());
        }

        setSupportActionBar(toolbar_editUserNickname);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar_editUserNickname.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editText_editUserNickname.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void setNickName() {
        String nickname=editText_editUserNickname.getText().toString();
        if(!TextUtils.isEmpty(nickname))
        {
            UserData userData=new UserData(user.getUid(),nickname,user.getDisplayName(),user.getPhoneNumber(),user.getEmail(), Objects.requireNonNull(user.getPhotoUrl()).toString());
            userRefUid.setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), "Профиль не обновлён", Toast.LENGTH_SHORT).show();
                    }
                }
            });;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_user_name, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_selectName:
                if(user!=null)
                {
                    setNickName();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
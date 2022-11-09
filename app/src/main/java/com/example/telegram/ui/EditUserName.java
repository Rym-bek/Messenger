package com.example.telegram.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.telegram.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class EditUserName extends AppCompatActivity {
    Toolbar toolbar_editUserName;
    EditText editText_editUserName_editName,editText_editUserName_editSurname;

    FirebaseUser user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_name);

        toolbar_editUserName = findViewById(R.id.toolbar_editUserName);
        editText_editUserName_editName = findViewById(R.id.editText_editUserName_editName);
        editText_editUserName_editSurname = findViewById(R.id.editText_editUserName_editSurname);

        user = FirebaseAuth.getInstance().getCurrentUser();

        setSupportActionBar(toolbar_editUserName);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar_editUserName.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editText_editUserName_editName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void setName()
    {
        String userName=editText_editUserName_editName.getText().toString();
        String userSurname=editText_editUserName_editSurname.getText().toString();
        if(!TextUtils.isEmpty(userName))
        {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(userName+" "+userSurname)
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getBaseContext(), "Имя не обновлено", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_user_name, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_selectName:
                setName();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

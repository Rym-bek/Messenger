package com.example.telegram.ui.dialogs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.telegram.R;
import com.example.telegram.ui.InputRegistrationCodePage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class DialogLoginPageConfirm extends AppCompatActivity {
    TextView textView_dialog_LoginPage_phone_number;
    Button button_dialog_LoginPage_phone_change,button_dialog_LoginPage_phone_apply;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login_page_confirm);


        textView_dialog_LoginPage_phone_number=findViewById(R.id.textView_dialog_LoginPage_phone_number);

        button_dialog_LoginPage_phone_change=findViewById(R.id.button_dialog_LoginPage_phone_change);
        button_dialog_LoginPage_phone_apply=findViewById(R.id.button_dialog_LoginPage_phone_apply);

        button_dialog_LoginPage_phone_change.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        button_dialog_LoginPage_phone_apply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(textView_dialog_LoginPage_phone_number.getText().toString().replaceAll("\\s+","").length()<11)
                {
                    finish();
                    Intent intent = new Intent(DialogLoginPageConfirm.this, DialogLoginPageWarning.class);
                    intent.putExtra("country_name",getIntent().getStringExtra("country_name"));
                    intent.putExtra("phone_number",textView_dialog_LoginPage_phone_number.getText());
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(DialogLoginPageConfirm.this, InputRegistrationCodePage.class);
                    intent.putExtra("phone_number",textView_dialog_LoginPage_phone_number.getText());
                    intent.putExtra("add_phone_to_profile",getIntent().getBooleanExtra("add_phone_to_profile",false));
                    startActivity(intent);
                }
            }
        });

        textView_dialog_LoginPage_phone_number.setText(getIntent().getStringExtra("phone_number"));

    }

    @Override
    protected void onStart() {
        super.onStart();
        //привязка диалога к верху и создание прочразчного фона
        Window window = this.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        //сделать края диалога закруглёнными
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout_dialog_LoginPage);
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(20);
        shape.setColor(Color.WHITE);
        constraintLayout.setBackground(shape);
    }
}

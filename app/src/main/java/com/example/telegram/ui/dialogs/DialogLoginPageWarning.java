package com.example.telegram.ui.dialogs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.telegram.R;


public class DialogLoginPageWarning extends AppCompatActivity {
    TextView textView_dialog_LoginPage_warning_country_name, textView_dialog_LoginPage_warning_phone_number;
    Button button_dialog_LoginPage_warning_help,button_dialog_LoginPage_warning_apply;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login_page_warning);

        textView_dialog_LoginPage_warning_country_name=findViewById(R.id.textView_dialog_LoginPage_warning_country_name);
        textView_dialog_LoginPage_warning_phone_number=findViewById(R.id.textView_dialog_LoginPage_warning_phone_number);

        button_dialog_LoginPage_warning_help=findViewById(R.id.button_dialog_LoginPage_warning_help);
        button_dialog_LoginPage_warning_apply=findViewById(R.id.button_dialog_LoginPage_warning_apply);

        button_dialog_LoginPage_warning_help.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });

        button_dialog_LoginPage_warning_apply.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        textView_dialog_LoginPage_warning_phone_number.setText(getIntent().getStringExtra("phone_number"));

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getIntent().getStringExtra("country_name"));
        spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD),0,spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView_dialog_LoginPage_warning_country_name.append(" ");
        textView_dialog_LoginPage_warning_country_name.append(spannableStringBuilder);

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
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout_dialog_LoginPage_warning);
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(20);
        shape.setColor(Color.WHITE);
        constraintLayout.setBackground(shape);
    }
}

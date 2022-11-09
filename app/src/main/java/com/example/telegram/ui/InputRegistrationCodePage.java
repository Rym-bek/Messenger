package com.example.telegram.ui;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.telegram.R;
import com.example.telegram.database.DatabaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class InputRegistrationCodePage extends AppCompatActivity {
    TextView textView_InputRegistrationCode_InputSmsCode_first, textView_InputRegistrationCode_InputSmsCode_second,
            textView_InputRegistrationCode_InputSmsCode_third,textView_InputRegistrationCode_InputSmsCode_four,
            textView_InputRegistrationCode_InputSmsCode_five, textView_InputRegistrationCode_InputSmsCode_six;

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    TableRow tableRow;

    Vibrator vibrator;

    int code_count=6;

    private String verificationId;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private static final String VERIFICATION_ID_KEY = "verification_id";

    String email, password, phoneNumber;

    //обвести один объект
    private void setBorderColor(TextView textView, int colorId)
    {
        Drawable background = textView.getBackground();
        GradientDrawable gradientDrawable = (GradientDrawable) background;
        gradientDrawable.setStroke(2,getColor(colorId));
    }

    //стартовая обводка
    private void allBorders(int colorIdFirst, int colorIdOthers)
    {
        setBorderColor(textView_InputRegistrationCode_InputSmsCode_first, colorIdFirst);
        for(int i=1;i<=code_count-1;i++)
        {
            setBorderColor((TextView)tableRow.getChildAt(i), colorIdOthers);
        }
    }

    private void handleTextView(int initialValue, int finalValue, final TextView  textview, String number) {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(initialValue, finalValue);
        valueAnimator.setDuration(200);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                textview.setText(valueAnimator.getAnimatedValue().toString());

            }
        });
        valueAnimator.start();
        textview.setText(number);
    }

    public void vibration()
    {
        //вибрация
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            vibrator.vibrate(50);
        }
    }

    private void addEmail()
    {
        if(email!=null && password!=null)
        {
            AuthCredential authCredential = EmailAuthProvider.getCredential(email, password);
            user.linkWithCredential(authCredential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                Intent intent = new Intent(InputRegistrationCodePage.this, EditUserProfile.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Ошибка привязки", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void changePhoneNumber(PhoneAuthCredential credential)
    {
        user.updatePhoneNumber(credential)
                .addOnCompleteListener(new OnCompleteListener <Void>() {
                    @Override
                    public void onComplete(@NonNull Task <Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(InputRegistrationCodePage.this, EditUserProfile.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(InputRegistrationCodePage.this, "Ошибка изменения номера", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addPhoneNumber(PhoneAuthCredential credential)
    {
        user.linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(InputRegistrationCodePage.this, EditUserProfile.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        } else {
                            Toast.makeText(InputRegistrationCodePage.this, "Ошибка добавления номер", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void signInWithCredential(PhoneAuthCredential credential) {
        if(getIntent().getBooleanExtra("add_phone_to_profile",false))
        {
            addPhoneNumber(credential);
        }
        else if(getIntent().getBooleanExtra("change_phone_number",false))
        {
            changePhoneNumber(credential);
        }
        else
        {
            auth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                if(email!=null && password!=null)
                                {
                                    addEmail();
                                }
                                else
                                {
                                    allBorders(R.color.green, R.color.green);
                                    Intent intent = new Intent(InputRegistrationCodePage.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            else
                            {
                                allBorders(R.color.red, R.color.red);

                                for(int i=0;i<=code_count-1;i++) {
                                    TextView textView = (TextView) tableRow.getChildAt(i);
                                    textView.setText(null);
                                }

                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        allBorders(R.color.main_theme, R.color.grey);
                                    }
                                }, 2000);
                                Toast.makeText(InputRegistrationCodePage.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void sendVerificationCode(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(number)            // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(InputRegistrationCodePage.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };


    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    public void button_functionality(String number)
    {
        TextView textView_case=(TextView)tableRow.getChildAt(code_count-1);
        if(textView_case.length()!=1)
        {
            vibration();
            textView_case=(TextView)tableRow.getChildAt(code_count-2);
            if(textView_case.length()==1)
            {
                textView_case=(TextView)tableRow.getChildAt(code_count-1);
                setBorderColor(textView_case, R.color.grey);
                handleTextView((Math.abs(Integer.parseInt(number)+10))*2,Integer.parseInt(number),textView_case, number);

                //склейка и отправка кода
                StringBuilder code_local = new StringBuilder();
                for(int i=0;i<=code_count-1;i++) {
                    TextView textView = (TextView) tableRow.getChildAt(i);
                    code_local.append(textView.getText());
                }
                Log.d("verifyCode",String.valueOf(code_local));
                verifyCode(String.valueOf(code_local));
            }
            else
            {
                for(int i=0;i<code_count-1;i++)
                {
                    TextView textView=(TextView)tableRow.getChildAt(i);
                    if(textView.length()==0)
                    {
                        handleTextView((Math.abs(Integer.parseInt(number)+10))*2,Integer.parseInt(number),textView, number);
                        setBorderColor(textView, R.color.grey);
                        setBorderColor((TextView) tableRow.getChildAt(i+1), R.color.main_theme);
                        break;
                    }
                }
            }
        }

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_registration_code);

        //облако
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        email=getIntent().getStringExtra("mail");
        password=getIntent().getStringExtra("password");
        if(user!=null)
        {
            phoneNumber = user.getPhoneNumber();
        }

        textView_InputRegistrationCode_InputSmsCode_first=findViewById(R.id.textView_InputRegistrationCode_InputSmsCode_first);
        textView_InputRegistrationCode_InputSmsCode_second=findViewById(R.id.textView_InputRegistrationCode_InputSmsCode_second);
        textView_InputRegistrationCode_InputSmsCode_third=findViewById(R.id.textView_InputRegistrationCode_InputSmsCode_third);
        textView_InputRegistrationCode_InputSmsCode_four=findViewById(R.id.textView_InputRegistrationCode_InputSmsCode_four);
        textView_InputRegistrationCode_InputSmsCode_five=findViewById(R.id.textView_InputRegistrationCode_InputSmsCode_five);
        textView_InputRegistrationCode_InputSmsCode_six=findViewById(R.id.textView_InputRegistrationCode_InputSmsCode_six);

        tableRow = findViewById(R.id.tableRow_InputRegistrationCode);

        //вибратор
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        allBorders(R.color.main_theme, R.color.grey);

        //верхняя панель
        Toolbar toolbar_InputRegistrationCode = findViewById(R.id.toolbar_InputRegistrationCode);
        setSupportActionBar(toolbar_InputRegistrationCode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_InputRegistrationCode.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });

        //обработка нажатий на клавиши
        Button number_one = findViewById(R.id.number_one);
        Button number_two = findViewById(R.id.number_two);
        Button number_three = findViewById(R.id.number_three);
        Button number_four = findViewById(R.id.number_four);
        Button number_five = findViewById(R.id.number_five);
        Button number_six = findViewById(R.id.number_six);
        Button number_seven = findViewById(R.id.number_seven);
        Button number_eight = findViewById(R.id.number_eight);
        Button number_nine = findViewById(R.id.number_nine);
        Button number_zero = findViewById(R.id.number_zero);
        ImageButton custom_keyboard_delete = findViewById(R.id.custom_keyboard_delete);

        number_one.setOnClickListener(onClickListener);
        number_two.setOnClickListener(onClickListener);
        number_three.setOnClickListener(onClickListener);
        number_four.setOnClickListener(onClickListener);
        number_five.setOnClickListener(onClickListener);
        number_six.setOnClickListener(onClickListener);
        number_seven.setOnClickListener(onClickListener);
        number_eight.setOnClickListener(onClickListener);
        number_nine.setOnClickListener(onClickListener);
        number_zero.setOnClickListener(onClickListener);
        custom_keyboard_delete.setOnClickListener(onClickListener);

        //вызов регистрации
        if(user!=null && email !=null && password!=null){
            sendVerificationCode(user.getPhoneNumber());
        }
        else
        {
            sendVerificationCode("+"+getIntent().getStringExtra("phone_number").replaceAll("\\s+",""));
        }

        if (verificationId == null && savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(VERIFICATION_ID_KEY, verificationId);
    }

    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (verificationId == null && savedInstanceState != null) {
            verificationId = savedInstanceState.getString(VERIFICATION_ID_KEY);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {

            switch(v.getId()){
                case R.id.number_one:
                    button_functionality(getString(R.string.one));
                    break;
                case R.id.number_two:
                    button_functionality(getString(R.string.two));
                    break;
                case R.id.number_three:
                    button_functionality(getString(R.string.three));
                    break;
                case R.id.number_four:
                    button_functionality(getString(R.string.four));
                    break;
                case R.id.number_five:
                    button_functionality(getString(R.string.five));
                    break;
                case R.id.number_six:
                    button_functionality(getString(R.string.six));
                    break;
                case R.id.number_seven:
                    button_functionality(getString(R.string.seven));
                    break;
                case R.id.number_eight:
                    button_functionality(getString(R.string.eight));
                    break;
                case R.id.number_nine:
                    button_functionality(getString(R.string.nine));
                    break;
                case R.id.number_zero:
                    button_functionality(getString(R.string.zero));
                    break;
                case R.id.custom_keyboard_delete:
                    vibration();

                    TextView textView_case=(TextView)tableRow.getChildAt(code_count-1);
                    if(textView_case.length()==1)
                    {
                        textView_case.setText(null);
                        setBorderColor(textView_case, R.color.main_theme);
                    }
                    else
                    {
                        for(int i=code_count-2;i>=0;i--)
                        {
                            TextView textView=(TextView)tableRow.getChildAt(i);
                            if(textView.length()!=0)
                            {
                                textView.setText(null);
                                setBorderColor(textView, R.color.main_theme);
                                setBorderColor((TextView) tableRow.getChildAt(i+1), R.color.grey);
                                break;
                            }
                        }
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + v.getId());
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        allBorders(R.color.main_theme, R.color.main_theme);
    }
}

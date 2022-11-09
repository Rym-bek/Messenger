package com.example.telegram.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.telegram.R;
import com.example.telegram.database.DatabaseHelper;
import com.example.telegram.models.Country;
import com.example.telegram.ui.dialogs.DialogLoginPageConfirm;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginPage extends AppCompatActivity {
    TextView textView_show_country, hint_LoginPage_phone_number, textView_LoginPage_mailRegistartion;
    EditText textView_LoginPage_phone_number_code,
            textView_LoginPage_phone_number;
    ImageButton LoginPage_imageView_forward;
    Button button_LoginPage_toMailRegistration;
    ConstraintLayout constraintLayout_LoginPage_first_container, constraintLayout_LoginPage_number_container;
    FloatingActionButton LoginPage_floatingActionButton;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    Vibrator vibrator;

    private int last_length;
    private boolean focus_country_code =false;
    private boolean replace_later =false;
    String hint_text="";
    List<Country> country_list = new ArrayList<>();


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


    public void button_functionality(String number)
    {
        if(textView_LoginPage_phone_number_code.length()==4 && !textView_LoginPage_phone_number.hasFocus())
        {
            vibration();
            //анимация
            constraintLayout_LoginPage_number_container.startAnimation(AnimationUtils.loadAnimation(LoginPage.this,R.anim.shake));

        }
        if(textView_LoginPage_phone_number_code.hasFocus())
        {
            textView_LoginPage_phone_number_code.append(number);
        }
        else if(textView_LoginPage_phone_number.hasFocus())
        {
            textView_LoginPage_phone_number.append(number);
        }
        vibration();
    }

    public void setText_and_Focus(StringBuilder stringBuilder){
        textView_LoginPage_phone_number.setText(stringBuilder);
        textView_LoginPage_phone_number.setSelection(textView_LoginPage_phone_number.getText().length());
    }

    public void replace_numbers()
    {
        if(replace_later)
        {
            int length_of_number_code=textView_LoginPage_phone_number_code.length();
            String text_of_textView_LoginPage_phone_number=textView_LoginPage_phone_number.getText().toString();
            int length_of_numbers=text_of_textView_LoginPage_phone_number.length();
            String without_space=text_of_textView_LoginPage_phone_number.replaceAll("\\s+","");
            StringBuilder stringBuilder = new StringBuilder(without_space);
            if(length_of_number_code==1)
            {
                if(length_of_numbers>7)
                {
                    stringBuilder.insert(3, " ");
                    stringBuilder.insert(7, " ");
                    setText_and_Focus(stringBuilder);
                }
                else if(length_of_numbers>3 && length_of_numbers<7)
                {
                    stringBuilder.insert(3, " ");
                    setText_and_Focus(stringBuilder);
                }
            }
            else if(length_of_number_code==2)
            {
                if(length_of_numbers>4)
                {
                    stringBuilder.insert(4, " ");
                    setText_and_Focus(stringBuilder);
                }
            }
            else if(length_of_number_code==3)
            {
                if(length_of_numbers>6)
                {
                    stringBuilder.insert(2, " ");
                    stringBuilder.insert(6, " ");
                    setText_and_Focus(stringBuilder);
                }
                else if(length_of_numbers>2 && length_of_numbers<6)
                {
                    stringBuilder.insert(2, " ");
                    setText_and_Focus(stringBuilder);
                }
            }
            else if(length_of_number_code==4)
            {
                if(length_of_numbers>3)
                {
                    stringBuilder.insert(3, " ");
                    setText_and_Focus(stringBuilder);
                }
            }
            replace_later=false;
        }
        else
        {
            replace_later=true;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        //textview
        textView_show_country = findViewById(R.id.textView_loginPage_showCountry);
        textView_LoginPage_phone_number_code = findViewById(R.id.textView_LoginPage_phone_number_code);
        textView_LoginPage_phone_number = findViewById(R.id.textView_LoginPage_phone_number);
        hint_LoginPage_phone_number = findViewById(R.id.hint_LoginPage_phone_number);
        button_LoginPage_toMailRegistration = findViewById(R.id.button_LoginPage_toMailRegistration);

        constraintLayout_LoginPage_first_container = findViewById(R.id.constraintLayout_LoginPage_country_container);
        constraintLayout_LoginPage_number_container = findViewById(R.id.constraintLayout_LoginPage_number_container);

        //кнопки
        LoginPage_imageView_forward = findViewById(R.id.imageView_loginPage_forward);

        //кнопка подтверждения
        LoginPage_floatingActionButton = findViewById(R.id.LoginPage_floatingActionButton);

        //убрать клавиатуру
        textView_LoginPage_phone_number.setShowSoftInputOnFocus(false);

        textView_LoginPage_phone_number_code.requestFocus();
        textView_LoginPage_phone_number_code.setShowSoftInputOnFocus(false);
        textView_LoginPage_phone_number_code.setTextIsSelectable(true);

        if(getIntent().getBooleanExtra("add_phone_to_profile",false))
        {
            button_LoginPage_toMailRegistration.setVisibility(View.GONE);
        }

        //вибратор
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //сделать неактивным поле номера
        textView_LoginPage_phone_number.setEnabled(false);
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
        country_list.addAll(mDBHelper.get_country_info());

        //получение значений, переданных в активити
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null)
        {
            //задать значения для текстовых полей
            textView_show_country.setText(bundle.getString("getCountry_flag")+"    "+bundle.getString("getCountry_name"));
            textView_LoginPage_phone_number_code.setText(bundle.getString("getCountry_code"));
            //задать умный интерфейс
            set_background_code();


        }


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

        //прослушиватель перехо на страницу регистрации по почте
        button_LoginPage_toMailRegistration.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, MailAuth.class);
                startActivity(intent);
            }
        });

        //прослушиватель кнопки подтверждения регистрации
        LoginPage_floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(textView_LoginPage_phone_number.getText().length()!=0)
                {
                    Intent intent = new Intent(LoginPage.this, DialogLoginPageConfirm.class);
                    intent.putExtra("phone_number",textView_LoginPage_phone_number_code.getText()+" "+textView_LoginPage_phone_number.getText());
                    intent.putExtra("country_name",country_list.get(0).getCountry_name());
                    intent.putExtra("add_phone_to_profile",getIntent().getBooleanExtra("add_phone_to_profile",false));
                    intent.putExtra("change_phone_number",getIntent().getBooleanExtra("add_phone_to_profile",false));
                    startActivity(intent);
                }
                else
                {
                    //анимация
                    constraintLayout_LoginPage_number_container.startAnimation(AnimationUtils.loadAnimation(LoginPage.this,R.anim.shake));

                    //вибрация
                    vibration();
                }
            }
        });

        //прослушиватель кнопки перехода на страницу стран
        LoginPage_imageView_forward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, CountryCodePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        constraintLayout_LoginPage_first_container.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, CountryCodePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        textView_LoginPage_phone_number.addTextChangedListener(new TextWatcher() {

            //после
            @Override
            public void afterTextChanged(Editable s) {
            }

            //до
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                last_length=s.length();            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                if(s.length()>last_length)
                {
                    //удалить подсказки на фоне
                    if(textView_LoginPage_phone_number.length()!=0)
                    {
                        hint_text= String.valueOf(hint_LoginPage_phone_number.getText());
                        char[] charArray = hint_text.toCharArray();
                        if(charArray[textView_LoginPage_phone_number.length()-1]=='0')
                        {
                            charArray[textView_LoginPage_phone_number.length()-1]='\u2007';
                        }
                        hint_LoginPage_phone_number.setText(String.valueOf(charArray));
                    }


                    int length_of_number_code=textView_LoginPage_phone_number_code.length();
                    String text_of_textView_LoginPage_phone_number=textView_LoginPage_phone_number.getText().toString();
                    int length_of_numbers=text_of_textView_LoginPage_phone_number.length();
                    String without_space=textView_LoginPage_phone_number.getText().toString().replaceAll("\\s+","");
                    StringBuilder stringBuilder = new StringBuilder(without_space);
                    if(length_of_number_code==1)
                    {
                        if(length_of_numbers>=7)
                        {
                            stringBuilder.insert(3, " ");
                            stringBuilder.insert(7, " ");
                            setText_and_Focus(stringBuilder);

                        }
                        else if(length_of_numbers>=3 && length_of_numbers<7)
                        {
                            stringBuilder.insert(3, " ");
                            setText_and_Focus(stringBuilder);
                        }
                    }
                    else if(length_of_number_code==2)
                    {
                        if(length_of_numbers>=4)
                        {
                            stringBuilder.insert(4, " ");
                            setText_and_Focus(stringBuilder);
                        }
                    }
                    else if(length_of_number_code==3)
                    {
                        if(length_of_numbers>=6)
                        {
                            stringBuilder.insert(2, " ");
                            stringBuilder.insert(6, " ");
                            setText_and_Focus(stringBuilder);
                        }
                        else if(length_of_numbers>=2 && length_of_numbers<6)
                        {
                            stringBuilder.insert(2, " ");
                            setText_and_Focus(stringBuilder);
                        }
                    }
                    else if(length_of_number_code==4)
                    {
                        if(length_of_numbers>=3)
                        {
                            stringBuilder.insert(3, " ");
                            setText_and_Focus(stringBuilder);
                        }
                    }
                }
                //добавить подсказки на фоне по мере удаления
                else if(s.length()<last_length)
                {
                    hint_text = String.valueOf(hint_LoginPage_phone_number.getText());
                    char[] charArray = hint_text.toCharArray();
                    //удалить подсказки на фоне
                    if(textView_LoginPage_phone_number.length()!=0) {
                        if (charArray[textView_LoginPage_phone_number.length()] == '\u2007') {
                            charArray[textView_LoginPage_phone_number.length()] = '0';
                        }
                        hint_LoginPage_phone_number.setText(String.valueOf(charArray));
                    }
                }
            }

        });
        textView_LoginPage_phone_number_code.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                set_background_code();
            }
        });
    }

    public void set_background_code()
    {
        Editable s=textView_LoginPage_phone_number_code.getText();
        if(s.length()!=0)
        {
            textView_LoginPage_phone_number.setEnabled(true);
            country_list.clear();
            String help_storage=String.valueOf(s);
            country_list.addAll(mDBHelper.get_country_name_with_code(help_storage));
            if(country_list.size()!=0)
            {
                textView_show_country.setText(country_list.get(0).getCountry_flag()+"    "+country_list.get(0).getCountry_name());
                if(!mDBHelper.get_country_code_availability(help_storage))
                {
                    textView_LoginPage_phone_number.requestFocus();
                    hint_text="";
                    int length_of_number_code=textView_LoginPage_phone_number_code.length();
                    Log.d("length_of_number_code","length_of_number_code");
                    if(length_of_number_code==1)
                    {
                        textView_LoginPage_phone_number.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12) });
                        Log.d("in_first","in_first");
                        for(int i=0;i<11-length_of_number_code;i++)
                        {
                            hint_text+="0";
                            if(hint_text.length()==3 || hint_text.length()==7)
                            {
                                hint_text+=" ";
                            }
                        }
                    }
                    else if(length_of_number_code==2)
                    {
                        textView_LoginPage_phone_number.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
                        for(int i=0;i<11-length_of_number_code;i++)
                        {
                            hint_text+="0";
                            if(hint_text.length()==4)
                            {
                                hint_text+=" ";
                            }
                        }
                    }
                    else if(length_of_number_code==3)
                    {
                        textView_LoginPage_phone_number.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
                        for(int i=0;i<11-length_of_number_code;i++)
                        {
                            hint_text+="0";
                            if(hint_text.length()==2 || hint_text.length()==6)
                            {
                                hint_text+=" ";
                            }
                        }
                    }
                    else if(length_of_number_code==4)
                    {
                        textView_LoginPage_phone_number.setFilters(new InputFilter[] { new InputFilter.LengthFilter(8) });
                        for(int i=0;i<11-length_of_number_code;i++)
                        {
                            hint_text+="0";
                            if(hint_text.length()==3)
                            {
                                hint_text+=" ";
                            }
                        }
                    }
                    hint_LoginPage_phone_number.setText(hint_text);
                }
            }
            else
            {
                hint_LoginPage_phone_number.setText("");
                textView_LoginPage_phone_number.setText("");
                textView_show_country.setText("Некорректный код страны");
                textView_LoginPage_phone_number.setEnabled(false);
            }
        }
        else
        {
            hint_LoginPage_phone_number.setText("");
            textView_LoginPage_phone_number.setText("");
            textView_show_country.setText("Выберите страну");
            textView_LoginPage_phone_number.setEnabled(false);
        }

        if (s.length() == 4 && !textView_LoginPage_phone_number.hasFocus()) {
            if(mDBHelper.get_country_code_availability_light(String.valueOf(textView_LoginPage_phone_number_code.getText().charAt(0)))){
                textView_LoginPage_phone_number.requestFocus();
                textView_LoginPage_phone_number.setFilters(new InputFilter[] { new InputFilter.LengthFilter(12) });
                hint_LoginPage_phone_number.setText("\u2007\u2007\u2007 000 0000");
                textView_LoginPage_phone_number.setText(textView_LoginPage_phone_number_code.getText().toString().substring(1));
                textView_LoginPage_phone_number_code.setText(String.valueOf(textView_LoginPage_phone_number_code.getText().charAt(0)));
            }

            textView_LoginPage_phone_number.setSelection(textView_LoginPage_phone_number.getText().length());
            textView_LoginPage_phone_number.requestFocus();
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
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

                    //вибрация и тряска
                    if(textView_LoginPage_phone_number_code.length()!=0)
                    {
                        vibration();
                    }
                    else
                    {
                        //анимация
                        constraintLayout_LoginPage_number_container.startAnimation(AnimationUtils.loadAnimation(LoginPage.this,R.anim.shake));
                    }

                    //перенос фокуса и обновление подсказки
                    if (textView_LoginPage_phone_number.length() == 0)
                    {
                        if (textView_LoginPage_phone_number_code.getText().length() > 0) {
                            String text = textView_LoginPage_phone_number_code.getText().toString();
                            textView_LoginPage_phone_number_code.setText(text.substring(0, text.length() - 1));
                        }
                        textView_LoginPage_phone_number_code.setSelection(textView_LoginPage_phone_number_code.getText().length());
                        textView_LoginPage_phone_number_code.requestFocus();
                        focus_country_code=true;
                    }

                    //удаление элемента
                    if(focus_country_code && textView_LoginPage_phone_number_code.hasFocus())
                    {
                        focus_country_code=false;
                    }
                    else if(textView_LoginPage_phone_number_code.hasFocus())
                    {
                        if(textView_LoginPage_phone_number_code.length()!=0)
                        {
                            textView_LoginPage_phone_number_code.setText(textView_LoginPage_phone_number_code.getText().toString().substring(0, textView_LoginPage_phone_number_code.length() - 1));
                            textView_LoginPage_phone_number_code.setSelection(textView_LoginPage_phone_number_code.getText().length());
                        }
                    }
                    else if(textView_LoginPage_phone_number.hasFocus())
                    {
                        if(textView_LoginPage_phone_number.length()!=0)
                        {
                            textView_LoginPage_phone_number.setText(textView_LoginPage_phone_number.getText().toString().substring(0, textView_LoginPage_phone_number.length() - 1));
                            textView_LoginPage_phone_number.setSelection(textView_LoginPage_phone_number.getText().length());
                        }
                    }

                    //обновление посказки
                    if (textView_LoginPage_phone_number.length() == 0) {
                        hint_text = String.valueOf(hint_LoginPage_phone_number.getText());
                        if(hint_text.length()!=0)
                        {
                            char[] charArray = hint_text.toCharArray();
                            charArray[0] = '0';
                            hint_LoginPage_phone_number.setText(String.valueOf(charArray));
                        }
                    }
                    if(textView_LoginPage_phone_number.getText().length()>2)
                    {
                        StringBuilder stringBuilder = new StringBuilder(String.valueOf(textView_LoginPage_phone_number.getText()));
                        if(stringBuilder.charAt(textView_LoginPage_phone_number.getText().length()-1)==' ')
                        {
                            stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(" "));
                            textView_LoginPage_phone_number.setText(stringBuilder);
                            textView_LoginPage_phone_number.setSelection(textView_LoginPage_phone_number.getText().length());
                            textView_LoginPage_phone_number.requestFocus();
                        }

                    }
                    //важно
                    replace_numbers();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + v.getId());
            }

        }
    };

    @Override
    protected void onDestroy() {
        mDBHelper.close();
        super.onDestroy();
    }
}

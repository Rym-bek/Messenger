package com.example.telegram.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.telegram.models.Country;
import com.example.telegram.models.Slide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "telegram_local.db";
    private static String DB_PATH = "";
    private static final int DB_VERSION = 7;

    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        this.mContext = context;

        copyDataBase();

        this.getReadableDatabase();
    }

    public void updateDataBase() throws IOException {
        if (mNeedUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();

            copyDataBase();

            mNeedUpdate = false;
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            mNeedUpdate = true;
    }


    //специальные методы
    public void addUserPhoneNumber(String phoneNumber)
    {
        ContentValues values = new ContentValues();
        values.put("phoneNumber", phoneNumber);
        mDataBase.insert("userInfo", null, values);
    }

    //получить информацию о слайдах
    public List get_slider_info(){
        List<Slide> slider_list = new ArrayList<>();
        String query ="SELECT * FROM slider";
        Cursor cursor = mDataBase.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String slide_image = cursor.getString(1);
            String slide_main = cursor.getString(2);
            String slide_description = cursor.getString(3);
            slider_list.add(new Slide(id,slide_image,slide_main,slide_description));
            cursor.moveToNext();
        }
        cursor.close();

        return slider_list;
    }

    //получить информацию о странах
    public List get_country_info(){
        List<Country> country_list = new ArrayList<>();
        String query ="SELECT * FROM country_codes";
        Cursor cursor = mDataBase.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String country_flag = cursor.getString(1);
            String country_name = cursor.getString(2);
            String country_code = cursor.getString(3);
            country_list.add(new Country(id,country_flag,country_name,country_code));
            cursor.moveToNext();
        }
        cursor.close();

        //сортировка
        Collections.sort(country_list, new Comparator<Country>() {
            public int compare(Country o1, Country o2) {
                return o1.getCountry_name().compareTo(o2.getCountry_name());
            }
        });

        return country_list;
    }

    //найти страну по коду
    public List get_country_name_with_code(String needed_code){
        List<Country> country_list = new ArrayList<>();
        String query ="SELECT * FROM country_codes WHERE country_code = "+needed_code;
        Cursor cursor = mDataBase.rawQuery(query, null);
        if(cursor!=null)
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(0);
                String country_flag = cursor.getString(1);
                String country_name = cursor.getString(2);
                String country_code = cursor.getString(3);
                country_list.add(new Country(id,country_flag,country_name,country_code));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return country_list;
    }

    public boolean get_country_code_availability_light(String needed_code){
        String query ="SELECT country_code FROM country_codes WHERE country_code LIKE "+needed_code;
        Cursor cursor = mDataBase.rawQuery(query, null);
        cursor.moveToFirst();
        if(cursor.getCount()!=0)
        {
            return true;
        }
        else
        {
            return false;
        }

    }

    //найти страну по коду
    public boolean get_country_code_availability(String needed_code){
        boolean bool_storage=true;
        String query ="SELECT country_code, INSTR(country_code, "+needed_code+") FROM country_codes WHERE INSTR(country_code, "+needed_code+")==1";
        Cursor cursor = mDataBase.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.d("WOW_WOW_0",cursor.getString(0));
            if(cursor.getInt(1)==1)
            {
                if(cursor.getString(0).length()>needed_code.length())
                {
                    bool_storage=true;
                    break;
                }
                else
                {
                    bool_storage=false;
                }
            }
            cursor.moveToNext();
        }
        Log.d("WOW_WOW_bool",String.valueOf(bool_storage));
        return bool_storage;

    }


}

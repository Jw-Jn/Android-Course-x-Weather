package com.example.win.weather.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by win on 2016/6/11.
 */
public class DBHelper extends SQLiteOpenHelper{

    public DBHelper(Context context) {
        super(context,"weatherDB",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table City ("
                + "id integer primary key autoincrement, "
                + "city_name text, "
                + "city_code text,"
                +"province_id integer)");
        db.execSQL( "create table Province ("
                + "id integer primary key autoincrement, "
                + "province_name text, "
                + "province_code text)");
        db.execSQL("create table County ("
                + "id integer primary key autoincrement, "
                + "county_name text, "
                + "county_code text, "
                + "city_id integer)");
        db.execSQL("create table Course ("
                +"id integer primary key autoincrement, "
                + "day text, "
                + "start integer, "
                + "step integer, "
                + "teacher text, "
                + "name text, "
                + "classroom text, "
                + "course_id text, "
                + "stu_id text)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }
}
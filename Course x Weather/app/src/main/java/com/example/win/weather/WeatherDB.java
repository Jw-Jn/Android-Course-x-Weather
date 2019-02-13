package com.example.win.weather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.win.weather.db.City;
import com.example.win.weather.db.County;
import com.example.win.weather.db.Course;
import com.example.win.weather.db.Province;
import com.example.win.weather.helper.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by win on 2016/6/11.
 */
public class WeatherDB {
    private static WeatherDB weatherDB;
    private static SQLiteDatabase db;

    private WeatherDB(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static WeatherDB getInstance(Context context) {
        if (weatherDB == null) {
            weatherDB = new WeatherDB(context);
        }
        return weatherDB;
    }

    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<>();
        Cursor cursor = db
                .query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor
                        .getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor
                        .getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("City", null, values);
        }
    }

    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("City", null, "province_id = ?",
                new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor
                        .getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor
                        .getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());
            db.insert("County", null, values);
        }
    }
    public List<County> loadCounties(int cityId) {
        List<County> list = new ArrayList<>();
        Cursor cursor = db.query("County", null, "city_id = ?",
                new String[] { String.valueOf(cityId) }, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor
                        .getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor
                        .getColumnIndex("county_code")));
                county.setCityId(cityId);
                list.add(county);
            } while (cursor.moveToNext());
        }
        return list;
    }
    public static void saveCourse(Course course) {
        if (course != null) {
            ContentValues values = new ContentValues();
            values.put("stu_id", course.getStuId());
            values.put("course_id", course.getCourseId());
            values.put("name",course.getName());
            values.put("teacher", course.getTeacher());
            values.put("classroom",course.getClassroom());
            values.put("step",course.getStep());
            values.put("start",course.getStart());
            values.put("day",course.getDay());
            db.insert("Course", null, values);
        }
    }
    public static boolean queryStuId(String stuId){
        Cursor cursor = db.query("Course", null, "stu_id = ?",new String[] {stuId},null, null, null);
        int i = 0;
        if(cursor.moveToFirst()){
            do{
                i+=1;
            }while(cursor.moveToNext());
        }
        if(i<=1)
            return true;
        return false;
    }
    public List[] loadCourses(String stuId){
        List[] list = new ArrayList[5];
        List<Course> mon = new ArrayList<>();
        List<Course> tue = new ArrayList<>();
        List<Course> wed = new ArrayList<>();
        List<Course> thu = new ArrayList<>();
        List<Course> fri = new ArrayList<>();
        Cursor cursor = db.query("Course", null, "stu_id = ?",
                new String[] {stuId }, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Course course = new Course();
                String day = cursor.getString(cursor.getColumnIndex("day"));
                course.setCourseId(cursor.getString(cursor.getColumnIndex("course_id")));
                course.setName(cursor.getString(cursor.getColumnIndex("name")));
                course.setTeacher(cursor.getString(cursor.getColumnIndex("teacher")));
                course.setClassroom(cursor.getString(cursor.getColumnIndex("classroom")));
                course.setStep(cursor.getInt(cursor.getColumnIndex("step")));
                course.setStart(cursor.getInt(cursor.getColumnIndex("start")));
                course.setDay(day);
                if(day.contains("周一")){
                    mon.add(course);
                }else if(day.contains("周二")){
                    tue.add(course);
                }
                else if(day.contains("周三")){
                    wed.add(course);
                }
                else if(day.contains("周四")){
                    thu.add(course);
                }else if(day.contains("周五")){
                    fri.add(course);
                }
            } while (cursor.moveToNext());
        }
        list[0] = mon;
        list[1] = tue;
        list[2] = wed;
        list[3] = thu;
        list[4] = fri;System.out.println("loadCourse");
        return list;
    }

}
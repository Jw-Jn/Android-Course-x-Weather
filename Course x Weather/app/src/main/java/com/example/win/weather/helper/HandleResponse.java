package com.example.win.weather.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.win.weather.WeatherDB;
import com.example.win.weather.db.City;
import com.example.win.weather.db.County;
import com.example.win.weather.db.Course;
import com.example.win.weather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by win on 2016/6/11.
 */
public class HandleResponse {
    public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    weatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCitiesResponse(WeatherDB weatherDB,String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    weatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCountiesResponse(WeatherDB weatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    weatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject data = jsonObject.getJSONObject("data");
            String cityName = data.getString("city");
            String temp = data.getString("wendu");
            String ganmao = data.getString("ganmao");
            JSONArray forecast = data.getJSONArray("forecast");
            saveWeatherInfo(context,cityName,temp,ganmao,forecast);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void saveWeatherInfo(Context context, String cityName,String temp,String ganmao,JSONArray forecast) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.M.d",
                Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("temp", temp);
        editor.putString("ganmao",ganmao);
        editor.putString("forecast",forecast.toString());
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }

    public static String handleLocationResponse(String response) {
        try {
            System.out.println(response);
            JSONObject jsonObject = new JSONObject(response);
            JSONObject row = jsonObject.getJSONObject("row");
            JSONObject result = row.getJSONObject("result");
            JSONObject addressComponent = result.getJSONObject("addressComponent");
            String city = addressComponent.getString("city");
            String district = addressComponent.getString("district");
            String province = addressComponent.getString("province");
            return "已定位："+province+","+city+","+district;
            } catch (JSONException e) {
            e.printStackTrace();
        }
        return "place no found";
    }
    public static String handleCourseResponse(WeatherDB weatherDB, String response, String stuId) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            int state = jsonObject.getInt("state");
            if(state == 200) {
                String stuName = jsonObject.getString("stuName");
                JSONArray jsonArray = jsonObject.getJSONArray("courses");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    String classroom = item.getString("classroom");
                    if (classroom.contains(";")) {
                        String[] cr = classroom.split(";");
                        for (int j = 0; j < cr.length; j++) {
                            Course course = new Course();
                            course.setStuId(stuId);
                            course.setCourseId(item.getString("id"));
                            course.setName(item.getString("name"));
                            course.setClassroom(cr[j].substring(cr[j].indexOf("(") + 1, cr[j].lastIndexOf(")")));
                            course.setTeacher(item.getString("teacher"));
                            String[] temp = cr[j].substring(cr[j].indexOf("周") + 2, cr[j].indexOf("(")).split(",");
                            course.setStart(Integer.parseInt(temp[0]));
                            course.setStep(temp.length);
                            course.setDay(cr[j].substring(cr[j].indexOf("周"), cr[j].indexOf("周") + 2));
                            weatherDB.saveCourse(course);
                        }
                    } else {
                        Course course = new Course();
                        course.setStuId(stuId);
                        course.setCourseId(item.getString("id"));
                        course.setName(item.getString("name"));
                        course.setClassroom(classroom.substring(classroom.indexOf("(") + 1, classroom.lastIndexOf(")")));
                        course.setTeacher(item.getString("teacher"));
                        String[] temp = classroom.substring(classroom.indexOf("周") + 2, classroom.indexOf("(")).split(",");
                        course.setStart(Integer.parseInt(temp[0]));
                        course.setStep(temp.length);
                        course.setDay(classroom.substring(classroom.indexOf("周"), classroom.indexOf("周") + 2));
                        weatherDB.saveCourse(course);
                    }
                }
            return stuName;
            }else if(state == 404) {
                return "id not found";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "error";
    }
}

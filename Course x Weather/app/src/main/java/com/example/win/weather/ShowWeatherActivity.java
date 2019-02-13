package com.example.win.weather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.win.weather.helper.HandleResponse;
import com.example.win.weather.helper.HttpCallbackListener;
import com.example.win.weather.helper.HttpUtil;
import com.example.win.weather.helper.VibratorHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowWeatherActivity extends Activity {

    private TextView city_name;
    private TextView publish;
    private TextView time;
    private TextView today_type;
    private TextView today_temp;
    private ListView forecast_list;
    private SensorManager sensorManager;
    private int tag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show_weather);
        city_name = (TextView) findViewById(R.id.city_name);
        publish = (TextView) findViewById(R.id.publish);
        time = (TextView) findViewById(R.id.time);
        today_temp = (TextView) findViewById(R.id.today_temp);
        today_type = (TextView) findViewById(R.id.today_type);
        forecast_list = (ListView) findViewById(R.id.forecast_list);

        String from = getIntent().getStringExtra("from");
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            publish.setText("同步中...");
            city_name.setVisibility(View.INVISIBLE);
            today_temp.setVisibility(View.INVISIBLE);
            today_type.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            showWeather();
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(listener,sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(sensorManager!=null){
            sensorManager.unregisterListener(listener);
        }
    }
    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float xValue = Math.abs(event.values[0]);
            float yValue = Math.abs(event.values[1]);
            float zValue = Math.abs(event.values[2]);
            if (xValue > 18 || yValue > 18 || zValue > 18) {
                tag += 1;
                if(tag == 6) {
                    VibratorHelper.Vibrate(ShowWeatherActivity.this, 300);
                    publish.setText("同步中...");
                    today_temp.setVisibility(View.INVISIBLE);
                    today_type.setVisibility(View.INVISIBLE);
                    updateWeather();
                    showWeather();
                    Toast.makeText(ShowWeatherActivity.this, "同步完成", Toast.LENGTH_SHORT).show();
                    tag = 0;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" +
                countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }

    private void queryWeatherInfo(String weatherCode) {
        String address = "http://wthrcdn.etouch.cn/weather_mini?citykey="+weatherCode;
        queryFromServer(address, "weatherCode");
    }
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    HandleResponse.handleWeatherResponse(ShowWeatherActivity.this,
                            response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publish.setText("同步失败");
                    }
                });
            }
        });
    }

    public void showWeather() {
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        publish.setText("摇一摇同步");
        city_name.setText( prefs.getString("city_name", ""));
        today_temp.setText(prefs.getString("temp", "")+"°");
        time.setText(prefs.getString("current_date", ""));
        city_name.setVisibility(View.VISIBLE);
        today_temp.setVisibility(View.VISIBLE);
        List<Map<String, String>> forecast = new ArrayList<>();
        Map<String, String> map;
        String result = prefs.getString("forecast","");
        try{
            JSONArray jsonArray = new JSONArray(result);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                String high = item.getString("high");
                high = high.substring(2,high.length()-1)+"°";
                String low = item.getString("low");
                low = low.substring(2,low.length()-1)+"°";
                String type = item.getString("type");
                String date = item.getString("date");
                date = date.substring(3);
                map = new HashMap<>();
                map.put("high",high);
                map.put("low",low);
                map.put("type",type);
                map.put("date", date);
                forecast.add(map);
                if(i==0){
                    today_type.setText(type);
                    today_type.setVisibility(View.VISIBLE);
                }
            }
        }catch (JSONException e){

        }
        SimpleAdapter adapter = new SimpleAdapter(ShowWeatherActivity.this,forecast,R.layout.list_item2,new String[]{"high","low","type","date"},new int[]{R.id.temp1,R.id.temp2,R.id.type,R.id.date});
        forecast_list = (ListView) findViewById(R.id.forecast_list);
        forecast_list.setAdapter(adapter);
        Toast.makeText(ShowWeatherActivity.this,"同步完成",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,SelectActivity.class);
        intent.putExtra("back",true);
        startActivity(intent);
            finish();
        }

    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        String city_name = prefs.getString("city_name", "");
        String address = "http://wthrcdn.etouch.cn/weather_mini?city="+city_name;
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                HandleResponse.handleWeatherResponse(ShowWeatherActivity.this,
                        response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}

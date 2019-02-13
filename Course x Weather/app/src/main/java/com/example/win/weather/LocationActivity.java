package com.example.win.weather;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.example.win.weather.helper.HandleResponse;
import com.example.win.weather.helper.HttpCallbackListener;
import com.example.win.weather.helper.HttpUtil;

public class LocationActivity extends Activity {
    private LocationManager locationManager;
    private String result = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgressDialog();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0,
                locationListener);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle
                extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            updateLocation(location);
        }
    };

    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(locationListener);
        }
    }

    private void updateLocation(Location location) {
        if (location != null) {
            String address = "http://lbs.juhe.cn/api/getaddressbylngb?lngx=" + location.getLongitude() + "&lngy=" + location.getLatitude();
            HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    result = HandleResponse.handleLocationResponse(response);
                }

                @Override
                public void onError(Exception e) {
                    result = "connect error";
                }
            });
            System.out.println(result);
            if (result != null && !result.contains("error")) {
                Toast.makeText(LocationActivity.this, result, Toast.LENGTH_SHORT).show();
                address = "http://wthrcdn.etouch.cn/weather_mini?city=" + result.split(",")[1];
                HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        HandleResponse.handleWeatherResponse(LocationActivity.this, response);
                        Intent intent = new Intent(LocationActivity.this, ShowWeatherActivity.class);
                        intent.putExtra("from", "location");
                        closeProgressDialog();
                        startActivity(intent);
                        if (ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        locationManager.removeUpdates(locationListener);
                    }
                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                                Toast.makeText(LocationActivity.this,
                                        "加载失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }else{
            String lat = "无法获取地理信息，请稍后...";
            Toast.makeText(LocationActivity.this, lat, Toast.LENGTH_SHORT).show();
        }
    }
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在获取地理位置...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}

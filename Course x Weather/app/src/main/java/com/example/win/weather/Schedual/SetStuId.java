package com.example.win.weather.Schedual;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.win.weather.R;
import com.example.win.weather.WeatherDB;
import com.example.win.weather.helper.HandleResponse;
import com.example.win.weather.helper.HttpCallbackListener;
import com.example.win.weather.helper.HttpUtil;

public class SetStuId extends Activity {
    private EditText editText;
    private Button submit;
    private SharedPreferences stu_id;
    private SharedPreferences.Editor editor;
    private WeatherDB weatherDB;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_stu_id);
        editText = (EditText) findViewById(R.id.stu_id);
        submit = (Button) findViewById(R.id.submit);
        stu_id = PreferenceManager.getDefaultSharedPreferences(this);
        weatherDB = WeatherDB.getInstance(SetStuId.this);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editText.getText().toString();
                editor = stu_id.edit();
                showProgressDialog();
                if(checkId(id)) {
                    editor.putString("stuId", id);
                    editor.commit();
                    Toast.makeText(SetStuId.this, "set success", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(SetStuId.this, "set failed", Toast.LENGTH_SHORT).show();
            }
        });}
        public boolean checkId(final String stuId) {
            String address = "http://172.29.97.180:8080/getSchedual?id=" + stuId;
            final String[] result = new String[1];
            boolean flag = true;
            if (weatherDB.queryStuId(stuId)) {
                HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                            result[0] = HandleResponse.handleCourseResponse(weatherDB, response, stuId);
                    }
                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                });
                while (result[0] == null){

                }
                if(result[0].equals("id not found")||result[0].equals("error"))
                    flag = false;
            }
            closeProgressDialog();
            return flag;
        }
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
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

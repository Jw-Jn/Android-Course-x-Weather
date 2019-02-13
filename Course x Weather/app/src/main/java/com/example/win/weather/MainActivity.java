package com.example.win.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.win.weather.Email.EmailSend;
import com.example.win.weather.Schedual.GetSchedual;
import com.example.win.weather.Schedual.SetStuId;
import com.igexin.sdk.PushManager;

import java.util.ArrayList;

public class MainActivity extends Activity{
    private DrawerLayout drawerLayout;
    private ListView drawerlist;
    private ArrayList<String> menuLists;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushManager.getInstance().initialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerlist = (ListView) findViewById(R.id.left_drawer);
        menuLists = new ArrayList<String>();
        menuLists.add("定位");
        menuLists.add("城市列表");
        menuLists.add("课程表");
        menuLists.add("设置学号");
        menuLists.add("意见反馈");
        adapter = new ArrayAdapter<String>(this,R.layout.list_item1, menuLists);
        drawerlist.setAdapter(adapter);
        drawerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (menuLists.get(position).toString().equals("定位")) {
                    Toast.makeText(MainActivity.this,"请打开位置功能",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                    startActivity(intent);
                } else if (menuLists.get(position).toString().equals("城市列表")) {
                    Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                    startActivity(intent);
                } else if (menuLists.get(position).toString().equals("意见反馈")) {
                    Intent intent = new Intent(MainActivity.this, EmailSend.class);
                    startActivity(intent);
                } else if (menuLists.get(position).toString().equals("课程表")) {
                    Intent intent = new Intent(MainActivity.this, GetSchedual.class);
                    startActivity(intent);
                }else if(menuLists.get(position).toString().equals("设置学号")){
                    Intent intent = new Intent(MainActivity.this, SetStuId.class);
                    startActivity(intent);
                }
            }
        });}}

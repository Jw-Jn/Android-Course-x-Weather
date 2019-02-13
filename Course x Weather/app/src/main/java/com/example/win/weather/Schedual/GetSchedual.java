package com.example.win.weather.Schedual;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.win.weather.R;
import com.example.win.weather.WeatherDB;
import com.example.win.weather.db.Course;

import java.util.ArrayList;
import java.util.List;

public class GetSchedual extends Activity {
    private WeatherDB weatherDB;
    LinearLayout weekPanels[]=new LinearLayout[5];
    List coursesInfo[]=new ArrayList[5];
    int itemHeight;
    int marTop,marLeft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedual);
        weatherDB = WeatherDB.getInstance(GetSchedual.this);
        SharedPreferences prefs = PreferenceManager.
                getDefaultSharedPreferences(this);
        String stuId = prefs.getString("stuId", "");

        itemHeight=getResources().getDimensionPixelSize(R.dimen.weekItemHeight);
        marTop=getResources().getDimensionPixelSize(R.dimen.weekItemMarTop);
        marLeft=getResources().getDimensionPixelSize(R.dimen.weekItemMarLeft);

        coursesInfo = weatherDB.loadCourses(stuId);

        for (int i = 0; i < weekPanels.length; i++) {
            weekPanels[i]=(LinearLayout) findViewById(R.id.weekPanel_1+i);
            initWeekPanel(weekPanels[i], coursesInfo[i]);
        }

    }

    public void initWeekPanel(LinearLayout linearLayout,List<Course>data){
        if(linearLayout==null || data==null || data.size()<1)return;
        Course pre=data.get(0);
        for (int i = 0; i < data.size(); i++) {
            Course c =data.get(i);
            TextView tv =new TextView(this);
            LinearLayout.LayoutParams lp =new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT ,
                    itemHeight*c.getStep()+marTop*(c.getStep()-1));
            if(i>0){
                lp.setMargins(marLeft, (c.getStart()-(pre.getStart()+pre.getStep()))*(itemHeight+marTop)+marTop, 0, 0);
            }else{
                lp.setMargins(marLeft, (c.getStart()-1)*(itemHeight+marTop)+marTop, 0, 0);
            }
            tv.setLayoutParams(lp);
            tv.setGravity(Gravity.TOP);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setTextSize(12);
            tv.setTextColor(getResources().getColor(R.color.courseTextColor));
            tv.setText(c.getName()+"\n"+c.getClassroom()+"\n"+c.getTeacher());
            tv.setBackgroundColor(getResources().getColor(R.color.colorgray));
            linearLayout.addView(tv);
            pre=c;
        }
    }
}
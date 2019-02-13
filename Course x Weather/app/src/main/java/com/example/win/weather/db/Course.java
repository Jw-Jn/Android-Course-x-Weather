package com.example.win.weather.db;

/**
 * Created by win on 2016/6/16.
 */
public class Course {
    private int id;
    private String teacher;
    private String name;
    private String classroom;
    private String courseId;
    private String stuId;
    private int start;
    private int step;
    private String day;

    public Course(){}

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getStuId() {return stuId;}

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public int getStart() {return start;}

    public int getStep() {return step;}

    public void setStart(int start) {this.start = start;}

    public void setStep(int step) {this.step = step;}

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}

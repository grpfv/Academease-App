package com.example.acdms_profile;

import com.google.firebase.Timestamp;

public class CourseModel {

    String subject;
    String instructor;
    String endTime;
    String startTime;
    String schedDay;
    Timestamp timestamp;

    public CourseModel() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getSchedDay() {
        return schedDay;
    }

    public void setSchedDay(String schedDay) {
        this.schedDay = schedDay;
    }
}
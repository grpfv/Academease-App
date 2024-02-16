package com.example.acdms_profile;

public class SchedModel {
    String schedSubject;
    String day;
    String subjectTime;
    String instructor;

    public SchedModel() {
    }

    public SchedModel(String schedSubject, String day, String subjectTime, String instructor) {
        this.schedSubject = schedSubject;
        this.day = day;
        this.subjectTime = subjectTime;
        this.instructor = instructor;
    }

    public String getSchedSubject() {
        return schedSubject;
    }

    public void setSchedSubject(String schedSubject) {
        this.schedSubject = schedSubject;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getSubjectTime() {
        return subjectTime;
    }

    public void setSubjectTime(String startTime) {
        this.subjectTime = startTime;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    // New methods to get start and end times separately
    public String getStartTime() {
        if (subjectTime != null && subjectTime.contains(" - ")) {
            return subjectTime.split(" - ")[0];
        }
        return "";
    }

    public String getEndTime() {
        if (subjectTime != null && subjectTime.contains(" - ")) {
            return subjectTime.split(" - ")[1];
        }
        return "";
    }


}

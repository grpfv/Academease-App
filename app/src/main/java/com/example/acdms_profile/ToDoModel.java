package com.example.acdms_profile;

import com.google.firebase.firestore.Exclude;

public class ToDoModel {
    String title;
    String subject;
    String dueDay;
    String dueTime;
    String remindDay;
    String remindTime;
    String dueDateTime;

    public ToDoModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDueDay() { return dueDay; }

    public void setDueDay(String dueDay) { this.dueDay = dueDay;}

    public String getDueTime() { return dueTime; }

    public void setDueTime(String dueTime) {this.dueTime = dueTime;}

    public String getRemindDay() {
        return remindDay;
    }

    public void setRemindDay(String remindDay) {
        this.remindDay = remindDay;
    }

    public String getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(String remindTime) {
        this.remindTime = remindTime;
    }

    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getDueDateTime() {
        return dueDateTime;
    }

    public void setDueDateTime(String dueDateTime) {
        this.dueDateTime = dueDateTime;
    }
}


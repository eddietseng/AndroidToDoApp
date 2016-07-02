package com.eddietseng.todo;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by eddietseng on 6/14/16.
 */
public class TodoItem implements Serializable {
    private String description;
    private int priority;
    private String date;
    private boolean status;

    public TodoItem() {
        this.priority = 0;
        Calendar calendar = Calendar.getInstance();
        DateFormat format = DateFormat.getDateTimeInstance();
        this.date = format.format(calendar.getTime());
        this.status = false;
    }

    public TodoItem(String description, int priority, String date) {
        this.description = description;
        this.priority = priority;
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getStatus() { return status; }

    public void setStatus(boolean status) { this.status = status; }

    @Override
    public String toString() {
        return "TodoItem{" +
                "description='" + description + '\'' +
                ", priority='" + priority + '\'' +
                ", date='" + date + '\'' +
                ", status='" + (status ? "done":"todo") + '\'' +
                '}';
    }
}

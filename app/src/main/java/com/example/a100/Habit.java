package com.example.a100;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Habit{

    @PrimaryKey(autoGenerate = true)
    private int id = 0;

    private String habitName;
    private String duration;
    private boolean startsTomorrow;
    private long createdDate;
    private int didDays;
    private String count;
    private String checkedDate;
    private int doCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHabitName() {
        return habitName;
    }

    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public boolean isStartsTomorrow() {
        return startsTomorrow;
    }

    public void setStartsTomorrow(boolean startsTomorrow) {
        this.startsTomorrow = startsTomorrow;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public int getDidDays() {
        return didDays;
    }

    public void setDidDays(int didDays) {
        this.didDays += didDays;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        if(count.equals("")){
            this.count = "1";
        } else {
            this.count = count;
        }
    }

    public String getCheckedDate() {
        return checkedDate;
    }

    public void setCheckedDate(String checkedDate) {
        this.checkedDate = checkedDate;
    }

    public int getDoCount() {
        return doCount;
    }

    public void setDoCount(int doCount) {
        this.doCount += doCount;
    }

    public void setDoCountZero(){
        this.doCount = 0;
    }

}

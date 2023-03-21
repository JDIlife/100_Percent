package com.example.a100;

public class Habit {
    private String habitName;
    private String duration;
    private boolean startsTomorrw;
    private long createdDate;
    private int didDays;
    private String count;

    public Habit(String habitName, String duration, boolean startsTomorrow, long createdDate, String count){
        this.habitName = habitName;
        this.duration = duration;
        this.startsTomorrw = startsTomorrw;
        this.createdDate = createdDate;
        this.didDays = 0;
        this.count = count;
    }

    public String getName(){
        return habitName;
    }

    public String getDuration(){
        return duration;
    }

    public boolean getStartDate(){
        return startsTomorrw;
    }

    public long getCreatedDate(){
        return createdDate;
    }

    public int getDidDays(){
        return didDays;
    }

    public String getCount(){
        return count;
    }

    public void setDidDays(){
        this.didDays++;
    }
}

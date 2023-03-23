package com.example.a100;

public class Habit {
    private String habitName;
    private String duration;
    private boolean startsTomorrw;
    private long createdDate;
    private int didDays;
    private String count;
    private String checkedDate;
    private int doCount;

    public Habit(String habitName, String duration, boolean startsTomorrow, long createdDate, String count){
        this.habitName = habitName;
        this.duration = duration;
        this.startsTomorrw = startsTomorrw;
        this.createdDate = createdDate;
        this.didDays = 0;
        if(count.equals("")){ // 사용자가 횟수를 따로 입력하지 않으면 기본으로 횟수를 1로 설정한다
            this.count = "1";
        }else{
            this.count = count;
        }
        this.checkedDate = ""; // currentDate.getCheckedDate(); 로 값을 불렀을 때 초기값이 없어서 오류가나는 것을 방지
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

    public String getCheckedDate(){
        return checkedDate;
    }

    public void setCreatedDate(String checkedDate){
        this.checkedDate = checkedDate;
    }

    public int getDoCount(){
        return doCount;
    }

    public void setDoCount(){
        this.doCount++;
    }

    public void setDoCountZero(){
        this.doCount = 0;
    }
}

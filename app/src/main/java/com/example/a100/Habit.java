package com.example.a100;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Habit implements Parcelable {

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

    public Habit(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    // 직렬화용 메서드
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(habitName);
        dest.writeString(duration);
        // writeBoolean 은 최소 sdk 요구가 29 버전이라서 사용하려면 minsdk를 29로 올리거나, boolean을 int형태로 변환해서 저장하는 방법이 있는데
        // 하위 호환을 위해 후자의 방법을 선택했다
        dest.writeInt(startsTomorrow ? 1: 0);
        dest.writeLong(createdDate);
        dest.writeInt(didDays);
        dest.writeString(count);
        dest.writeString(checkedDate);
        dest.writeInt(doCount);
    }
    // 역직렬화용 생성자
    protected Habit(Parcel in) {
        id = in.readInt();
        habitName = in.readString();
        duration = in.readString();
        startsTomorrow = in.readByte() != 0;
        createdDate = in.readLong();
        didDays = in.readInt();
        count = in.readString();
        checkedDate = in.readString();
        doCount = in.readInt();
    }

    // 역직렬화용 메서드
    public static final Creator<Habit> CREATOR = new Creator<Habit>() {
        @Override
        public Habit createFromParcel(Parcel in) {
            return new Habit(in);
        }

        @Override
        public Habit[] newArray(int size) {
            return new Habit[size];
        }
    };

    // getter && setter
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

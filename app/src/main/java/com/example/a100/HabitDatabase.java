package com.example.a100;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Habit.class}, version = 2, exportSchema = false)
abstract public class HabitDatabase extends RoomDatabase {
    public abstract HabitDao habitDao();

    // 싱글톤 패턴으로 Room Database 구현
    private static HabitDatabase INSTANCE;

    private static final Object sLock = new Object();

    public static HabitDatabase getInstance(Context context){
        synchronized (sLock){
            if(INSTANCE==null){
                INSTANCE= Room.databaseBuilder(context.getApplicationContext(), HabitDatabase.class, "Habit.db")
                        .build();
            }
            return INSTANCE;
        }
    }

}

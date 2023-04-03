package com.example.a100;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface HabitDao {

    @Insert
    void setInsertHabit(Habit habit);

    @Update
    void setUpdateHabit(Habit habit);

    @Delete
    void setDeleteHabit(Habit habit);

    @Query("SELECT * FROM Habit WHERE id = :id")
    Habit getHabitById(int id);

    @Query("SELECT * FROM Habit")
    List<Habit> getHabitAll();

}


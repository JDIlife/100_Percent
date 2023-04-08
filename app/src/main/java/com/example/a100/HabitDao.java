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

    @Query("SELECT * FROM Habit")
    List<Habit> getHabitAll();


    @Query("UPDATE Habit SET diary = :newDiary WHERE id = :habitId")
    void setUpdateDiary(int habitId, List<String> newDiary);

    @Query("UPDATE Habit SET goal = :newGoal WHERE id = :habitId")
    void setUpdateGoal(int habitId, String newGoal);

}


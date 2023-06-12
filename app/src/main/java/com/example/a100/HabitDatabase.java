package com.example.a100;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Habit.class}, version = 4, exportSchema = false)
@TypeConverters({StringListTypeConverter.class})
abstract public class HabitDatabase extends RoomDatabase {

    public abstract HabitDao habitDao();

    // 싱글톤 패턴으로 Room Database 구현
    private static HabitDatabase INSTANCE;

    private static final Object sLock = new Object();

    public static HabitDatabase getInstance(Context context){
        synchronized (sLock){
            if(INSTANCE==null){
                INSTANCE= Room.databaseBuilder(context.getApplicationContext(), HabitDatabase.class, "Habit.db")
                        // TypeConverter 추가
                        .addTypeConverter(new StringListTypeConverter())
                        // 마이그레이션 추가
                        .addMigrations(MIGRATION_3_4)
                        .build();
            }
            return INSTANCE;
        }
    }

    // 수동 마이그레이션 코드
    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Habit ADD COLUMN diary TEXT");
            database.execSQL("ALTER TABLE habit ADD COLUMN endDate TEXT");
        }
    };
}

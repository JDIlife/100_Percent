package com.example.a100;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomDialog.OnSaveClickListener{

    private ListView habitListView;
    private List<Habit> habitList;
    private HabitAdapter habitAdapter;

    private HabitDao mHabitDao;
    private HabitDatabase mHabitDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        HabitDatabase database = Room.databaseBuilder(getApplicationContext(), HabitDatabase.class, "hundred.db")
//                .fallbackToDestructiveMigration() // 데이터베이스 버전 변경 가능
//                .allowMainThreadQueries() // Main 쓰레드에서 DB에 입출력 가능하게 설정
//                .build();
//
//        mHabitDao = database.habitDao();


        mHabitDatabase = HabitDatabase.getInstance(this);
        mHabitDao = mHabitDatabase.habitDao();

        habitListView = findViewById(R.id.habit_listview);
        List<Habit> addedHabitList = new ArrayList<>();
        habitList = mHabitDao.getHabitAll();
//        habitAdapter = new HabitAdapter(this, habitList);
//        habitListView.setAdapter(habitAdapter);

        Habit[] habit_array = habitList.toArray(new Habit[habitList.size()]);
        for (int i = 0; i < habitList.size(); i++) {
            addedHabitList.add(habit_array[i]);
        }

        habitAdapter = new HabitAdapter(this, addedHabitList);
        habitListView.setAdapter(habitAdapter);

        // 누르면 CustomDialog 를 띄워주는 버튼 지정
        Button addHabitButton = findViewById(R.id.add_habit_btn);
        addHabitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CustomDialog customDialog = new CustomDialog(MainActivity.this, MainActivity.this);
                customDialog.show();
            }
        });

    }

    // 커스텀 다이얼로그의 save_btn 을 누르면 Room 에 입력받은 데이터를 저장한다
    @Override
    public void onSaveClicked(Habit habit){
//        HabitDatabase database = Room.databaseBuilder(getApplicationContext(), HabitDatabase.class, "hundred.db")
//                .fallbackToDestructiveMigration() // 데이터베이스 버전 변경 가능
//                .allowMainThreadQueries() // Main 쓰레드에서 DB에 입출력 가능하게 설정
//                .build();

        mHabitDatabase = HabitDatabase.getInstance(this);
        mHabitDao = mHabitDatabase.habitDao();

//        mHabitDao = database.habitDao();
        mHabitDao.setInsertHabit(habit);

        // habitAdapter에 데이터의 변경을 알려준다
        habitAdapter.notifyDataSetChanged();
    }

}

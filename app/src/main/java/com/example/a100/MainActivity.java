package com.example.a100;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        habitListView = findViewById(R.id.habit_listview);
        habitList = new ArrayList<>();
        habitAdapter = new HabitAdapter(this, habitList);
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

    @Override
    public void onSaveClicked(Habit habit){
        HabitDatabase database = Room.databaseBuilder(getApplicationContext(), HabitDatabase.class, "hundred.db")
                .fallbackToDestructiveMigration() // 데이터베이스 버전 변경 가능
                .allowMainThreadQueries() // Main 쓰레드에서 DB에 입출력 가능하게 설정
                .build();

        mHabitDao = database.habitDao();

        mHabitDao.setInsertHabit(habit); // => 여기서 어플이 강제로 종료되는 오류가 난다! (version 숫자를 2로 올려주니까 종료는 안된다!!)

        habitAdapter.notifyDataSetChanged();
    }

}

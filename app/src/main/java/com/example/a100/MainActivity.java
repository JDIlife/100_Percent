package com.example.a100;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomDialog.OnSaveClickListener{

    private ListView habitListView;
    private List<Habit> habitList;
    private HabitAdapter habitAdapter;

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
    public void onSaveClicked(String habitName, String duration, boolean startsTomorrow, long createdDate, String count){
        habitList.add(new Habit(habitName, duration, startsTomorrow, createdDate, count));
        habitAdapter.notifyDataSetChanged();
    }

}

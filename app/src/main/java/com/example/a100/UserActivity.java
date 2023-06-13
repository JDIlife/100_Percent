package com.example.a100;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserActivity extends AppCompatActivity {

    private HabitDao mHabitDao;
    private HabitDatabase mHabitDatabase;
    private List<Habit> habitList;

    private int endHabitNum = 0;
    private long averageEndPercent;

    class DBGetThread implements Runnable{
        @Override
        public void run(){
            try {
                habitList = mHabitDao.getHabitAll();
            } catch (Exception e){
                // error Handling
                Log.d("database error", "database error");
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // room 데이터베이스 초기화
        mHabitDatabase = HabitDatabase.getInstance(this);
        mHabitDao = mHabitDatabase.habitDao();

        DBGetThread dbGetThread = new DBGetThread();
        Thread t = new Thread(dbGetThread);
        t.start();

        // 데이터베이스에서 데이터를 가져올 때 까지 기다린다
        try {
            t.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        long totalPercent = 0;

        LocalDate today = LocalDate.now();
        // habitList 를 반복하여 값을 얻는다
        for(Habit h: habitList){
            String habitEndDate = h.getEndDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate endDate = LocalDate.parse(habitEndDate, formatter);

            if(endDate.isBefore(today)){ // today 보다 endDate 가 오래되면 true 가 된다
                // 종료된 습관의 개수에 1씩 더한다
                endHabitNum += 1;

                long duration = Long.parseLong(h.getDuration());
                int didDays = h.getDidDays();

                // 종료된 해당 습관의 실천 퍼센트를 구한 뒤 totalPercent 에 더한다
                long percent = (didDays / duration) * 100;
                totalPercent += percent;

            }
        }

        // 평균 습관 퍼센트를 구한다
        averageEndPercent = totalPercent / habitList.size();

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("");
        ab.setDisplayHomeAsUpEnabled(true);

        // ui 요소 지정
        TextView completedHabitNum = (TextView) findViewById(R.id.completed_habit_num);
        TextView completedHabitPercent = (TextView) findViewById(R.id.completed_habit_percent);

        completedHabitNum.setText(endHabitNum + " 개");
        completedHabitPercent.setText(String.valueOf(averageEndPercent));

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home: { // 툴바 뒤로가기 활성화
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
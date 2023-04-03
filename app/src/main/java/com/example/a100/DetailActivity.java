package com.example.a100;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private HabitDao mHabitDao;
    private HabitDatabase mHabitDatabase;
    private Habit habit;
    private List<Habit> habitList;

    class DBDeleteThread implements Runnable{

        @Override
        public void run(){
            try {
                // onCreate() 에서 this.habit = habit 으로 초기화된 habit을 이용해서 습관 삭제
                mHabitDao.setDeleteHabit(habit);

            } catch (Exception e){
                // error Handling
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // room 데이터베이스 초기화
        mHabitDatabase = HabitDatabase.getInstance(this);
        mHabitDao = mHabitDatabase.habitDao();

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("");
        ab.setDisplayHomeAsUpEnabled(true);

        Habit habit = getIntent().getParcelableExtra("habit");
        // onCreate() 하면서 인스턴스 변수인 habit을 초기화
        this.habit = habit;

        String habitName = habit.getHabitName();
        String duration = habit.getDuration();
        boolean startsTomorrow = habit.isStartsTomorrow();
        int didDays = habit.getDidDays();
        long createdDate = habit.getCreatedDate();

        // ============== Detail UI 지정 ============//

        TextView circleDuration = (TextView) findViewById(R.id.sub_circle_duration);
        TextView circlePassedDate = (TextView) findViewById(R.id.sub_circle_passed_date);
        TextView habitNameTextView = (TextView) findViewById(R.id.sub_habit_name);
        TextView habitProgressTextView = (TextView) findViewById(R.id.sub_habit_progress);

        EditText diaryInput = (EditText) findViewById(R.id.diary_input);
        Button diaryInputBtn = (Button) findViewById(R.id.diary_input_btn);

        // 기존에 저장된 습관생성날짜와 현재의 날짜 비교용
        GregorianCalendar today = new GregorianCalendar();
        SimpleDateFormat todayDateFormat = new SimpleDateFormat("yyyyMMdd");
        String todayDate = todayDateFormat.format(today.getTime());

        // **습관 생성날짜와 조회날짜를 비교해서 습관을 생성하고 며칠이 지났는지 보여준다
        // 1.일자를 조회하는 오늘 날짜를 가져온다
        long nowDate = today.getTimeInMillis();
        // 2.처음 습관을 생성했던 날짜와 차이를 계산해서 지나간 날짜를 얻는다
        long diffSec = (nowDate - createdDate) / 1000;
        long passedDate = diffSec / (24*60*60);

        circleDuration.setText(duration);
        circlePassedDate.setText(String.valueOf(passedDate));
        habitNameTextView.setText(habitName);

        habitProgressTextView.setText(passedDate + "일 중" + didDays + "일 달성   ");

        if(startsTomorrow){ // 습관을 내일부터 시작한 경우
            if(passedDate != 0){
                int doPercent = (int) ((didDays / (float)passedDate) * 100);
                habitProgressTextView.setText(passedDate + "일 중 " + didDays + "일 달성    " + doPercent +"%");
                circlePassedDate.setText(String.valueOf(passedDate));
            } else if(passedDate == 0 && didDays == 0){
                habitProgressTextView.setText(passedDate + "일 중 " + didDays + "일 달성    " + "    100%");
                circlePassedDate.setText(String.valueOf(passedDate));
            }
        } else { // 습관을 오늘부터 시작한 경우
            int doPercent = (int) ((didDays / (float)(passedDate + 1)) * 100);
            habitProgressTextView.setText((passedDate + 1) + "일 중 " + didDays + "일 달성    " + doPercent +"%");
            circlePassedDate.setText(String.valueOf(passedDate + 1));
        }

    }

    // menu 를 그려준다
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home: { // 툴바의 뒤로가기 버튼을 누르면 홈화면으로 이동한다
                finish();
                return true;
            }
            // 더보기 메뉴에 달리는 기능들
            case R.id.set_goal: { // 목표 추가하기
                Toast.makeText(this, "set goal", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.edit_habit: { // 습관 수정하기

                Toast.makeText(this, "edit habit", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.delete_habit: { // 습관 삭제하기

                // Room DB의 습관 삭제
                DBDeleteThread dbDeleteThread = new DBDeleteThread();
                Thread t = new Thread(dbDeleteThread);
                t.start();

                // mainActivity 로 돌아감
                finish();
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }
}

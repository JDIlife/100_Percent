package com.example.a100;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Intent 를 받아 해당하는 변수를 만들고 초기화한다
        Intent intent = getIntent();

        String habitName = intent.getStringExtra("habitName");
        String duration = intent.getStringExtra("duration");
        int didDays = intent.getIntExtra("didDays", 0);
        String count = intent.getStringExtra("count");
        long createdDate = intent.getLongExtra("createdDate", 0);
        boolean startsTomorrow = intent.getBooleanExtra("startsTomorrow", false);

        // ============== Detail UI 지정 ============//

        TextView circleDuration = (TextView) findViewById(R.id.sub_circle_duration);
        TextView circlePassedDate = (TextView) findViewById(R.id.sub_circle_passed_date);
        TextView habitNameTextView = (TextView) findViewById(R.id.sub_habit_name);
        TextView habitProgressTextView = (TextView) findViewById(R.id.sub_habit_progress);

        Button backBtn = (Button) findViewById(R.id.goback_btn);
        Button seeMoreBtn = (Button) findViewById(R.id.see_more_btn);
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
}
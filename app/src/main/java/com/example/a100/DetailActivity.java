package com.example.a100;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private HabitDao mHabitDao;
    private HabitDatabase mHabitDatabase;
    private Habit habit;
    private List<Habit> habitList;
    private List<String> diaryList = new ArrayList<>();
    private String goal;

    TextView goalTextView;

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

    // diary 만 업데이트하는 스레드
    class DBInputDiaryThread implements Runnable{
        @Override
        public void run(){
            mHabitDao.setUpdateDiary(habit.getId(), diaryList);
        }
    }

    // gaol 만 업데이트하는 스레드
    class DBInputGoalThread implements Runnable{
        @Override
        public void run(){
            mHabitDao.setUpdateGoal(habit.getId(), goal);
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

        TextView goalTextView = (TextView) findViewById(R.id.goal_textview);

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

        CharSequence str = habit.getGoal();
        goalTextView.setText("목표: " + str);

        diaryInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diaryList.add(String.valueOf(diaryInput.getText()));

                DBInputDiaryThread dbInputDiaryThread = new DBInputDiaryThread();
                Thread t = new Thread(dbInputDiaryThread);
                t.start();
            }
        });

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
                EditText et1 = new EditText(DetailActivity.this);

                // 다이얼로그의 OK 버튼을 누르면 수행될 리스너
                DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goal = String.valueOf(et1.getText());

                        goalTextView = findViewById(R.id.goal_textview);
                        goalTextView.setText("목표: " + goal);
                        DBInputGoalThread dbInputGoalThread = new DBInputGoalThread();
                        Thread t = new Thread(dbInputGoalThread);
                        t.start();
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("목표를 입력하세요")
                        .setView(et1)
                        .setPositiveButton("ok", okListener)
                        .setNegativeButton("cancel", null)
                        .show();

                break;
            }
            case R.id.edit_habit: { // 습관 수정하기

                Toast.makeText(this, "edit habit", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.delete_habit: { // 습관 삭제하기

                // 다이얼로그의 OK 버튼을 누르면 수행될 리스너
                DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBDeleteThread dbDeleteThread = new DBDeleteThread();
                        Thread t = new Thread(dbDeleteThread);
                        t.start();
                        finish();
                    }
                };

                // 기본 다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("정말 습관을 삭제하시겠습니까?")
                        .setPositiveButton("OK", okListener)
                        .setNegativeButton("Cancel", null)
                        .show();

                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }
}

/*
    goal 이 왜 최신화가 안되나?

    처음 습관 생성 habit.gaol == ""  ==> 목표 추가 habit.goal = "목표" ==> habit.setUpdate(goal) ==> 뒤로 가기 ==> 다시 습관 상세 페이지 ==> setText(habit.getGoal())

    이 로직대로면 목표를 설정했다가 뒤로 나갔다가 다시 돌아왔을 때 정상적으로 습관이 갱신돼서 상세 페이지에 보여야하는데, 실제로는 빈 화면만 나온다...

    1. 데이터가 제대로 저장되지 않았나?? ==> DB를 확인해보면 goal 데이터는 정상적으로 업데이트 되었다!!

    완전히 다시 어플을 시작했을 때를 보면 goalTextView 에 제대로 setText를 하지 못하는 모습이다.
    2. setText() 는 charSequence 인데, getGoal 은 String 을 반환해서 그런가?? ==> habit.getGoal() 을 CharSequence 로 형변한 한 뒤에 setText() 안에 넣었지만 여전히 안된다

    3. 목표가 goal 에는 잘 저장되어있는데, 막상 "목표"+ 를 붙여서 확인해보니 getGoal() 이 null 이다??!! ==> 그래서 빈화면이 나왔던 것!
    ===> 아하!! 인텐트에 goal 을 설정해주지 않아서 그렇다!! 습관 상세목표이지로 ㄷ
 */

package com.example.a100;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
                // onCreate() 에서 this.habit = habit 으로 초기화된 habit 을 이용해서 습관 삭제
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

        // 기존에 존재하는 diary 항목들을 가져온다
        this.diaryList = habit.getDiary();

        // ========== 상세 페이지 RecyclerView 설정 부분
        List<String> dataSet = habit.getDiary();

        RecyclerView recyclerView = findViewById(R.id.diary_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        DiaryListAdapter diaryListAdapter = new DiaryListAdapter(dataSet);
        recyclerView.setAdapter(diaryListAdapter);


        // ============ 입력 버튼을 누르면 습관 일지가 저장되는 부분
        diaryInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 습관 일지 작성일자
                GregorianCalendar today = new GregorianCalendar();
                SimpleDateFormat todayDateFormat = new SimpleDateFormat("yyyyMMdd");
                String todayDate = todayDateFormat.format(today.getTime());

                // 사용자가 습관일지를 입력할 때 자동으로 작성일자를 더해서 저장한다
                String diary = todayDate + String.valueOf(diaryInput.getText());

                // 기존에 존재하는 diary 항목에 추가해서 저장한다
                diaryList.add(diary);

                DBInputDiaryThread dbInputDiaryThread = new DBInputDiaryThread();
                Thread t = new Thread(dbInputDiaryThread);
                t.start();

                // 사용자가 습관 일지 추가 버튼을 누르면 자동으로 diaryInput 에 써있던 텍스트를 지우고, 가상 키보드를 숨긴다
                diaryInput.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(diaryInput.getWindowToken(), 0);

                // 습관 일지 추가 버튼을 누르면 곧바로 RecyclerView 에 적용되도록 어뎁터를 설정해준다
                List<String> dataSet = habit.getDiary();

                RecyclerView recyclerView = findViewById(R.id.diary_recycler_view);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DetailActivity.this);
                recyclerView.setLayoutManager(linearLayoutManager);

                DiaryListAdapter diaryListAdapter = new DiaryListAdapter(dataSet);
                recyclerView.setAdapter(diaryListAdapter);
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

    // 사용자가 EditText 를 눌러 키보드가 올라온 상태에서 다른 영역을 터치하면 키보드가 숨겨지는 기능
    // Activity 클래스의 메서드인 dispatchTouchEvent 사용 : 별도의 호출이 없어도 활동 창에서 터치 이벤트가 발생할 때마다 자동으로 호출됨
    public boolean dispatchTouchEvent(MotionEvent ev){
        // 현재 인풋 포커스를 가진 화면을 가져온다
        View view = getCurrentFocus();
        // view 가 존재하는지, EditText 의 요소인지, android.webkit 패키지가 아닌지 확인
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit")) {
            // EditText view 의 화면 좌표를 얻는다
            int scrcoods[] = new int[2];
            view.getLocationOnScreen(scrcoods);
            // EditText view 로 부터 상대적으로 터치 이벤트가 일어난 위치를 얻는다
            float x = ev.getRawX() + view.getLeft() - scrcoods[0];
            float y = ev.getRawY() + view.getTop() - scrcoods[1];
            // 터치 이벤트가 EditText view 바깥에서 일어났는지 확인
            if(x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()){
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}

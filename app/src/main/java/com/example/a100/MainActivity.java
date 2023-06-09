package com.example.a100;

import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import java.util.List;


public class MainActivity extends AppCompatActivity implements CustomDialog.OnSaveClickListener {

    private ListView habitListView;
    private List<Habit> habitList;
    private HabitAdapter habitAdapter;

    private HabitDao mHabitDao;
    private HabitDatabase mHabitDatabase;


    // ==== 앱 처음 실행시 DB의 내용을 가져와서 ListView에 보여주기 위한 runnable
    // 별도의 쓰레드를 만들어서 DB에 접근하기
    class DBRunnable implements Runnable{

        @Override
        public void run(){
            try {
                // db 접근
                habitList = mHabitDao.getHabitAll();
                habitAdapter = new HabitAdapter(MainActivity.this, habitList);
                habitListView.setAdapter(habitAdapter); // => 습관 삭제 이후에 habitListView를 제대로 잡지 못하고있다.
            } catch (Exception e){
                // error Handling
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ======== 필요한 UI 요소 지정
        habitListView = findViewById(R.id.habit_listview);

        // room 데이터베이스 초기화
        mHabitDatabase = HabitDatabase.getInstance(this);
        mHabitDao = mHabitDatabase.habitDao();


        // 별도의 쓰레드로 실행
        DBRunnable dbRunnable = new DBRunnable();
        Thread t = new Thread(dbRunnable);
        t.start();

        // 누르면 CustomDialog 를 띄워주는 버튼 지정
        Button addHabitButton = findViewById(R.id.add_habit_btn);
        addHabitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog(MainActivity.this, MainActivity.this);
                customDialog.show();
            }
        });

        // 사용자 액티비티로 이동하는 이미지 버튼 지정
        ImageButton userImgBtn = (ImageButton) findViewById(R.id.user_img_btn);
        userImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });

        // listview 의 아이템을 클릭하면 상세 화면으로 이동!
        habitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);

                // 선택된 listViewItem 의 Room 데이터를 DetailActivity Intent 로 전달한다
                Habit clickedHabit = (Habit)parent.getItemAtPosition(position);
                intent.putExtra("habit", clickedHabit);
                startActivity(intent);
            }
        });

    }

    // 커스텀 다이얼로그의 save_btn 을 누르면 Room 에 입력받은 데이터를 저장한다
    @Override
    public void onSaveClicked(Habit habit) {

        /// runnable, runOnUiThread 를 사용해서 버튼을 클릭해 습관을 생성하면 바로 ListView로 업데이트 되도록 하는 기능
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                habitAdapter = new HabitAdapter(MainActivity.this, habitList);
                habitListView.setAdapter(habitAdapter);
            }
        };

        class HabitInsertThread implements Runnable{
            @Override
            public void run(){
                // DB에 데이터 저장, 조회
                mHabitDao.setInsertHabit(habit);
                habitList = mHabitDao.getHabitAll();

                // Main 쓰레드로 전달
                runOnUiThread(runnable);
            }
        }

        HabitInsertThread habitInsertThread = new HabitInsertThread();
        Thread t = new Thread(habitInsertThread);
        t.start();

    }

    @Override
    protected void onResume(){
        super.onResume();

        // UI 스레드(Main Thread) 에서 habitListview 의 어댑터를 설정한다(기본값 null인 상태)
        habitListView.setAdapter(habitAdapter);

        DBResumeThread dbResumeThread = new DBResumeThread();
        Thread t = new Thread(dbResumeThread);
        t.start();

    }

    class DBResumeThread implements Runnable{
        @Override
        public void run(){
            try {
                // 습관 삭제 이후 최신화된 Room DB 를 불러와서 새로운 어댑터를 만든다
                habitList = mHabitDao.getHabitAll();
                habitAdapter = new HabitAdapter(MainActivity.this, habitList);
                // 새로운 어댑터를 habitListView 에 부착한다. habitAdapter 가 null 이 아니라 새롭게 만든 어댑터로 초기화된다
                habitListView.setAdapter(habitAdapter);

            } catch (Exception e){
                // error Handling
            }
        }
    }

}
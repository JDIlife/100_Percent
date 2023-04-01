package com.example.a100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
                habitListView.setAdapter(habitAdapter);
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

        // listview 의 아이템을 클릭하면 상세 화면으로 이동!
        habitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), ""+id, Toast.LENGTH_SHORT).show(); // position을 통해 몇번째를 불렀는지 알 수 있다.

                // 문제: chkBtn.setEnabled(false) 가 되어있지 않은 상태에서는 이 메서드를 실행할 수 없다!
                // 체크버튼의 xml 요소에서 focusable=false 로 설정해서 해결!!
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

}

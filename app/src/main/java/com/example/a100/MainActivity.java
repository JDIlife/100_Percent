package com.example.a100;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

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

//                habitAdapter.notifyDataSetChanged(); // 여기도 안적어줘도 된다??!! 이걸로 view를 업데이트 하는게 아닌가?
                // 어차피 Adapter로 데이터 받았고, setAdapter로 연결했으니까 그냥 바로 보여주기만 하면 되는건가?
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
                //habitAdapter.notifyDataSetChanged(); // 있어도 되고 없어도 정상동작?? 이 부분이 데이터가 바뀌었음을 감지하고 view를 다시 그리는 거아닌가?
                // 그렇다면 이걸 안쓰면 비정상작동해야하는데, 안써도 멀쩡하다
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

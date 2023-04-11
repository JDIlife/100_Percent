package com.example.a100;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class CustomDialog extends Dialog  implements View.OnClickListener{

    private EditText habitNameEditText;
    private EditText durationEditText;
    private Switch startsTodaySwitch;
    private EditText countEditText;


    Habit passedHabit;

    // listener 인터페이스
    public interface OnSaveClickListener{
        void onSaveClicked(Habit habit);
    }

    private OnSaveClickListener onSaveClickListener;

    // CustomDialog 생성자
    public CustomDialog(@NonNull Context context, OnSaveClickListener onSaveClickListener){
        super(context);
        this.onSaveClickListener = onSaveClickListener;
    }

    // 습관을 수정하는 경우에 사용하는 생성자 (수정하기 이전의 기존 habit 을 넘겨받는다)
    public CustomDialog(Context context, OnSaveClickListener onSaveClickListener, Habit passedHabit){
        super(context);
        this.onSaveClickListener = onSaveClickListener;
        this.passedHabit = passedHabit;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dialog);

        // CustomDialog 입력 요소 지정
        habitNameEditText = findViewById(R.id.habit_name);
        durationEditText = findViewById(R.id.duration_edittext);
        startsTodaySwitch = findViewById(R.id.starts_today);
        countEditText = findViewById(R.id.count);

        // 습관을 수정한다면 기존의 데이터를 미리 입력값으로 세팅한다
        if (passedHabit.getId() != 0){
            habitNameEditText.setText(passedHabit.getHabitName());
            durationEditText.setText(passedHabit.getDuration());
            startsTodaySwitch.setChecked(passedHabit.isStartsTomorrow());
        }

        // CustomDialog 의 Ok 버튼과 Cancel 버튼을 지정하고 이벤트를 등록
        Button saveBtn = findViewById(R.id.save_btn);
        Button cancelBtn = findViewById(R.id.cancel_btn);
        saveBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            // Ok 버튼을 눌렀을 때 동작
            case R.id.save_btn:

                Habit habit = new Habit();

                habit.setHabitName(habitNameEditText.getText().toString());
                habit.setDuration(durationEditText.getText().toString());
                habit.setStartsTomorrow(startsTodaySwitch.isChecked());

                // 습관 생성일자를 가져옴 (습관을 시작하고 지나간 날짜를 계산하기 위해서)
                Calendar habitCreatedDate = new GregorianCalendar();
                long createdDate = habitCreatedDate.getTimeInMillis();
                habit.setCreatedDate(createdDate);

                habit.setCount(countEditText.getText().toString());

                // ** 습관 생성일자를 포멧만 바꿔서 기본 체크일자로 저장 (어플 실행시 null 오류 방지)
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                String checkedDate = dateFormat.format(createdDate);
                habit.setCheckedDate(checkedDate);

                // 나중에 사용자가 습관 상세 페이지에서 입력할 값들이 null 이 되지 않도록 초기화
                habit.setGoal("");
                List<String> defaultDiary = new ArrayList<>();
                habit.setDiary(defaultDiary);

                // 습관 수정시 변하지 않는 부분은 기존의 데이터로 셋팅한다
                if(passedHabit.getId() != 0){
                    habit.setId(passedHabit.getId());
                    habit.setCreatedDate(passedHabit.getCreatedDate());
                    habit.setGoal(passedHabit.getGoal());
                    habit.setDiary(passedHabit.getDiary());
                }

                // 리스너 인터페이스 함수 호출
                onSaveClickListener.onSaveClicked(habit);
                dismiss();
                break;

            // Cancel 버튼을 눌렀을 때 동작
            case R.id.cancel_btn:
                dismiss();
                break;
        }
    }
}

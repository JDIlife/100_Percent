package com.example.a100;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

public class CustomDialog extends Dialog  implements View.OnClickListener{

    private EditText habitNameEditText;
    private EditText durationEditText;
    private Switch startsTodaySwitch;
    private EditText countEditText;

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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_dialog);

        // CustomDialog 입력 요소 지정
        habitNameEditText = findViewById(R.id.habit_name);
        durationEditText = findViewById(R.id.duration_edittext);
        startsTodaySwitch = findViewById(R.id.starts_today);
        countEditText = findViewById(R.id.count);

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
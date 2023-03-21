package com.example.a100;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class HabitAdapter extends ArrayAdapter<Habit> {

    private Context context;
    private List<Habit> habitList;

    public HabitAdapter(Context context, List<Habit> habitList){
        // HabitAdapter 의 조상인 (상속받은) ArrayAdapter의 생성자 사용
        super(context, 0, habitList);
        this.context = context;
        this.habitList = habitList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View listItemView = convertView;
        if(listItemView  == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.listview_item, parent, false);
        }

        Habit currentHabit = getItem(position);

        //---------- 실제로 listview 에 listitem 을 그려주는 부분 -----------------//
        TextView nameTextView = listItemView.findViewById(R.id.habit_name);
        if(currentHabit.getCount().equals("1")){
            nameTextView.setText(currentHabit.getName());
        } else {
            nameTextView.setText(currentHabit.getName() + "    " + "/" + currentHabit.getCount());
        }

        TextView durationTextView = listItemView.findViewById(R.id.circle_duration);
        durationTextView.setText(String.valueOf(currentHabit.getDuration()));

        TextView circlePassedDate = listItemView.findViewById(R.id.circle_passed_date);

        // **습관 생성날짜와 조회날짜를 비교해서 습관을 생성하고 며칠이 지났는지 보여준다
            // 1.일자를 조회하는 오늘 날짜를 가져온다
        Calendar today = new GregorianCalendar();
        long nowDate = today.getTimeInMillis();
            // 2.처음 습관을 생성했던 날짜와 차이를 계산해서 지나간 날짜를 얻는다
        long diffSec = (currentHabit.getCreatedDate() - nowDate) / 1000;
        long passedDate = diffSec / (24*60*60);
            // 3.setText() 는 안의 숫자를 id값으로 인식하기 때문에 숫자를 그대로 넣으면 오류가 난다!!
        circlePassedDate.setText(String.valueOf(passedDate));

        TextView progressTextView = listItemView.findViewById(R.id.habit_progress);

        // 처음 습관을 만들었을 때 기본값을 보여준다
        int didDays = currentHabit.getDidDays();
        progressTextView.setText(passedDate + "일 중 " + didDays + "일 달성" + "    0%");

        // check Button click event
        Button chkBtn = listItemView.findViewById(R.id.check_btn);
        chkBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // 체크버튼이 눌릴 때마다 setDidDays()로 didDays를 늘린 이후에 getDidDays()로 증가된 값을 가져와서 보여준다
                currentHabit.setDidDays();
                int didDays = currentHabit.getDidDays();
                // 바로 나누면 0/0 이 되어버려서 자동으로 어플이 종료된다
                if(didDays != 0 && passedDate != 0){
                    int doPercent = (int) (didDays / passedDate) * 100;
                    progressTextView.setText(passedDate + "일 중 " + didDays + "일 달성" + "    "  +"%");
                } else {
                    progressTextView.setText(passedDate + "일 중 " + didDays + "일 달성" + "    100%");
                }


            }
        });

        return listItemView;
    }
}
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
        GregorianCalendar today = new GregorianCalendar();
        SimpleDateFormat todayDateFormat = new SimpleDateFormat("yyyyMMdd");
        String todayDate = todayDateFormat.format(today.getTime());

        TextView nameTextView = listItemView.findViewById(R.id.habit_name);
        if(currentHabit.getCount().equals("1")){
            nameTextView.setText(currentHabit.getName());
        } else if(!(currentHabit.getCheckedDate().equals(todayDate))){ // 습관을 마지막으로 체크한 날짜와 어플을 켰을 때 날짜가 다르면 0으로 초기화
            nameTextView.setText(currentHabit.getName() + "    " + "0/" + currentHabit.getCount());
        }


        TextView durationTextView = listItemView.findViewById(R.id.circle_duration);
        durationTextView.setText(String.valueOf(currentHabit.getDuration()));

        TextView circlePassedDate = listItemView.findViewById(R.id.circle_passed_date);

        // **습관 생성날짜와 조회날짜를 비교해서 습관을 생성하고 며칠이 지났는지 보여준다
            // 1.일자를 조회하는 오늘 날짜를 가져온다
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
            int doCount = 0;
            public void onClick(View v){
                // 체크버튼이 눌릴 때마다 setDidDays()로 didDays를 늘린 이후에 getDidDays()로 증가된 값을 가져와서 보여준다
                //currentHabit.setDidDays();
                int didDays = currentHabit.getDidDays();
                // 바로 나누면 0/0 이 되어버려서 자동으로 어플이 종료된다
                if(didDays != 0 && passedDate != 0){
                    int doPercent = (int) (didDays / passedDate) * 100;
                    progressTextView.setText(passedDate + "일 중 " + didDays + "일 달성" + "    "  +"%");
                } else {
                    progressTextView.setText(passedDate + "일 중 " + didDays + "일 달성" + "    100%");
                }

                // ** 체크버튼을 클릭한 날짜를 기록함
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                GregorianCalendar checkedMoment = new GregorianCalendar();
                String checkedDate = dateFormat.format(checkedMoment.getTime());
                currentHabit.setCreatedDate(checkedDate);

                if(doCount < Integer.valueOf(currentHabit.getCount())){
                    ++doCount;
                    nameTextView.setText(currentHabit.getName() + "    " + doCount + "/" + currentHabit.getCount());
                } else if(doCount == Integer.valueOf(currentHabit.getCount())){
                    currentHabit.setDidDays();
                    chkBtn.setEnabled(false);
                    int didDays2 = currentHabit.getDidDays();
                    progressTextView.setText(passedDate + "일 중 " + didDays2 + "일 달성" + "    100%");
                }
            }
        });

        return listItemView;
    }
}

/* 날짜 지남 알고리즘
    -- 굳이 매일 단위로 오늘에서 내일로 날짜가 지났음을 감지할 필요는 없다!!
    --> 그냥 오늘의 날짜와 다르기만 하면 되는 것!!
    => 어플을 열었을 때(기존에 저장한 날짜) 비교 (확인한 날짜) ==> 차이가 있다면 0으로 초기화

    1. 처음 체크버튼을 눌렀을 때, 체크 버튼을 누른 시간을 해당 습관에 저장한다 ==> habit에 checkDate 변수 추가
        -- checkDate가 저장되는 시기는 checkBtn이 눌리는 시기마다 저장
        -- millisecond로 가져오는게 아니라 문자열로 년월일까지만 저장하고, 그 저장한 값을 또 현재의 년월일로만 뽑은 값과 대조한다
    2. 앱을 다시 열었을 때, 기존에 저장된 checkDate와 currentDate를 비교해서 같다면 데이터 유지, 다르면 0으로 초기화
        -- 앱을 켜서 listViewItem을 보여주는 시기에 결정

    ** 하루에 횟수를 다 채우고 나야지만 passedDate가 증가하는 알고리즘

    if(currentHabit.getCount() != doCount){

    }

                            (횟수를 다채움) && (하루가 지나지 않음) ==> 그런데 어차피 하루가 지난 상태면 doCount가 0으로 초기화된다 즉, 앞의 조건은 횟수를 다 채워야만 날이 증가하는 용도
    if((currentHabit.getCount() == doCount) && (currentHabit.getCheckedDate().equals(todayDate))){ // ==> 습관의 count가 따로 설정되어 있지 않다면 기본값 1 로 설정되어있을 것이니까 상관없다.
        currentHabit.setDidDays();
        ckeckBtn.setEnabled(false): // 이것도 날짜가 지나면 사용가능하도록 초기화되야함!
    } else if ()

    ** passedDate가 증가하고나면 check버튼이 더 안눌리도록 (습관 횟수 채우면 누른다고 날짜가 채워지지 않도록)


 */
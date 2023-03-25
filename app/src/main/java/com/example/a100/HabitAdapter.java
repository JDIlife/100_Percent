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

        // >>>>>>>>>>>>>>>>>>>---------- 실제로 listview 에 listitem 을 그려주는 부분 -----------------<<<<<<<<<<<<<<<<< //

        // ========== 모든 UI 요소들을 지정한다 ==============//
        TextView nameTextView = listItemView.findViewById(R.id.habit_name);
        Button chkBtn = listItemView.findViewById(R.id.check_btn);
        TextView durationTextView = listItemView.findViewById(R.id.circle_duration);
        TextView circlePassedDate = listItemView.findViewById(R.id.circle_passed_date);
        TextView progressTextView = listItemView.findViewById(R.id.habit_progress);

        // 기존에 저장된 습관생성날짜와 현재의 날짜 비교용
        GregorianCalendar today = new GregorianCalendar();
        SimpleDateFormat todayDateFormat = new SimpleDateFormat("yyyyMMdd");
        String todayDate = todayDateFormat.format(today.getTime());

        // ============ 습관 제목, 횟수 설정 부분 ============== //
        if(currentHabit.getCount().equals("1")){ // 횟수가 기본값인 1로 설정되어있으면 그냥 습관제목만 보여준다
            nameTextView.setText(currentHabit.getHabitName());
        } else if(!(currentHabit.getCheckedDate().equals(todayDate))){ // 습관을 마지막으로 체크한 날짜와 어플을 켰을 때 날짜가 다르면 0으로 초기화
            nameTextView.setText(currentHabit.getHabitName() + "    " + "0/" + currentHabit.getCount());
        }

        // 원부분 습관 총 기간 표시
        durationTextView.setText(String.valueOf(currentHabit.getDuration()));


        // 원 부분 습관 지나간 기간 표시
        // **습관 생성날짜와 조회날짜를 비교해서 습관을 생성하고 며칠이 지났는지 보여준다
            // 1.일자를 조회하는 오늘 날짜를 가져온다
        long nowDate = today.getTimeInMillis();
            // 2.처음 습관을 생성했던 날짜와 차이를 계산해서 지나간 날짜를 얻는다
        long diffSec = (nowDate - currentHabit.getCreatedDate()) / 1000;
        //long passedDate = diffSec / (24*60*60);
        long passedDate = diffSec;
            // 3.setText() 는 안의 숫자를 id값으로 인식하기 때문에 숫자를 그대로 넣으면 오류가 난다!!
        circlePassedDate.setText(String.valueOf(passedDate));



        // 습관 진행 바 내용
        // 처음 습관을 만들었을 때 기본값을 보여준다
        int didDays = currentHabit.getDidDays();
        //progressTextView.setText(passedDate + "일 중 " + didDays + "일 달성" + "    0%"); // => 이 줄이 새로 습관을 생성하면 기존의 습관 진행도를 0으로 만든다
        // 그렇다고 위의 줄을 적지 않으면 처음 습관을 생성할 때 진행바에 아무 내용도 없다.


        // check Button click event
        chkBtn.setOnClickListener(new View.OnClickListener(){
            int doCount = currentHabit.getDoCount();
            int totalCount = Integer.valueOf(currentHabit.getCount());

            public void onClick(View v){

                // ** 체크버튼을 클릭한 날짜를 기록함
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                GregorianCalendar checkedMoment = new GregorianCalendar();
                String checkedDate = dateFormat.format(checkedMoment.getTime());
                currentHabit.setCreatedDate(Long.parseLong(checkedDate));

                // 습관의 횟수보다 적을 때만 버튼을 클릭하면 doCount 증가, 습관 횟수를 모두 채웠으면 chkBtn 비활성화
                if(doCount < (Integer.valueOf(currentHabit.getCount()) - 1)){
                    currentHabit.setDoCount(1);
                    doCount = currentHabit.getDoCount();
                    nameTextView.setText(currentHabit.getHabitName() + "    " + doCount + "/" + currentHabit.getCount());
                } else if(doCount == (Integer.valueOf(currentHabit.getCount())) - 1){
                    currentHabit.setDoCount(1);
                    doCount = currentHabit.getDoCount();
                    nameTextView.setText(currentHabit.getHabitName() + "    " + doCount + "/" + currentHabit.getCount());
                    currentHabit.setDidDays(1);
                    chkBtn.setEnabled(false);

                    int didDays = currentHabit.getDidDays();
                    // 바로 나누면 0/0 이 되어버려서 자동으로 어플이 종료된다
                    if(didDays != 0 && passedDate != 0){
                        int doPercent = (int) ((didDays / (float)passedDate) * 100);
                        progressTextView.setText(passedDate + "일 중 " + didDays + "일 달성    " + doPercent +"%");
                    } else {
                        progressTextView.setText(passedDate + "일 중 " + didDays + "일 달성" + "    100%");
                    }
                }
            }
        });

        return listItemView;
    }

}

// 문제: 습관 생성 -> 체크 -> doPercent 정상 계산 // 새로운 습관 생성 -> 기존의 doPercent 0이 되어버림

/* 날짜 지남 알고리즘

    !! getView() 의 호출시점이 listview에 요소가 추가되거나, 스크롤되거나 하는 상황이다. 그렇기 때문에 기존에 생각했던 아래의 알고리즘은 정상작동하지 않는다
    !! 어플을 열자마자 비교할 수 없기 때문이다. { getView() 의 호출시점 특성상 }

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

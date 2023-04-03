package com.example.a100;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

public class HabitAdapter extends ArrayAdapter<Habit> {

    private Context context;
    private List<Habit> habitList;

    private HabitDao mHabitDao;
    private HabitDatabase mHabitDatabase;

    public HabitAdapter(Context context, List<Habit> habitList){
        // HabitAdapter 의 조상인 (상속받은) ArrayAdapter의 생성자 사용
        super(context, 0, habitList);
        this.context = context;
        this.habitList = habitList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Log.d("Get View Test", "get View is running");
        View listItemView = convertView;
        if(listItemView  == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.listview_item, parent, false);
        }

        // Room 데이터베이스 초기화
        mHabitDatabase = HabitDatabase.getInstance(context);
        mHabitDao = mHabitDatabase.habitDao();

        Habit currentHabit = getItem(position);

        // currentHabit 을 통해 Room 데이터베이스를 업데이트하는 스레드
        class DBUpdateThread implements Runnable{

            @Override
            public void run(){
                try {
                    // db 접근
                    mHabitDao.setUpdateHabit(currentHabit);
                } catch (Exception e){
                    // error Handling
                }
            }
        }
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

        // ===== 습관 제목, 횟수 표시
        if(currentHabit.getCheckedDate().equals(todayDate)){
            nameTextView.setText(currentHabit.getHabitName() + "    " + currentHabit.getDoCount() + "/" + currentHabit.getCount());
        } else if(!(currentHabit.getCheckedDate().equals(todayDate))){ // 습관을 마지막으로 체크한 날짜와 어플을 켰을 때 날짜가 다르면 0으로 초기화
            nameTextView.setText(currentHabit.getHabitName() + "    " + "0/" + currentHabit.getCount());
            currentHabit.setDoCountZero();

            DBUpdateThread dbRunnable = new DBUpdateThread();
            Thread t = new Thread(dbRunnable);
            t.start();
        }

        // ====== 원부분 습관 총 기간 표시
        durationTextView.setText(String.valueOf(currentHabit.getDuration()));

        // ====== 원 부분 습관 지나간 기간 표시
        // **습관 생성날짜와 조회날짜를 비교해서 습관을 생성하고 며칠이 지났는지 보여준다
            // 1.일자를 조회하는 오늘 날짜를 가져온다
        long nowDate = today.getTimeInMillis();
            // 2.처음 습관을 생성했던 날짜와 차이를 계산해서 지나간 날짜를 얻는다
        long diffSec = (nowDate - currentHabit.getCreatedDate()) / 1000;
        long passedDate = diffSec / (24*60*60);

        // ======  습관 진행 바 내용
        // 처음 습관을 만들었을 때 기본값을 보여준다
        int didDays = currentHabit.getDidDays();

        if(currentHabit.isStartsTomorrow()){ // 습관을 내일부터 시작한 경우
            if(passedDate != 0){
                int doPercent = (int) ((didDays / (float)passedDate) * 100);
                progressTextView.setText(passedDate + "일 중 " + didDays + "일 달성    " + doPercent +"%");
                circlePassedDate.setText(String.valueOf(passedDate));
            } else if(passedDate == 0 && didDays == 0){ // 내일부터 시작하는 습관을 만든 당일에는 체크버튼 비활성화
                progressTextView.setText(passedDate + "일 중 " + didDays + "일 달성    " +"    100%");
                circlePassedDate.setText(String.valueOf(passedDate));
                chkBtn.setEnabled(false);
            }
        } else { // 습관을 오늘부터 시작한 경우 (습관을 만든 당일부터 1일로 센다)
            int doPercent = (int) ((didDays / (float)(passedDate + 1)) * 100);
            progressTextView.setText((passedDate + 1) + "일 중 " + didDays + "일 달성    " + doPercent +"%");
            circlePassedDate.setText(String.valueOf(passedDate + 1));
        }

        // ===== 습관 체크버튼 활성화/비활성화 여부 (횟수를 모두 채우고, 동일한 날짜라면 체크버튼 비활성화)
        if((currentHabit.getDoCount() == Integer.valueOf(currentHabit.getCount())) && (currentHabit.getCheckedDate().equals(todayDate))){
            chkBtn.setEnabled(false);
        }

        // 체크버튼 클릭 이벤트
        chkBtn.setOnClickListener(new View.OnClickListener(){
            int doCount = currentHabit.getDoCount();
            int totalCount = Integer.valueOf(currentHabit.getCount());

            public void onClick(View v){
                // ** 체크버튼을 클릭한 날짜를 기록함
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                GregorianCalendar checkedMoment = new GregorianCalendar();
                String checkedDate = dateFormat.format(checkedMoment.getTime());
                currentHabit.setCheckedDate(checkedDate);

                //습관의 횟수보다 적을 때만 버튼을 클릭하면 doCount 증가, 습관 횟수를 모두 채웠으면 chkBtn 비활성화
                if(doCount < (totalCount - 1)){
                    currentHabit.setDoCount(1);
                    doCount = currentHabit.getDoCount(); // doCount 증가
                    nameTextView.setText(currentHabit.getHabitName() + "    " + doCount + "/" + totalCount);

                } else if(doCount == (totalCount - 1)){
                    currentHabit.setDoCount(1);
                    doCount = currentHabit.getDoCount();
                    nameTextView.setText(currentHabit.getHabitName() + "    " + doCount + "/" + totalCount);
                    currentHabit.setDidDays(1);
                    chkBtn.setEnabled(false);

                    int didDays = currentHabit.getDidDays();

                    if(currentHabit.isStartsTomorrow()){
                        int doPercent = (int) ((didDays / (float)passedDate) * 100);
                        progressTextView.setText(passedDate + "일 중 " + didDays + "일 달성    " + doPercent +"%");
                    } else {
                        int doPercent = (int) ((didDays / (float)(passedDate + 1)) * 100);
                        progressTextView.setText((passedDate + 1) + "일 중 " + didDays + "일 달성    " + doPercent +"%");
                    }
                }

                // 별도의 쓰레드로 실행 (클릭의 결과로 생성된 currentHabit 을 업데이트한다)
                DBUpdateThread dbRunnable = new DBUpdateThread();
                Thread t = new Thread(dbRunnable);
                t.start();

            }
        });

        return listItemView;
    }
}

/*
    해결해야하는 과제

    1. listViewItem 클릭시 삭제가 가능한 습관 세부 항목으로 들어가야됨

    2. MainActivithy 에서 항상 표시되는 "오늘도 화이팅!!" 문구를 날짜가 바뀔 때마다 새로운 동기부여 문구로 변경해야됨

    3. UI 수정, 애니메이션 추가


 */
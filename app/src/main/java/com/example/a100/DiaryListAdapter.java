package com.example.a100;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DiaryListAdapter extends RecyclerView.Adapter<DiaryListAdapter.ViewHolder> {
    private List<String> localDataSet;

    // ===== 뷰홀더 클래스
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView diaryDateTextView;
        private TextView diaryContentTextView;

        public ViewHolder(View itemView){
            super(itemView);

            diaryDateTextView = itemView.findViewById(R.id.diary_date);
            diaryContentTextView = itemView.findViewById(R.id.diary_content);
        }
    }

    public DiaryListAdapter (List<String> diaryList){
        localDataSet = diaryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_diary_item, parent, false);
        DiaryListAdapter.ViewHolder viewHolder = new DiaryListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = localDataSet.get(position);

        if(!text.isEmpty()){ // diary 의 내용이 비어있지 않을 때, diary 가 비어있지 않을 때만 값을 RecyclerView 에 할당한다
            // 습관일지 + 습관 내용으로 저장했던 값을 분리해서 각각에 맞는 ui에 넣어준다
            String diaryDate = text.substring(0, 8);
            String diaryContent = text.substring(8, text.length());

            holder.diaryDateTextView.setText(diaryDate);
            holder.diaryContentTextView.setText(diaryContent);
        }

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }


}

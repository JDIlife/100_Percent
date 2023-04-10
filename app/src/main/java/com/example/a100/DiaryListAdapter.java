package com.example.a100;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String text = localDataSet.get(position);

        if(!text.isEmpty()){ // diary 의 내용이 비어있지 않을 때, diary 가 비어있지 않을 때만 값을 RecyclerView 에 할당한다
            // 습관일지 + 습관 내용으로 저장했던 값을 분리해서 각각에 맞는 ui에 넣어준다
            String diaryDate = text.substring(0, 8);
            String diaryContent = text.substring(8, text.length());

            holder.diaryDateTextView.setText(diaryDate);
            holder.diaryContentTextView.setText(diaryContent);
        }

        // 습관 일지를 롱터치하면 팝업메뉴를 보여준다
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.diary_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            // 일지 수정을 눌렀을 때 처리
                            case R.id.edit_diary:
                                // 입력을 받을 수 있는 다이얼로그를 띄워준다

                                break;
                            // 일지 삭제를 눌렀을 때 처리
                            case R.id.delete_diary:
                                localDataSet.remove(position);
                                notifyItemRemoved(position);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

}

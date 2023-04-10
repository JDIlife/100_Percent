package com.example.a100;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

    private Context context;

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

    public DiaryListAdapter (List<String> diaryList, Context context){
        localDataSet = diaryList;
        this.context = context;
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
                            case R.id.edit_diary: { // 일지 수정을 눌렀을 때 처리
                                // 입력을 받을 수 있는 다이얼로그를 띄워준다
                                EditText et1 = new EditText(context);

                                DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 기존의 습관일지에서 날짜만 추린다
                                        String date = localDataSet.get(position).substring(0, 8);
                                        // 기존의 일지를 삭제한다
                                        localDataSet.remove(position);
                                        // 기존의 날짜에 새롭게 수정한 내용을 붙여서 해당 위치에 추가한다
                                        String editedDiary = date + String.valueOf(et1.getText());
                                        localDataSet.add(position, editedDiary);
                                        notifyItemChanged(position);
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("일지를 수정하세요")
                                        .setView(et1)
                                        .setPositiveButton("ok", okListener)
                                        .setNegativeButton("cancel", null)
                                        .show();
                                break;
                            }
                            case R.id.delete_diary: { // 일지 삭제를 눌렀을 때 처리
                                localDataSet.remove(position);
                                notifyItemRemoved(position);
                                break;
                            }
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

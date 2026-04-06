package com.example.petcare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Button;
import java.util.List;

public class TaskAdapter extends BaseAdapter {
    private Context context;
    private List<PetTask> taskList;
    private DatabaseHelper databaseHelper;

    public TaskAdapter(Context context, List<PetTask> taskList, DatabaseHelper databaseHelper) {
        this.context = context;
        this.taskList = taskList;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return taskList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_task, parent, false);
        }

        PetTask task = taskList.get(position);

        TextView tvName = convertView.findViewById(R.id.tv_task_name);
        TextView tvDetails = convertView.findViewById(R.id.tv_task_details);
        CheckBox cbStatus = convertView.findViewById(R.id.cb_task_status);
        Button btnDelete = convertView.findViewById(R.id.btn_delete_task);

        tvName.setText(task.getTaskName());
        String details = task.getTaskType() + " at " + task.getTaskTime();
        tvDetails.setText(details);
        cbStatus.setChecked(task.getIsCompleted() == 1);

        cbStatus.setOnClickListener(v -> {
            int newStatus = cbStatus.isChecked() ? 1 : 0;
            task.setIsCompleted(newStatus);
            databaseHelper.updateTaskStatus(task.getId(), newStatus);
        });

        btnDelete.setOnClickListener(v -> {
            databaseHelper.deleteTask(task.getId());
            taskList.remove(position);
            notifyDataSetChanged();
        });

        return convertView;
    }
}

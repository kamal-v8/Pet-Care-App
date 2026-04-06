package com.example.petcare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private TextView tvEmptyMessage;
    private DatabaseHelper databaseHelper;
    private List<PetTask> taskList;
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.lv_tasks);
        tvEmptyMessage = findViewById(R.id.tv_empty_message);
        databaseHelper = new DatabaseHelper(this);

        findViewById(R.id.btn_add_task).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
        });

        checkPermissions();
        loadTasks();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void loadTasks() {
        taskList = databaseHelper.getAllTasks();
        if (taskList.isEmpty()) {
            tvEmptyMessage.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            adapter = new TaskAdapter(this, taskList, databaseHelper);
            listView.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }
}

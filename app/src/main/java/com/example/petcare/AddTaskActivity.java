package com.example.petcare;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etName;
    private TextView tvType;
    private TextView tvTimeDisplay;
    private String selectedType = "";
    private String selectedTime = "";
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        etName = findViewById(R.id.et_task_name);
        tvType = findViewById(R.id.tv_task_type);
        tvTimeDisplay = findViewById(R.id.tv_pick_time_display);
        TextView tvTimeStatus = findViewById(R.id.tv_selected_time);
        calendar = Calendar.getInstance();

        String[] types = {"Feeding", "Vaccination", "Walking", "Grooming"};
        tvType.setOnClickListener(v -> new AlertDialog.Builder(this)
            .setTitle("Select Task Type")
            .setItems(types, (dialog, which) -> {
                selectedType = types[which];
                tvType.setText(selectedType);
                tvType.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            })
            .show());

        tvTimeDisplay.setOnClickListener(v -> new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // If selected time is in the past, move it to tomorrow
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            String timeStatus = "Time: " + selectedTime;
            tvTimeStatus.setText(timeStatus);
            tvTimeDisplay.setText("Change Time");
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show());

        findViewById(R.id.btn_save_task).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();

            if (name.isEmpty() || selectedType.isEmpty() || selectedTime.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            databaseHelper.insertTask(name, selectedType, selectedTime);
            scheduleNotification(name);
            finish();
        });
    }

    private void scheduleNotification(String taskName) {
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("task_name", taskName);

        // Use a unique request code for each alarm to prevent overwriting
        int requestCode = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            // Use setExactAndAllowWhileIdle to work even in background/battery-saving mode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // For Android 12+, we need special permission to schedule exact alarms
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    // Fallback to inexact if permission is missing
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }
}

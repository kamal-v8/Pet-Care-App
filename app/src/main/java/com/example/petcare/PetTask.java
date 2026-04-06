package com.example.petcare;

public class PetTask {
    private int id;
    private String taskName;
    private String taskType;
    private String taskTime;
    private int isCompleted; // 0 for pending, 1 for complete

    public PetTask(int id, String taskName, String taskType, String taskTime, int isCompleted) {
        this.id = id;
        this.taskName = taskName;
        this.taskType = taskType;
        this.taskTime = taskTime;
        this.isCompleted = isCompleted;
    }

    public int getId() { return id; }
    public String getTaskName() { return taskName; }
    public String getTaskType() { return taskType; }
    public String getTaskTime() { return taskTime; }
    public int getIsCompleted() { return isCompleted; }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }
}

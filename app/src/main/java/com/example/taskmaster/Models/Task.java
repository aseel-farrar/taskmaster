package com.example.taskmaster.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "task_name")
    private String taskTitle;

    @ColumnInfo(name = "task_body")
    private String taskBody;

    @ColumnInfo(name = "task_state")
    private String taskState;

    public Task(String taskTitle, String taskBody, String taskState) {
        this.taskTitle = taskTitle;
        this.taskBody = taskBody;
        this.taskState = taskState;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTaskBody() {
        return taskBody;
    }

    public void setTaskBody(String taskBody) {
        this.taskBody = taskBody;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

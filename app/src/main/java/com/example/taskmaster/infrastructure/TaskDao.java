package com.example.taskmaster.infrastructure;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.taskmaster.Models.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void addTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Query("SELECT * FROM task")
    List<Task> findAll();

    @Query("SELECT * FROM task WHERE task_name LIKE :name")
    Task findByName(String name);

    @Query("SELECT * FROM task WHERE task_name LIKE :id")
    Task findById(Long id);
}

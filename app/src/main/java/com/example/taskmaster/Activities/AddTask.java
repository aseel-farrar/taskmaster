package com.example.taskmaster.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.taskmaster.Models.Task;
import com.example.taskmaster.R;
import com.example.taskmaster.infrastructure.AppDatabase;
import com.example.taskmaster.infrastructure.TaskDao;

public class AddTask extends AppCompatActivity {
    private TaskDao taskDao;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Spinner statesList = findViewById(R.id.spinner);
        String[] states = new String[]{"new", "assigned", "in progress", "complete"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, states);
        statesList.setAdapter(adapter);

        // Room
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "task_List")
                .allowMainThreadQueries().build();
        taskDao = database.taskDao();

        findViewById(R.id.buttonAddTask).setOnClickListener(view -> {
            String taskTitle = ((EditText) findViewById(R.id.editTextTaskTitle)).getText().toString();
            String taskBody = ((EditText) findViewById(R.id.editTextTaskBody)).getText().toString();

            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            String taskState = spinner.getSelectedItem().toString();

            System.out.println(taskState);

            Task newTask = new Task(taskTitle, taskBody, taskState);
            taskDao.addTask(newTask);

            Toast toast = Toast.makeText(this, "submitted!", Toast.LENGTH_LONG);
            toast.show();
        });

        //Go Home!
        ImageView goHomeImage = findViewById(R.id.fromAddTaskActivityToHome);
        goHomeImage.setOnClickListener(v -> {
            // back button pressed
            Intent goHome = new Intent(AddTask.this,MainActivity.class);
            startActivity(goHome);
        });
    }
}
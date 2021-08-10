package com.example.taskmaster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Go to add task activity
        findViewById(R.id.buttonAddTaskActivity).setOnClickListener(view -> {
            Intent goToAddTaskActivity = new Intent(MainActivity.this, AddTask.class);
            startActivity(goToAddTaskActivity);
        });

        // Go to all tasks activity
        findViewById(R.id.buttonAllTasks).setOnClickListener(view -> {
            Intent goToAllTaskActivity = new Intent(MainActivity.this, AllTasks.class);
            startActivity(goToAllTaskActivity);
        });
    }
}
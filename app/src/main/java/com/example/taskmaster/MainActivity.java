package com.example.taskmaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


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

        // Go to settings activity
        findViewById(R.id.imageViewSettings).setOnClickListener(view -> {
            Intent goToSittingsActivity = new Intent(MainActivity.this, Settings.class);
            startActivity(goToSittingsActivity);
        });

        // Handling the tasks buttons
        findViewById(R.id.buttonCode).setOnClickListener(view -> {
            showDetails(((Button)findViewById(R.id.buttonCode)).getText().toString());
        });

        findViewById(R.id.buttonClean).setOnClickListener(view -> {
            showDetails(((Button)findViewById(R.id.buttonClean)).getText().toString());
        });

        findViewById(R.id.buttonSport).setOnClickListener(view -> {
            showDetails(((Button)findViewById(R.id.buttonSport)).getText().toString());
        });
    }

    /**
     * function to call the details Activity for specific task Name
     *
     * @param taskName: taskName
     */
    public void showDetails(String taskName){
        Intent intent = new Intent(MainActivity.this, Details.class);
        intent.putExtra("taskName", taskName);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //create sharedPreference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = sharedPreferences.getString("username", "");
        if (!username.equals("")) {
            ((TextView)findViewById(R.id.textViewTasks)).setText(username+"'s Tasks");
        }
    }
}
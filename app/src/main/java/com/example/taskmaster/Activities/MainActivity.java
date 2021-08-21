package com.example.taskmaster.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;

import android.widget.TextView;

import com.example.taskmaster.Adaptesrs.TaskAdapter;
import com.example.taskmaster.Models.Task;
import com.example.taskmaster.R;
import com.example.taskmaster.infrastructure.AppDatabase;
import com.example.taskmaster.infrastructure.TaskDao;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private List<Task> tasks;
    private TaskAdapter adapter;

    private RecyclerView taskRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    private TaskDao taskDao;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Room
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "task_List")
                .allowMainThreadQueries().build();
        taskDao = database.taskDao();

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
    }

    private void listItemDeleted() {
        adapter.notifyDataSetChanged();
    }


    /**
     * function to call the details Activity for specific task Name
     *
     * @param taskName: taskName
     */
    public void showDetails(String taskName) {
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
            ((TextView) findViewById(R.id.textViewTasks)).setText(username + "'s Tasks");
        }

        // RecycleView
        tasks = taskDao.findAll();
        if (tasks == null) {
            tasks = new ArrayList<>();
        }

       taskRecyclerView = findViewById(R.id.list);
        adapter = new TaskAdapter(tasks, new TaskAdapter.OnTaskItemClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent goToDetailsIntent = new Intent(getApplicationContext(), Details.class);
                goToDetailsIntent.putExtra("taskTitle", tasks.get(position).getTaskTitle());
                goToDetailsIntent.putExtra("taskBody", tasks.get(position).getTaskBody());
                goToDetailsIntent.putExtra("taskState", tasks.get(position).getTaskState());
                startActivity(goToDetailsIntent);
            }

            @Override
            public void onDeleteItem(int position) {
                //delete from database
                taskDao.deleteTask(tasks.get(position));

                //delete from tasks list
                tasks.remove(position);
                listItemDeleted();
            }
        });
        linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);
        taskRecyclerView.setLayoutManager(linearLayoutManager);
        taskRecyclerView.setAdapter(adapter);
    }
}
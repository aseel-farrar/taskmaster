package com.example.taskmaster.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.taskmaster.Adaptesrs.TaskAdapter;
import com.example.taskmaster.Models.Task;
import com.example.taskmaster.R;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private List<Task> tasks;
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecycleView code
        RecyclerView foodRecyclerView = findViewById(R.id.list);
        tasks = new ArrayList<>();

        tasks.add(new Task(
                "Code",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                "new"));

        tasks.add(new Task(
                "Clean",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                "assigned"));

        tasks.add(new Task(
                "Shopping",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                "new"));

        tasks.add(new Task(
                "Sport",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                "in progress"));

        tasks.add(new Task(
                "Read",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                "complete"));


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
                tasks.remove(position);
                listItemDeleted();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);

        foodRecyclerView.setLayoutManager(linearLayoutManager);
        foodRecyclerView.setAdapter(adapter);

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
    }
}
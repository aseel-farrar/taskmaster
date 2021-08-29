package com.example.taskmaster.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.Adaptesrs.TaskAdapter;
import com.example.taskmaster.Models.Task;
import com.example.taskmaster.R;

import java.util.ArrayList;
import java.util.List;

public class TeamTasks extends AppCompatActivity {

    private static final String TAG = "TeamTasks";
    private List<Task> tasks;
    private TaskAdapter adapter;

    private RecyclerView taskRecyclerView;
    private LinearLayoutManager linearLayoutManager;


    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_tasks);
        setTitle("Team Tasks");


        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                notifyDataSetChanged();
                return false;
            }
        });

        //Go Home!
        ImageView goHomeImage = findViewById(R.id.fromSettingsActivityToHome);
        goHomeImage.setOnClickListener(v -> {
            // back button pressed
            Intent goHome = new Intent(TeamTasks.this, MainActivity.class);
            startActivity(goHome);
        });

        // Sign Out
        findViewById(R.id.imageViewLogOut).setOnClickListener(view -> logout());


    }

    private void listItemDeleted() {
        adapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();

        //create sharedPreference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String teamName = sharedPreferences.getString("teamName", "");

        tasks = new ArrayList<>();

        if (!teamName.equals("")) {
            ((TextView) findViewById(R.id.textViewMyTeam)).setText(teamName + " Tasks");
            getTeamTasksFromAPI(teamName);

        }
//        getTeamTasksFromAPI("Team A");

//        for (Task task : tasks) {
//            Log.i(TAG, "team tasks: " + task.getTaskTitle());
//        }


        // RecycleView

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
                // todo: chech the delete
                Team team = Team.builder().teamName("Team A").build();

                // todo: delete from Amplify API
                com.amplifyframework.datastore.generated.model.Task task = com.amplifyframework.datastore.generated.model.Task.builder()
                        .taskTitle(tasks.get(position).getTaskTitle())
                        .taskBody(tasks.get(position).getTaskBody())
                        .taskState(tasks.get(position).getTaskState())
                        .team(team)
                        .build();

                Amplify.API.mutate(ModelMutation.delete(task),
                        response -> Log.i(TAG, "item deleted from API:" + task.getTaskTitle()),
                        error -> Log.e(TAG, "Delete failed", error)
                );

                //delete from tasks list
                tasks.remove(position);
                listItemDeleted();


                // delete from any local storage/cache if any

            }
        });

        linearLayoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);
        taskRecyclerView.setLayoutManager(linearLayoutManager);
        taskRecyclerView.setAdapter(adapter);


    }


    private void getTeamTasksFromAPI(String teamName) {
        Amplify.API.query(ModelQuery.list(com.amplifyframework.datastore.generated.model.Task.class),
                response -> {
                    for (com.amplifyframework.datastore.generated.model.Task task : response.getData()) {

                        if ((task.getTeam().getTeamName()).equals(teamName)) {
                            tasks.add(new Task(task.getTaskTitle(), task.getTaskBody(), task.getTaskState()));
                            Log.i(TAG, "onCreate: the Tasks DynamoDB are => " + task.getTaskTitle());
                            Log.i(TAG, "onCreate: the team DynamoDB are => " + task.getTeam().getTeamName());
                        }
                    }
                    handler.sendEmptyMessage(1);
                },
                error -> Log.e(TAG, "onCreate: Failed to get Tasks from DynamoDB => " + error.toString())
        );
    }

    @SuppressLint("NotifyDataSetChanged")
    private void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    private void logout() {

        Amplify.Auth.signOut(
                () -> Log.i("AuthQuickstart", "Signed out successfully"),
                error -> Log.e("AuthQuickstart", error.toString())
        );

        // Clear the IdentityManager credentials
        final IdentityManager idm = new IdentityManager(this, new AWSConfiguration(this));
        IdentityManager.setDefaultIdentityManager(idm);
        idm.getUnderlyingProvider().clearCredentials();
        idm.getUnderlyingProvider().clear();
        idm.getUnderlyingProvider().setLogins(null);

        Intent goToSignIn = new Intent(TeamTasks.this, SignInActivity.class);
        startActivity(goToSignIn);
    }
}
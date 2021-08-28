package com.example.taskmaster.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.Models.Task;
import com.example.taskmaster.R;
import com.example.taskmaster.infrastructure.AppDatabase;
import com.example.taskmaster.infrastructure.TaskDao;

import java.util.ArrayList;
import java.util.List;

public class AddTask extends AppCompatActivity {

    private static final String TAG = "AddTaskActivity";

    private TaskDao taskDao;

    private String teamId = "";

    private final List<Team> teams = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        setTitle("Add Task");
        getAllTeamsDataFromAPI();

        // states spinner
        Spinner statesList = findViewById(R.id.spinner);
        String[] states = new String[]{"New", "Assigned", "In progress", "Complete"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, states);
        statesList.setAdapter(adapter);


        // teams spinner
        Spinner teamsList = findViewById(R.id.spinnerTeam);
        String[] teams = new String[]{"Team A", "Team B", "Team C"};
        ArrayAdapter<String> TeamsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, teams);
        teamsList.setAdapter(TeamsAdapter);


        // Room
        AppDatabase database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "task_List")
                .allowMainThreadQueries().build();
        taskDao = database.taskDao();

        // add task button handler
        findViewById(R.id.buttonAddTask).setOnClickListener(view -> {
            String taskTitle = ((EditText) findViewById(R.id.editTextTaskTitle)).getText().toString();
            String taskBody = ((EditText) findViewById(R.id.editTextTaskBody)).getText().toString();

            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            String taskState = spinner.getSelectedItem().toString();

            Spinner teamSpinner = (Spinner) findViewById(R.id.spinnerTeam);
            String teamName = teamSpinner.getSelectedItem().toString();


            // save to room
            Task newTask = new Task(taskTitle, taskBody, taskState);
            taskDao.addTask(newTask);


            // todo: clean the following

            // get the Team from Database and it will update the teamId with the new id

            Log.i(TAG, "on button Listener the team id is >>>>> " + getTeamId(teamName));

            // save task to dynamoDB
            addTaskToDynamoDB(taskTitle,
                    taskBody,
                    taskState,
                    new Team(getTeamId(teamName), teamName));

        });

        //Go Home!
        ImageView goHomeImage = findViewById(R.id.fromAddTaskActivityToHome);
        goHomeImage.setOnClickListener(v -> {
            // back button pressed
            Intent goHome = new Intent(AddTask.this, MainActivity.class);
            startActivity(goHome);
        });
    }

    public void addTaskToDynamoDB(String taskTitle, String taskBody, String taskState, Team team) {
        com.amplifyframework.datastore.generated.model.Task task = com.amplifyframework.datastore.generated.model.Task.builder()
                .taskTitle(taskTitle)
                .taskBody(taskBody)
                .taskState(taskState)
                .team(team)
                .build();

        Amplify.API.mutate(ModelMutation.create(task),
                success -> Log.i(TAG, "Saved item: " + task.getTaskTitle()),
                error -> Log.e(TAG, "Could not save item to API", error));

        Toast toast = Toast.makeText(this, "submitted!", Toast.LENGTH_LONG);
        toast.show();
    }

    private void getAllTeamsDataFromAPI() {
        Amplify.API.query(ModelQuery.list(Team.class),
                response -> {
                    for (Team team : response.getData()) {
                        teams.add(team);
                        Log.i(TAG, "the team id DynamoDB are => " + team.getTeamName() + "  " + team.getId());
                    }
                },
                error -> Log.e(TAG, "onCreate: Failed to get team from DynamoDB => " + error.toString())
        );
    }

    public void getTeamFromApi(String teamName) {
        Amplify.API.query(ModelQuery.list(Team.class, Team.TEAM_NAME.contains(teamName)),
                response -> {
                    List<Team> teams = (List<Team>) response.getData().getItems();
                    Log.i(TAG, "get exist team id => " + teams.get(0).getId());
                    teamId = teams.get(0).getId();
                },
                error -> {
                    Log.e(TAG, "onCreate: Failed to get Teams from DynamoDB => " + error.toString());
                }
        );

        Log.i(TAG, "the Team from the function => " + teamName + " - " + teamId);

    }

    public void saveTeamToApi(String teamName) {
        Team team = Team.builder().teamName(teamName).build();

        Amplify.API.query(ModelQuery.list(Team.class, Team.TEAM_NAME.contains(teamName)),
                response -> {
                    List<Team> teams = (List<Team>) response.getData().getItems();

                    if (teams.isEmpty()) {
                        Amplify.API.mutate(ModelMutation.create(team),
                                success -> Log.i(TAG, "Saved item: " + team.getTeamName()),
                                error -> Log.e(TAG, "Could not save item to API", error));
                    }
                },
                error -> Log.e(TAG, "onCreate: Failed to get Teams from DynamoDB => " + error.toString())
        );

    }

    public String getTeamId(String teamName) {
        for (Team team : teams) {
            if (team.getTeamName().equals(teamName)) {
                return team.getId();
            }
        }
        return "";
    }
}
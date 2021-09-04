package com.example.taskmaster.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.example.taskmaster.Models.Task;
import com.example.taskmaster.R;
import com.example.taskmaster.infrastructure.AppDatabase;
import com.example.taskmaster.infrastructure.TaskDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddTask extends AppCompatActivity {

    private static final String TAG = "AddTaskActivity";
    public static final int REQUEST_FOR_FILE = 999;

    private TaskDao taskDao;
    private String teamId = "";
    private final List<Team> teams = new ArrayList<>();
    private String taskName;
    private File uploadFile;

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
            taskName = ((EditText) findViewById(R.id.editTextTaskTitle)).getText().toString();
            String taskBody = ((EditText) findViewById(R.id.editTextTaskBody)).getText().toString();

            Spinner spinner = (Spinner) findViewById(R.id.spinner);
            String taskState = spinner.getSelectedItem().toString();

            Spinner teamSpinner = (Spinner) findViewById(R.id.spinnerTeam);
            String teamName = teamSpinner.getSelectedItem().toString();


            // save to room
            Task newTask = new Task(taskName, taskBody, taskState);
            taskDao.addTask(newTask);


            // todo: clean the following

            // get the Team from Database and it will update the teamId with the new id

            Log.i(TAG, "on button Listener the team id is >>>>> " + getTeamId(teamName));

            // save task to dynamoDB
            addTaskToDynamoDB(taskName,
                    taskBody,
                    taskState,
                    new Team(getTeamId(teamName), teamName));

            // upload to S3
            uploadFileToS3(uploadFile);

        });

        //Go Home!
        ImageView goHomeImage = findViewById(R.id.fromAddTaskActivityToHome);
        goHomeImage.setOnClickListener(v -> {
            // back button pressed
            Intent goHome = new Intent(AddTask.this, MainActivity.class);
            startActivity(goHome);
        });

        // Sign Out
        findViewById(R.id.imageViewLogOut).setOnClickListener(view -> logout());

        //Upload File
        findViewById(R.id.buttonUploadTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFileFromDevice();
            }
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

        Intent goToSignIn = new Intent(AddTask.this, SignInActivity.class);
        startActivity(goToSignIn);
    }

    //>>>>>> get the file from the device then upload it to S3
    private void getFileFromDevice() {
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        chooseFile = Intent.createChooser(chooseFile, "Choose a File");
        startActivityForResult(chooseFile, REQUEST_FOR_FILE);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FOR_FILE && resultCode == RESULT_OK) {
            Log.i(TAG, "onActivityResult: returned from file explorer");
            Log.i(TAG, "onActivityResult: => " + data.getData());

            uploadFile = new File(getApplicationContext().getFilesDir(), "uploadFile");

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileUtils.copy(inputStream, new FileOutputStream(uploadFile));
            } catch (Exception exception) {
                Log.e(TAG, "onActivityResult: file upload failed" + exception.toString());
            }

//            uploadFileToS3(uploadFile);
        }
    }


    private void uploadFileToS3(File uploadFile) {
        String key;
        if (taskName != null)
            key = taskName + ".jpg";
        else
            key = String.format("defaultTask%s.jpg", new Date().getTime());

        Amplify.Storage.uploadFile(
                key,
                uploadFile,
                success -> Log.i(TAG, "uploadFileToS3: succeeded " + success.getKey()),
                error -> Log.e(TAG, "uploadFileToS3: failed " + error.toString())
        );
    }

}
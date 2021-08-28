package com.example.taskmaster.Activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taskmaster.R;

public class Details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle("Details");

        // handling the Data passed via the intent
        Intent intent = getIntent();

        String taskName = intent.getExtras().getString("taskTitle");
        String taskBody = intent.getExtras().getString("taskBody");
        String taskState = intent.getExtras().getString("taskState");

        ((TextView)findViewById(R.id.textViewDetailsTaskTitle)).setText(taskName);
        ((TextView)findViewById(R.id.taskBody)).setText(taskBody);
        ((TextView)findViewById(R.id.taskState)).setText(taskState);

        //Go Home!
        ImageView goHomeImage = findViewById(R.id.fromTaskDetailsActivityToHome);
        goHomeImage.setOnClickListener(v -> {
            // back button pressed
            Intent goHome = new Intent(Details.this,MainActivity.class);
            startActivity(goHome);
        });

    }
}
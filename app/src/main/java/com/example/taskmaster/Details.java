package com.example.taskmaster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Details extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // handling the Data passed via the intent
        Intent intent = getIntent();
        String taskName = intent.getExtras().getString("taskName");
        ((TextView)findViewById(R.id.textViewDetailsTaskTitle)).setText(taskName);
    }
}
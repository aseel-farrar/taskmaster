package com.example.taskmaster.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.taskmaster.R;

public class AddTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        findViewById(R.id.buttonAddTask).setOnClickListener(view -> {
            Toast toast = Toast.makeText(this, "submitted!", Toast.LENGTH_LONG);
            toast.show();
        });
    }
}
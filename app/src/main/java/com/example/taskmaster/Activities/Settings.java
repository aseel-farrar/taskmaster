package com.example.taskmaster.Activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taskmaster.R;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //create sharedPreference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

        // save the user name in sharedPreference
        findViewById(R.id.imageViewSave).setOnClickListener(view -> {
            String username = ((EditText) findViewById(R.id.editTextUsername)).getText().toString();

            preferenceEditor.putString("username", username);
            preferenceEditor.apply();

            Toast toast = Toast.makeText(this, "saved!", Toast.LENGTH_LONG);
            toast.show();
        });
    }
}
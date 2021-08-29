package com.example.taskmaster.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amplifyframework.core.Amplify;
import com.example.taskmaster.R;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setTitle("Settings");


        // teams spinner
        Spinner teamsList = findViewById(R.id.spinnerTeam);
        String[] teams = new String[]{"","Team A", "Team B", "Team C"};
        ArrayAdapter<String> TeamsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, teams);
        teamsList.setAdapter(TeamsAdapter);

        //create sharedPreference
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();


        // save the user name in sharedPreference
        findViewById(R.id.imageViewSave).setOnClickListener(view -> {
            String username = ((EditText) findViewById(R.id.editTextUsername)).getText().toString();


            Spinner teamSpinner = (Spinner) findViewById(R.id.spinnerTeam);
            String teamName = teamSpinner.getSelectedItem().toString();

//            preferenceEditor.putString("username", username);
            preferenceEditor.putString("teamName", teamName);
            preferenceEditor.apply();

            Toast toast = Toast.makeText(this, "saved!", Toast.LENGTH_LONG);
            toast.show();
        });

        //Go Home!
        ImageView goHomeImage = findViewById(R.id.fromSettingsActivityToHome);
        goHomeImage.setOnClickListener(v -> {
            // back button pressed
            Intent goHome = new Intent(Settings.this,MainActivity.class);
            startActivity(goHome);
        });

        // Sign Out
        findViewById(R.id.imageViewLogOut).setOnClickListener(view -> logout());

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

        Intent goToSignIn = new Intent(Settings.this, SignInActivity.class);
        startActivity(goToSignIn);
    }
}
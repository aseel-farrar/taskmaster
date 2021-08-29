package com.example.taskmaster.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amplifyframework.core.Amplify;
import com.example.taskmaster.R;

import java.util.Objects;

public class AllTasks extends AppCompatActivity {

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks);

        setTitle("All Tasks");


        Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);


        //Go Home!
        ImageView goHomeImage = findViewById(R.id.fromAllTaskActivityToHome);
        goHomeImage.setOnClickListener(v -> {
            // back button pressed
            Intent goHome = new Intent(AllTasks.this,MainActivity.class);
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

        Intent goToSignIn = new Intent(AllTasks.this, SignInActivity.class);
        startActivity(goToSignIn);
    }
}
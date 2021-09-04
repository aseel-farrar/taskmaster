package com.example.taskmaster.Activities;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amplifyframework.core.Amplify;
import com.example.taskmaster.R;

import java.io.File;

public class Details extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";

    private String taskName;
    private File downloadedImage;
    private ImageView taskImage;
    private Handler handleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle("Details");

        handleImageView = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                setTaskImage();
                return false;
            }
        });
        taskImage = findViewById(R.id.downloadedImage);

        // handling the Data passed via the intent
        Intent intent = getIntent();

        taskName = intent.getExtras().getString("taskTitle");
        String taskBody = intent.getExtras().getString("taskBody");
        String taskState = intent.getExtras().getString("taskState");

        ((TextView) findViewById(R.id.textViewDetailsTaskTitle)).setText(taskName);
        ((TextView) findViewById(R.id.taskBody)).setText(taskBody);
        ((TextView) findViewById(R.id.taskState)).setText(taskState);


        Log.i(TAG, "onCreate:  DIRECTORY -->   " + getApplicationContext().getFilesDir());
        getFileFromApi();


        //Go Home!
        ImageView goHomeImage = findViewById(R.id.fromTaskDetailsActivityToHome);
        goHomeImage.setOnClickListener(v -> {
            // back button pressed
            Intent goHome = new Intent(Details.this, MainActivity.class);
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

        Intent goToSignIn = new Intent(Details.this, SignInActivity.class);
        startActivity(goToSignIn);
    }

    private void setTaskImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8; // down sizing image as it throws OutOfMemory  Exception for larger images
        Bitmap bitmap = BitmapFactory.decodeFile(downloadedImage.getAbsolutePath(), options);
        taskImage.setImageBitmap(bitmap);
    }


    private void getFileFromApi() {
        Amplify.Storage.downloadFile(
                taskName + ".jpg",
                new File(getApplicationContext().getFilesDir() + "test.jpg"),
                success -> {
                    Log.i(TAG, "getFileFromApi: successfully   ----> " + success.toString());
                    downloadedImage = success.getFile();
                    handleImageView.sendEmptyMessage(1);
                },
                failure -> Log.i(TAG, "getFileFromApi:  failed  ---> " + failure.toString())
        );
    }
}
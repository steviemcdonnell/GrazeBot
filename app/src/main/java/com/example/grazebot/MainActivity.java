package com.example.grazebot;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MapActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isServicesOK()) {
            initMap();
        }
    }
    public void onClickSwitchControl(View view) {
        Intent intent = new Intent(this, ControlActivity.class);
        startActivity(intent);
    }
    public void onClickSwitchTimer(View view) {
        Intent intent = new Intent(this, TimerActivity.class);
        startActivity(intent);
    }/*
    public void onClickSwitchMaps(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }*/

    private void initMap() {
        Button buttonMap = (Button) findViewById(R.id.buttonMap);
        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services verison");

        int available = GoogleApiAvailability
                .getInstance()
                .isGooglePlayServicesAvailable(MainActivity.this);
        if(available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServiceOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            // An error occurred but we can resolve it
            Log.d(TAG, "isServiceOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}

package com.example.grazebot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MapActivity";
    private String IP_ADDRESS = "";
    private String PORT = "";

    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(isServicesOK()) {
            initMap();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if( requestCode == 1) { // Settings Intent Return
            if(resultCode == Activity.RESULT_OK){
                try {
                    assert data != null;
                    Bundle bundle = data.getBundleExtra("data");
                    IP_ADDRESS = bundle.getString("ip_address");
                    TextView ip_port_label = (TextView) findViewById(R.id.ip_address);
                    ip_port_label.setText(IP_ADDRESS);
                } catch(NullPointerException e){
                    Log.e(TAG, "onActivityResult: no bundle in intent return " + e.getMessage());
                }
            }
            else{
                Log.d(TAG, "onActivityResult: Nothing returned");
            }
        }
    }

    public void onClickSwitchSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, 1);
        Log.d(TAG, "onClickSwitchSettings: ");
    }

    public void onClickSwitchControl(View view) {
        Intent intent = new Intent(this, ControlActivity.class);
        Log.d(TAG, "onClickSwitchControl: ");
        startActivity(intent);
    }/*
    public void onClickSwitchTimer(View view) {
        Intent intent = new Intent(this, TimerActivity.class);
        startActivity(intent);
    }
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

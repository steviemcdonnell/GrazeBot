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
    private Connection_Status connection_status = Connection_Status.NOT_CONNECTED;

    private final int SETTINGS_CODE = 0;
    private final int CONTROL_CODE = 1;

    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: " + connection_status.getConnectCode());
        if(isServicesOK()) {
            initMap();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if( requestCode == SETTINGS_CODE) { // Settings Intent Return
            if(resultCode == Activity.RESULT_OK){
                try {
                    assert data != null;
                    Bundle bundle = data.getBundleExtra("data");
                    IP_ADDRESS = bundle.getString("ip_address");
                    connection_status = bundle.getInt("status")==0?Connection_Status.CONNECTED:Connection_Status.NOT_CONNECTED;
                    Log.d(TAG, "onActivityResult: " + connection_status.getConnectCode() + connection_status);
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
        Log.d(TAG, "onClickSwitchSettings: ");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, SETTINGS_CODE);
    }

    public void onClickSwitchControl(View view) {
        Log.d(TAG, "onClickSwitchControl: " + connection_status);
        if( connection_status == Connection_Status.CONNECTED) {
            Intent intent = new Intent(this, ControlActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("ip_address", IP_ADDRESS);
            intent.putExtra("data", bundle);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Not Connected to Server", Toast.LENGTH_LONG).show();
            onClickSwitchSettings(view);
        }
    }

    public void onClickSwitchGraphs(View view) {
        Log.d(TAG, "onClickSwitchGraphs: " + connection_status);
        if( connection_status == Connection_Status.CONNECTED) {
            Intent intent = new Intent(this, GraphActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("ip_address", IP_ADDRESS);
            intent.putExtra("data", bundle);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Not Connected to Server", Toast.LENGTH_LONG).show();
            onClickSwitchSettings(view);
        }
    }

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

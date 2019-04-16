package com.example.grazebot;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;



public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onClickSwitchControl(View view) {
        Intent intent = new Intent(this, ControlActivity.class);
        startActivity(intent);
    }
    public void onClickSwitchTimer(View view) {
        Intent intent = new Intent(this, TimerActivity.class);
        startActivity(intent);
    }
    public void onClickSwitchMaps(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

}

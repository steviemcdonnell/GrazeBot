package com.example.grazebot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ControlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
    }

    public void onClickClose(View view) {
        finish();
    }
}
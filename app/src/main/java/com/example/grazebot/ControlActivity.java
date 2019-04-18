package com.example.grazebot;

import android.annotation.SuppressLint;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class ControlActivity extends AppCompatActivity {

    private static final String TAG = "ControlActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        Button leftButton = createButtonListener(findViewById(R.id.buttonLeft), "left");
        Button rightButton = createButtonListener(findViewById(R.id.buttonRight), "right");
        Button forwardButton = createButtonListener(findViewById(R.id.buttonForward), "forward");
        Button reverseButton = createButtonListener(findViewById(R.id.buttonReverse), "reverse");

    }

    public void onClickClose(View view) {
        finish();
    }

    @SuppressLint("ClickableViewAccessibility")
    private Button createButtonListener(View id, final String command) {
        Button btn = findViewById(id.getId());
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d(TAG, "onTouch: DOWN");
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "onTouch: UP");
                }
                return false;
            }
        });
        return btn;
    }
}

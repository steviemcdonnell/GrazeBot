package com.example.grazebot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

public class ControlActivity extends AppCompatActivity implements HttpHandler.OnResponseReceived {

    private static final String TAG = "ControlActivity";
    private static String IP_ADDRESS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        Bundle parentPassedBundle = getIntent().getBundleExtra("data");
        if( parentPassedBundle != null) {
            IP_ADDRESS = parentPassedBundle.getString("ip_address");
        }
        Log.d(TAG, "onCreate: " + IP_ADDRESS);

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
                    sendMoveCommand(command);
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "onTouch: UP");
                    sendMoveCommand("still");
                }
                return false;
            }
        });
        return btn;
    }

    private void sendMoveCommand(final String command){
        HttpHandler httpHandler = new HttpHandler(this);
        JsonDataTemplate dataTemplate = new JsonDataTemplate(new HashMap<String, String>(){{
            put("command", "move");
            put("movement", command);
        }});
        httpHandler.makeRequest(IP_ADDRESS, dataTemplate.getJsonData());
    }

    @Override
    public void onResponseReceived(String response) {

    }
}

package com.example.grazebot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity implements HttpHandler.OnResponseReceived {

    private String IP_ADDRESS = "";
    TextView textView = null;
    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textView = (TextView) findViewById(R.id.status);
    }

    public void onClickTestConnection(View view) {
        HttpHandler httpHandler = new HttpHandler(this);
        getNetworkData();
        JsonDataTemplate dataTemplate = new JsonDataTemplate(new HashMap<String, String>(){{
            put("command", "test");
            put("movement", "Backwards");
        }});
        httpHandler.makeRequest(IP_ADDRESS, dataTemplate.getJsonData());
    }

    @SuppressLint("SetTextI18n")
    public void responseCallback(String response){

    }

    private void getNetworkData(){
        EditText ip_address = (EditText) findViewById(R.id.ip_address_input);
        EditText port = (EditText) findViewById(R.id.port_input);
        IP_ADDRESS = "http://" +
                ip_address.getText().toString() +
                ":" +
                port.getText().toString();
    }

    public void onClickReturn(View view){
        Intent returnIntent = new Intent();
        Bundle bundle = new Bundle();
        getNetworkData();
        bundle.putString("ip_address", IP_ADDRESS);
        returnIntent.putExtra("data", bundle);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onResponseReceived(String response) {
        Log.e(TAG, "onClickTestConnection: response" + response );
        if( response != null ){
            if (response.contains("test_OK")) {
                textView.setText("Connected");
                textView.setBackgroundColor(getResources().getColor(R.color.connected));
            }
            else {
                textView.setText("Not Connected");
                textView.setBackgroundColor(getResources().getColor(R.color.fail_to_connect));
            }
        }
        else {
            textView.setText("Not Connected");
            textView.setBackgroundColor(getResources().getColor(R.color.fail_to_connect));
        }
    }
}

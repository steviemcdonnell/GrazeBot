package com.example.grazebot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity implements HttpHandler.OnResponseReceived {

    private String IP_ADDRESS = "";
    private Connection_Status connection_status = Connection_Status.NOT_CONNECTED;
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
        }});
        httpHandler.makeRequest(IP_ADDRESS, dataTemplate.getJsonData());
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
        bundle.putInt("conn_status", connection_status.getConnectCode());
        returnIntent.putExtra("data", bundle);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResponseReceived(JsonParser response) {
        Log.e(TAG, "onClickTestConnection: response" + response );
        try {
            if (response != null) {
                if (response.getMap().get("response").equals("test_OK")) {
                    textView.setText("Connected");
                    textView.setBackgroundColor(getResources().getColor(R.color.connected));
                    connection_status = Connection_Status.CONNECTED;
                } else {
                    textView.setText("Corrupt Data");
                    textView.setBackgroundColor(getResources().getColor(R.color.fail_to_connect));
                    connection_status = Connection_Status.NOT_CONNECTED;
                }
            } else {
                textView.setText("No Response");
                textView.setBackgroundColor(getResources().getColor(R.color.fail_to_connect));
                connection_status = Connection_Status.NOT_CONNECTED;
            }
        } catch( Exception e) {
            Log.e(TAG, "onResponseReceived: " + e.getMessage() );
        }
    }
}

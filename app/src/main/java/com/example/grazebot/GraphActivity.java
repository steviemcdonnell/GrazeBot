package com.example.grazebot;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashMap;

public class GraphActivity extends AppCompatActivity implements HttpHandler.OnResponseReceived {

    private static final String TAG = "GraphActivity";
    GraphView graph_temperature;
    GraphView graph_pressure;
    GraphView graph_humidity;
    private String IP_ADDRESS = null;
    private final int interval = 1000; // 1 Second
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable(){
        public void run() {
            getGraphData();
            handler.postDelayed(runnable, interval);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Bundle parentPassedBundle = getIntent().getBundleExtra("data");
        if( parentPassedBundle != null) {
            IP_ADDRESS = parentPassedBundle.getString("ip_address");
        }
        Log.d(TAG, "onCreate: " + IP_ADDRESS);
        graph_temperature = (GraphView) findViewById(R.id.graph_temp);
        graph_pressure = (GraphView) findViewById(R.id.graph_pressure);
        graph_humidity = (GraphView) findViewById(R.id.graph_humidity);
        onClickUpdateGraph(null);
        handler.postDelayed(runnable, interval);

    }

    public void onClickUpdateGraph(View view) {
       getGraphData();
    }

    private void getGraphData() {
        HttpHandler httpHandler = new HttpHandler(this);
        JsonDataTemplate dataTemplate = new JsonDataTemplate(new HashMap<String, String>(){{
            put("command", "test");
        }});
        httpHandler.makeRequest(IP_ADDRESS, dataTemplate.getJsonData());
    }

    public void onClickReturn(View view) {
        finish();
    }

    @Override
    public void onResponseReceived(JsonParser response) {
        updateGraphs(graph_temperature, response.getTemperatureList());
        updateGraphs(graph_pressure, response.getPressureList());
        updateGraphs(graph_humidity, response.getHumidityList());
    }

    private void updateGraphs(GraphView graph, ArrayList<Double> arrayList){
        graph.removeAllSeries();
        DataPoint[] points = convertArrayListToArray(arrayList);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(points);
        graph.addSeries(series);
    }

    private DataPoint[] convertArrayListToArray(ArrayList<Double> arrayList){
        int size = arrayList.size();
        DataPoint[] values = new DataPoint[size];
        for( int i = 0; i < size; i++){
            double value = arrayList.get(i);
            DataPoint v = new DataPoint(i, value);
            values[i] = v;
        }
        return values;
    }
}

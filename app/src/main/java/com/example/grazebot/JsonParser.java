package com.example.grazebot;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class JsonParser {

    private static final String TAG = "JsonParser";

    private HashMap<String, String> map;
    private ArrayList<Double> temperatureList;
    private ArrayList<Double> pressureList;
    private ArrayList<Double> humidityList;
    private JSONObject jsonObject;

    JsonParser(String jsonString) {
        map = new HashMap<>();
        temperatureList = new ArrayList<>();
        pressureList = new ArrayList<>();
        humidityList = new ArrayList<>();
        try {
            this.jsonObject = new JSONObject(jsonString);
        } catch( JSONException e){
            Log.e(TAG, "JsonParser: " + e.getMessage());
        }
        buildMap();
        buildTemperatureList();
        buildPressureList();
        buildHumidityList();
    }

    private void buildMap(){
        try {
            Iterator<?> keys = jsonObject.keys();
            while( keys.hasNext() ){
                String key = (String) keys.next();
                String value = jsonObject.getString(key);
                map.put(key, value);
            }
        } catch( JSONException e) {
            Log.e(TAG, "buildMap: " + e.getMessage() );
        }
        System.out.println(map.values());
    }



    private void buildTemperatureList(){
        try {
            for(int i = 0; i < 99; i++)
            {
                temperatureList.add(jsonObject.getJSONArray("sensor_array").getJSONArray(i).getDouble(1));
            }
            Log.d(TAG, "buildTemperatureList: " + temperatureList);
        } catch(Exception e){
            Log.e(TAG, "buildTemperatureList: " + e.getMessage() );
        }
    }

    private void buildPressureList(){
        try {
            for(int i = 0; i < 99; i++)
            {
                pressureList.add(jsonObject.getJSONArray("sensor_array").getJSONArray(i).getDouble(2));
            }
            Log.d(TAG, "buildPressureList: " + pressureList);
        } catch(Exception e){
            Log.e(TAG, "buildPressureList: " + e.getMessage() );
        }
    }

    private void buildHumidityList(){
        try {
            for(int i = 0; i < 99; i++)
            {
                humidityList.add(jsonObject.getJSONArray("sensor_array").getJSONArray(i).getDouble(3));
            }
            Log.d(TAG, "buildHumidityList: " + humidityList);
        } catch(Exception e){
            Log.d(TAG, "buildHumidityList: " + e.getMessage());
        }
    }

    public ArrayList<Double> getTemperatureList() {
        return temperatureList;
    }

    public ArrayList<Double> getPressureList() {
        return pressureList;
    }

    public ArrayList<Double> getHumidityList() {
        return humidityList;
    }

    HashMap<String, String> getMap() {
        return map;
    }
}

package com.example.grazebot;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonBuilder {
    private String command;
    private String latitude;
    private String longitude;
    private String movement;

    public JsonBuilder(){
        command = "fetch";
        latitude = "45.00";
        longitude = "100.00";
        movement = "None";
    }

    public String buildJsonObject(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("command", command);
            obj.put("latitude", latitude);
            obj.put("longitude", longitude);
            obj.put("movement", movement);
        } catch (JSONException e){
            e.printStackTrace();
        }
        return obj.toString();
    }
}


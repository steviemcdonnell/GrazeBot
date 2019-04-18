package com.example.grazebot;

import org.json.JSONObject;

import java.util.HashMap;

public class JsonDataTemplate extends HashMap<String, String> {

    JsonDataTemplate(HashMap<String, String> args){
        put("command", args.containsKey("command")?args.get("command"):"fetch");
        put("latitude", args.containsKey("latitude")?args.get("latitude"):"0.00");
        put("longitude", args.containsKey("longitude")?args.get("longitude"):"0.00");
        put("movement", args.containsKey("movement")?args.get("movement"):"still");
    }

    public HashMap<String, String> getData(){
        return this;
    }

    public JSONObject getJsonData(){
        return new JSONObject(this);
    }
}

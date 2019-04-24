//http://tutorials.jenkov.com/java/enums.html
package com.example.grazebot;

public enum Connection_Status {
    CONNECTED(0),
    NOT_CONNECTED(1);

    private int connectCode;

    Connection_Status(int connectCode){
        this.connectCode = connectCode;
    }

    public int getConnectCode(){
        return this.connectCode;
    }

    public void setConnectCode(int code){
        this.connectCode = code;
    }

}

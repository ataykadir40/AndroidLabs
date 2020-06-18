package com.example.androidlabs;

public class Message {
    private String message;
    private String type;

    public Message(String message, String type){
        this.message = message;
        this.type = type;
    }

    public String getType(){return type;}

    public String getMessage(){return message;}


}

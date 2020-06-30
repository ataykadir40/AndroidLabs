package com.example.androidlabs;

public class Message {
    private String message;
    private long id;
    private boolean isSend;
    private boolean isReceived;


    public Message(String message, long id, boolean isSend, boolean isReceived){
        this.message = message;
        this.id = id;
        this.isSend = isSend;
        this.isReceived = isReceived;
    }

    public long getId() {
        return id;
    }

    public boolean isSend() {
        return isSend;
    }

    public boolean isReceived() {
        return isReceived;
    }

    public String getMessage(){return message;}


}

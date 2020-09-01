package com.example.letschat.Model;

public class Chat {

    private String Sender, Receiver, Message;
    private boolean Status;

    public Chat() {
    }

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean status) {
        Status = status;
    }

    public Chat(String sender, String receiver, String message, boolean status) {
        Sender = sender;
        Receiver = receiver;
        Message = message;
        Status = status;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        this.Sender = sender;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        this.Receiver = receiver;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }
}

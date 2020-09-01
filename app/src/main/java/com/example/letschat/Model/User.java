package com.example.letschat.Model;

public class User {
    private String ID, Username, Email, ImgUrl, Status, Search, Info;

    public User() {
    }

    public String getStatus() {
        return Status;
    }

    public String getSearch() {
        return Search;
    }

    public void setSearch(String search) {
        Search = search;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }

    public User(String ID, String username, String email, String imgUrl, String status, String search, String info) {
        this.ID = ID;
        Username = username;
        Email = email;
        ImgUrl = imgUrl;
        Status = status;
        Search = search;
        Info = info;
    }

    public void setStatus(String status) {
        Status = status;
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }
}

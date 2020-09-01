package com.example.letschat.Model;

public class PostModel {
    private String Description;
    private String ImgURL;
    private String PostId;
    private String Publisher;

    public PostModel() {
    }

    public PostModel(String postId, String imgURL, String description, String publisher) {
        PostId = postId;
        ImgURL = imgURL;
        Description = description;
        Publisher = publisher;
    }

    public String getPostId() {
        return PostId;
    }

    public void setPostId(String postId) {
        PostId = postId;
    }

    public String getImgURL() {
        return ImgURL;
    }

    public void setImgURL(String imgURL) {
        ImgURL = imgURL;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String publisher) {
        Publisher = publisher;
    }
}

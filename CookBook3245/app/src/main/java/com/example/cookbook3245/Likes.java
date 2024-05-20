package com.example.cookbook3245;

public class Likes {

    String userId;
    String liked;

    public Likes() {}

    public Likes(String userId, String liked) {
        this.liked = liked;
        this.userId = userId;

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLiked() {
        return liked;
    }

    public void setLiked(String liked) {
        this.liked = liked;
    }
}

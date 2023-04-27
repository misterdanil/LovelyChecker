package com.example.lovelychecker;

public class Post {
    private String email;
    private String password;
    private String username;
    private String confirm_code;

    public Post(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public Post(String confirm_code) {
        this.confirm_code = confirm_code;
    }
}

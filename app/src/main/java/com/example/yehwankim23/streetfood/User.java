package com.example.yehwankim23.streetfood;

public class User {
    private static final String DEFAULT = "";
    private String email;
    private String password;
    private String username;

    User(String email, String password) {
        this(email, password, DEFAULT);
    }

    User(String email, String password, String username) {
        setEmail(email);
        setPassword(password);
        setUsername(username);
    }

    public String getEmail() {
        return email;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    private void setUsername(String username) {
        this.username = username;
    }
}

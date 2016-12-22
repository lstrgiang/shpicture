package com.cs494.common;

public class UserInfo{
    private String username;
    private int status;
    private String statusText;

    public UserInfo(String username, String statusText, int status){
        this.username = username;
        this.status = status;
        this.statusText = statusText;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

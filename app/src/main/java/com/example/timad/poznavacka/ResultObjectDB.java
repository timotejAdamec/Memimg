package com.example.timad.poznavacka;

public class ResultObjectDB {
    private String userID;
    private String result;


    public ResultObjectDB() {
    }

    public ResultObjectDB(String userID, String result) {
        this.userID = userID;
        this.result = result;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}

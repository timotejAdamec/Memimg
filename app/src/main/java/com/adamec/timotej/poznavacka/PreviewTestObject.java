package com.adamec.timotej.poznavacka;

/**
 * objekt testu zobrazený
 */
public class PreviewTestObject {
    private String name;
    private boolean started;
    private String previewImgUrl;
    private String databaseID;
    private String userID;
    private String classification;
    private String content;
    private boolean finished;
    private String activeTestID;
    private String testCode;
    private boolean resultsEmpty;


    public PreviewTestObject() {
    }

    public PreviewTestObject(String name, boolean started, String previewImgUrl, String databaseID, String userID, String classification, String content, boolean finished, String activeTestID, String testCode, boolean resultsEmpty) {
        this.name = name;
        this.started = started;
        this.previewImgUrl = previewImgUrl;
        this.databaseID = databaseID;
        this.userID = userID;
        this.classification = classification;
        this.content = content;
        this.finished = finished;
        this.activeTestID = activeTestID;
        this.testCode = testCode;
        this.resultsEmpty = resultsEmpty;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public String getPreviewImgUrl() {
        return previewImgUrl;
    }

    public void setPreviewImgUrl(String previewImgUrl) {
        this.previewImgUrl = previewImgUrl;
    }

    public String getDatabaseID() {
        return databaseID;
    }

    public void setDatabaseID(String databaseID) {
        this.databaseID = databaseID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public String getActiveTestID() {
        return activeTestID;
    }

    public void setActiveTestID(String activeTestID) {
        this.activeTestID = activeTestID;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    public boolean isResultsEmpty() {
        return resultsEmpty;
    }

    public void setResultsEmpty(boolean resultsEmpty) {
        this.resultsEmpty = resultsEmpty;
    }
}

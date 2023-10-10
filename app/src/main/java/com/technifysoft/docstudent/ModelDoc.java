package com.technifysoft.docstudent;

public class ModelDoc {


    String id,title,uid,pdf;
    long timestamp;

    //default constructor required for firebase
    public ModelDoc(){

    }


    //parametrized constructor


    public ModelDoc(String id, String title, String pdf, String uid, long timestamp) {
        this.id = id;
        this.title = title;
        this.pdf=pdf;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    //Getters and Setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String gettitle() {
        return title;
    }


    public String getPdf(){return pdf;}
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

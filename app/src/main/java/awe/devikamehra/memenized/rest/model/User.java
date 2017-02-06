package awe.devikamehra.memenized.rest.model;

import java.util.ArrayList;

/**
 * Created by Devika on 30-01-2017.
 */

public class User {

    private String uid;
    private String name;
    private String emailId;
    private ArrayList<String> memeList;
    private ArrayList<String> collegeList;

    public User(String uid, String name, String emailId) {
        this.uid = uid;
        this.name = name;
        this.emailId = emailId;
        memeList = null;
        collegeList = null;
    }

    public User(String uid, String name, String emailId, ArrayList<String> memeList, ArrayList<String> collegeList) {
        this.uid = uid;
        this.name = name;
        this.emailId = emailId;
        this.memeList = memeList;
        this.collegeList = collegeList;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public ArrayList<String> getMemeList() {
        return memeList;
    }

    public void setMemeList(ArrayList<String> memeList) {
        this.memeList = memeList;
    }

    public ArrayList<String> getCollegeList() {
        return collegeList;
    }

    public void setCollegeList(ArrayList<String> collegeList) {
        this.collegeList = collegeList;
    }
}

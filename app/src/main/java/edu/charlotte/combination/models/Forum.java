package edu.charlotte.combination.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Forum implements Serializable {
    // forumName, forumOwner, dateTime, forumDescription, forumId, likeCounts;
    //textViewForumCreatedBy, textViewForumText, textViewForumTitle, textViewForumLikesDate, textViewForumLikesCount;
    String ForumCreatorUID;
    String ForumText;
    String ForumTitle;
    String ForumDate;
    Integer ForumLikesCount;
    String ForumId;
    ArrayList<String> likes;
    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }


    String ForumCreatorName;

    public String getForumCreatorName() {
        return ForumCreatorName;
    }

    public void setForumCreatorName(String forumCreatorName) {
        ForumCreatorName = forumCreatorName;
    }

    public Forum() {
    }

    public Forum(String forumCreatorUID, Integer forumLikesCount, String forumDate, String forumTitle, String forumText, String forumId) {
        ForumCreatorUID = forumCreatorUID;
        ForumLikesCount = forumLikesCount;
        ForumDate = forumDate;
        ForumTitle = forumTitle;
        ForumText = forumText;
        ForumId = forumId;
    }

    public Forum(String forumCreatorUID, String forumText, String forumTitle, String forumDate, Integer forumLikesCount) {
        ForumCreatorUID = forumCreatorUID;
        ForumText = forumText;
        ForumTitle = forumTitle;
        ForumDate = forumDate;
        ForumLikesCount = forumLikesCount;
    }

    public String getForumId() {
        return ForumId;
    }

    public void setForumId(String forumId) {
        ForumId = forumId;
    }

    public String getForumCreatorUID() {
        return ForumCreatorUID;
    }

    public void setForumCreatorUID(String forumCreatorUID) {
        ForumCreatorUID = forumCreatorUID;
    }

    public Integer getForumLikesCount() {
        return ForumLikesCount;
    }

    public void setForumLikesCount(Integer forumLikesCount) {
        ForumLikesCount = forumLikesCount;
    }

    public String getForumDate() {
        return ForumDate;
    }

    public void setForumDate(String forumDate) {
        ForumDate = forumDate;
    }

    public String getForumTitle() {
        return ForumTitle;
    }

    public void setForumTitle(String forumTitle) {
        ForumTitle = forumTitle;
    }

    public String getForumText() {
        return ForumText;
    }

    public void setForumText(String forumText) {
        ForumText = forumText;
    }


    @Override
    public String toString() {
        return "Forum{" +
                "ForumCreatedBy='" + ForumCreatorUID + '\'' +
                ", ForumText='" + ForumText + '\'' +
                ", ForumTitle='" + ForumTitle + '\'' +
                ", ForumDate='" + ForumDate + '\'' +
                ", ForumLikesCount='" + ForumLikesCount + '\'' +
                ", ForumId='" + ForumId + '\'' +
                '}';
    }
}

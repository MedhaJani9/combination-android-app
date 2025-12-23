package edu.charlotte.combination.models;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable {

    String text;
    String ownerId;
    String ownerName;
    Date createdAt;
    String commentId;

    public Comment() {

    }

    public Comment(String text, String ownerId, String ownerName, Date createdAt, String commentId) {
        this.text = text;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.createdAt = createdAt;
        this.commentId = commentId;
    }

    public Comment(String text, String ownerId, String ownerName , Date createdAt ) {
        this.text = text;
        this.ownerName = ownerName;
        this.createdAt = createdAt;
        this.ownerId = ownerId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}


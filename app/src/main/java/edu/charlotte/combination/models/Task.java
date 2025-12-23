package edu.charlotte.combination.models;

import java.io.Serializable;

public class Task implements Serializable {

    private  String name;
    private  String category;
    private  Priority priority;
    private  String taskId;
    private String userId;

    public Task() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Task(String name, String category, Priority priority, String userId) {
        this.name = name;
        this.category = category;
        this.priority = priority;
        this.userId = userId;
    }

}

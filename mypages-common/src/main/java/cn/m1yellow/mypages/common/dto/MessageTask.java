package cn.m1yellow.mypages.common.dto;

import com.google.gson.Gson;

import java.util.UUID;


public class MessageTask {

    private String taskId;
    private int command;
    private String payload;


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public MessageTask() {
        taskId = UUID.randomUUID().toString();
    }

    public MessageTask(String payload) {
        taskId = UUID.randomUUID().toString();
        this.payload = payload;
        this.command = -1;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String toString() {
        return String.format("<MessageTask: %s, %s, '%s'>", taskId, command, payload);
    }

}

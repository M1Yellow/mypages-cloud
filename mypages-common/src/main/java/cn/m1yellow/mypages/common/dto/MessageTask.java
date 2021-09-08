package cn.m1yellow.mypages.common.dto;

import cn.m1yellow.mypages.common.util.UUIDGenerateUtil;

import java.io.Serializable;
import java.util.UUID;


public class MessageTask implements Serializable {

    private static final long serialVersionUID = 1L;

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
        taskId = UUIDGenerateUtil.getUUID32();
        this.payload = payload;
        this.command = -1;
    }

    public String toString() {
        return String.format("<MessageTask: %s, %s, '%s'>", taskId, command, payload);
    }

}

package com.vegetable.mypages.constant;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

public enum PlatformInfo {

    MYPAGES(1, "前言"),
    BILIBILI(2, "B站"),
    WEIBO(3, "微博"),
    DOUBAN(4, "豆瓣"),
    ZHIHU(5, "知乎");

    private int id;
    private String name;

    PlatformInfo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static String getName(int id) {
        for (PlatformInfo p : PlatformInfo.values()) {
            if (p.getId() == id) {
                return p.name;
            }
        }
        return null;
    }

    public static PlatformInfo getPlatformInfo(int id) {
        for (PlatformInfo p : PlatformInfo.values()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

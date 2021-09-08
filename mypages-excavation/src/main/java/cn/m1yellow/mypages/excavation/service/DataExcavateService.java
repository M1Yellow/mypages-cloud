package cn.m1yellow.mypages.excavation.service;

import cn.m1yellow.mypages.excavation.bo.UserInfoItem;

import java.util.Map;

/**
 * 数据挖掘服务接口
 */
public interface DataExcavateService {

    /**
     * 单个文件下载，从请求 url 返回的 html 响应中取元素信息
     *
     * @param fromUrl 请求地址
     * @param saveDir 保存路径
     * @param params  补充请求参数
     * @return 用户信息封装
     */
    UserInfoItem singleImageDownloadFromHtml(String fromUrl, String saveDir, Map<String, Object> params);

    /**
     * 单个文件下载，从请求 url 返回的 json 响应中取元素信息
     *
     * @param fromUrl 请求地址
     * @param saveDir 保存路径
     * @param params  补充请求参数
     * @return 用户信息封装
     */
    UserInfoItem singleImageDownloadFromJson(String fromUrl, String saveDir, Map<String, Object> params);

}

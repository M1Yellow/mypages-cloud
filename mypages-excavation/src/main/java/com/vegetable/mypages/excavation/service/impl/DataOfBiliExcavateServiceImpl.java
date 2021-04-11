package com.vegetable.mypages.excavation.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vegetable.mypages.common.service.FileDownloadService;
import com.vegetable.mypages.common.util.HeaderUtil;
import com.vegetable.mypages.common.util.PooledHttpClientAdaptor;
import com.vegetable.mypages.excavation.bo.UserInfoItem;
import com.vegetable.mypages.excavation.service.DataExcavateService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * B站图片获取
 */
@Service("dataOfBiliExcavateService")
public class DataOfBiliExcavateServiceImpl implements DataExcavateService {

    // TODO 这里报错，实际是能通过编译的
    @Resource(name = "httpClientDownloadService")
    FileDownloadService httpClientDownloadService;
    @Resource
    PooledHttpClientAdaptor httpClient;

    /**
     * 从网页元素中获取图片地址下载（暂未使用）
     * @param fromUrl
     * @param saveDir
     * @param params
     * @return
     */
    @Override
    public UserInfoItem singleImageDownloadFromHtml(String fromUrl, String saveDir, Map<String, Object> params) {
        // 获取 html 对象
        String html = httpClient.getHtml(fromUrl, HeaderUtil.getOneHeaderRandom());
        Document doc = Jsoup.parse(html, "UTF-8");

        // 指定获取信息的元素位置
        UserInfoItem infoItem = new UserInfoItem();
        // 用户名
        String userName = doc.getElementById("h-name").text();
        infoItem.setUserName(userName);
        // 个性签名
        String signature = doc.getElementsByClass("h-sign").first().text();
        infoItem.setSignature(signature);
        // 头像地址
        String imgUrl = doc.getElementById("h-avatar").attr("src");
        if (imgUrl.indexOf("@") > -1) { // 截取去掉后面的 webp 参数
            imgUrl = imgUrl.substring(0, imgUrl.indexOf("@"));
        }
        // 头像名称（包含文件类型）
        String[] imgUrlArr = imgUrl.split("/");
        String headImgName = imgUrlArr[imgUrlArr.length - 1];

        // 下载文件
        if (params == null) params = new HashMap<>();
        params.put("userAgent", HeaderUtil.getOneHeaderRandom());
        params.put("referer", fromUrl);
        httpClientDownloadService.singleFileDownload(imgUrl, headImgName, saveDir, params);

        // 保存信息入库，在 admin 模块操作

        infoItem.setHeadImgPath(saveDir);

        return infoItem;
    }

    @Override
    public UserInfoItem singleImageDownloadFromJson(String fromUrl, String saveDir, Map<String, Object> params) {

        String result = httpClient.getHtml(fromUrl, HeaderUtil.getOneHeaderRandom());
        JSONObject resultObject = JSON.parseObject(result);
        JSONObject dataObject = JSON.parseObject(resultObject.getString("data"));

        // 指定获取信息的元素位置
        UserInfoItem infoItem = new UserInfoItem();
        // 用户名
        String userName = dataObject.getString("name");
        infoItem.setUserName(userName);
        // 个性签名
        String signature = dataObject.getString("sign");
        infoItem.setSignature(signature);
        // 头像地址
        String imgUrl = dataObject.getString("face");
        if (imgUrl.indexOf("@") > -1) { // 截取去掉后面的 webp 参数
            imgUrl = imgUrl.substring(0, imgUrl.indexOf("@"));
        }
        infoItem.setHeadImgUrl(imgUrl);

        // 头像名称（包含文件类型）
        String[] imgUrlArr = imgUrl.split("/");
        String headImgName = imgUrlArr[imgUrlArr.length - 1];
        infoItem.setHeadImgName(headImgName);

        // 下载文件
        if (params == null) params = new HashMap<>();
        params.put("userAgent", HeaderUtil.getOneHeaderRandom());
        params.put("referer", fromUrl);
        httpClientDownloadService.singleFileDownload(imgUrl, headImgName, saveDir, params);

        // 保存信息入库，在 admin 模块操作

        infoItem.setHeadImgPath(saveDir.substring(saveDir.indexOf("/images")) + headImgName);

        return infoItem;
    }
}

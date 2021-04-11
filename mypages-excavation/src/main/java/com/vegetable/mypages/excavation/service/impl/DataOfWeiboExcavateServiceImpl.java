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
 * 微博图片获取
 */
@Service("dataOfWeiboExcavateService")
public class DataOfWeiboExcavateServiceImpl implements DataExcavateService {

    // TODO 这里报错，实际是能通过编译的
    @Resource(name = "httpClientDownloadService")
    FileDownloadService httpClientDownloadService;
    @Resource
    PooledHttpClientAdaptor httpClient;


    /**
     * 一部加载渲染内容数据，不方便获取
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
        String userName = doc.getElementsByClass("username").first().text();
        infoItem.setUserName(userName);
        // 个性签名
        String signature = doc.getElementsByClass("pf_intro").first().text();
        infoItem.setSignature(signature);
        // 头像地址
        String imgUrl = doc.getElementsByClass("photo").first().attr("src");
        if (imgUrl.indexOf("?KID=") > -1) { // 截取去掉后面的参数
            imgUrl = imgUrl.substring(0, imgUrl.indexOf("?KID="));
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
        // TODO 请求失败，自动重试
        String result = httpClient.getHtml(fromUrl, HeaderUtil.getOneHeaderRandom());
        JSONObject resultObject = JSON.parseObject(result);
        JSONObject dataObject = JSON.parseObject(resultObject.getString("data"));
        JSONObject userInfoObject = JSON.parseObject(dataObject.getString("userInfo"));

        // 指定获取信息的元素位置
        UserInfoItem infoItem = new UserInfoItem();
        // 用户名
        String userName = userInfoObject.getString("screen_name");
        infoItem.setUserName(userName);
        // 个性签名
        String description = userInfoObject.getString("description");
        String verify = userInfoObject.getString("verified_reason");
        String signature = (verify == null ? "" : verify) + (description == null ? "" : (" " + description));
        infoItem.setSignature(signature);
        // 头像地址
        String imgUrl = userInfoObject.getString("avatar_hd");
        if (imgUrl.indexOf("@") > -1) { // 截取去掉后面的 webp 参数
            imgUrl = imgUrl.substring(0, imgUrl.indexOf("@"));
        }
        infoItem.setHeadImgUrl(imgUrl);

        // 头像名称（包含文件类型）
        String[] imgUrlArr = imgUrl.split("/");
        String headImgName = imgUrlArr[imgUrlArr.length - 1];
        infoItem.setHeadImgName(headImgName);

        // TODO 下载文件之前，删除或覆盖原来的，更换头像之后，文件名不同。也可以判断文件 hash，避免重复下载
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

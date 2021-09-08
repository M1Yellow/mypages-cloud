package cn.m1yellow.mypages.excavation.service.impl;

import cn.m1yellow.mypages.common.service.FileDownloadService;
import cn.m1yellow.mypages.common.service.OssService;
import cn.m1yellow.mypages.common.util.HeaderUtil;
import cn.m1yellow.mypages.common.util.HttpClientUtil;
import cn.m1yellow.mypages.common.util.ObjectUtil;
import cn.m1yellow.mypages.excavation.bo.UserInfoItem;
import cn.m1yellow.mypages.excavation.service.DataExcavateService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 微博图片获取
 */
@Slf4j
@Service("dataOfWeiboExcavateService")
public class DataOfWeiboExcavateServiceImpl implements DataExcavateService {

    // TODO 这里报错，实际是能通过编译的
    @Resource(name = "httpClientDownloadService")
    FileDownloadService httpClientDownloadService;
    @Autowired
    private OssService ossService;

    @Value("${aliyun.oss.bucketName}")
    private String ALIYUN_OSS_BUCKET_NAME;
    @Value("${aliyun.oss.dir.avatar}")
    private String ALIYUN_OSS_DIR_AVATAR;


    /**
     * 一部加载渲染内容数据，不方便获取（暂未使用）
     *
     * @param fromUrl
     * @param saveDir
     * @param params
     * @return
     */
    @Override
    public UserInfoItem singleImageDownloadFromHtml(String fromUrl, String saveDir, Map<String, Object> params) {
        // 获取 html 对象
        String html = HttpClientUtil.getHtml(fromUrl, HeaderUtil.getOneHeaderRandom());
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
        //String headImgName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
        String[] imgUrlArr = imgUrl.split("/");
        String headImgName = imgUrlArr[imgUrlArr.length - 1];
        infoItem.setHeadImgName(headImgName);

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
        String result = HttpClientUtil.getHtml(fromUrl, HeaderUtil.getOneHeaderRandom());
        log.info(">>>> singleImageDownloadFromJson result:{}", result);

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
        //String headImgName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
        String[] imgUrlArr = imgUrl.split("/");
        String headImgName = imgUrlArr[imgUrlArr.length - 1];
        infoItem.setHeadImgName(headImgName);

        // 下载文件
        if (params == null) params = new HashMap<>();
        params.put("userAgent", HeaderUtil.getOneHeaderRandom());
        params.put("referer", fromUrl);
        // TODO 下载之前，校验当前文件名是否跟原来的一致，不一致需要删除原来的文件，再下载新文件，避免文件堆积
        String profileOriginalDir = ObjectUtil.getString(params.get("profileOriginalDir"));
        //String filePath = saveDir.substring(saveDir.lastIndexOf("/images")) + headImgName;
        //String filePath = "/" + ALIYUN_OSS_DIR_AVATAR + headImgName;
        String filePath = ossService.getPathV2(ALIYUN_OSS_DIR_AVATAR, headImgName);

        // 上传到OSS
        InputStream is = null;
        try {
            is = new URL(imgUrl).openStream();
            boolean saveResult = ossService.saveFile(ALIYUN_OSS_BUCKET_NAME, is, profileOriginalDir, filePath);
            if (!saveResult) {
                log.info(">>>> OSS 文件保存失败：{}", filePath);
                return null;
            }
        } catch (Exception e) {
            log.info(">>>> OSS 文件保存异常: ", e);
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("OSS 文件流关闭异常", e);
                }
            }
        }

        // 下载到服务器
        //httpClientDownloadService.singleFileDownload(imgUrl, headImgName, saveDir, params);

        infoItem.setHeadImgPath(filePath);

        return infoItem;
    }

}

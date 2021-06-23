package cn.m1yellow.mypages.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CommonUtil {

    private static final String[] URL_TOP_PATHS = {".com", ".cn", ".net", ".org"};


    /**
     * 从平台用户主页 url 中获取平台英文名称。各个网站的域名千奇百怪，很难做到匹配百分百的网站
     * 复杂例子：https://api.p1.weibo.com.cn/u/234927394247?from=place.bilibili.com/3453535676/video
     *
     * @param url
     * @return
     */
    public static String getPlatformNameEnFromUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }


        return null;
    }


    /**
     * 获取域名地址
     * 举例：
     * https://api.p1.weibo.com.cn/u/234927394247?from=place.bilibili.com/3453535676/video
     * https://api.p1.weibo.com.cn
     *
     * @param url
     * @return
     */
    public static String getSimpleUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        int lastIdx = 0; // 用于判断最终的结束位置
        for (String path : URL_TOP_PATHS) {
            if (url.contains(path)) {
                int idx = url.indexOf(path) + path.length();
                if (idx > lastIdx) {
                    lastIdx = idx;
                }
            }
        }

        return url.substring(0, lastIdx);
    }


    /**
     * 访问路径去掉指定前面 n 级路径
     *
     * @param path 访问路径 uri.getPath()
     * @param n    去掉几级路径
     * @return
     */
    public static String StripPathPrefix(String path, int n) {
        if (StringUtils.isBlank(path)) {
            return path;
        }
        String[] pathArr = path.split("/");
        if (n >= pathArr.length) {
            return "/";
        }
        StringBuffer newPath = new StringBuffer("");
        for (int i = 0; i < pathArr.length; i++) {
            if (i <= n) continue;
            newPath.append("/").append(pathArr[i]);
        }
        path = newPath.toString();
        return path.equals("") ? "/" : path;
    }


    /**
     * 将url参数转换成map
     *
     * @param param aa=11&bb=22&cc=33
     * @return
     */
    public static Map<String, Object> getUrlParams(String param) {
        Map<String, Object> map = new HashMap<String, Object>(0);
        if (StringUtils.isBlank(param)) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
        return map;
    }


    /**
     * 将map转换成url
     *
     * @param map
     * @return
     */
    public static String getUrlParamsByMap(Map<String, Object> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }


}

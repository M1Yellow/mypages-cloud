package cn.m1yellow.mypages.service;


import cn.m1yellow.mypages.dto.OssCallbackResult;
import cn.m1yellow.mypages.dto.OssPolicyResult;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;

/**
 * Oss对象存储管理Service
 * Created by macro on 2018/5/17.
 */
public interface OssService {
    /**
     * Oss上传策略生成
     */
    OssPolicyResult policy();

    /**
     * Oss上传成功回调
     */
    OssCallbackResult callback(HttpServletRequest request);

    /**
     * 文件流形式上传
     * @param bucketName OSS 创建的 bucket 名称
     * @param filePath 文件路径，包含文件名，不包含 OSS 服务器地址和 OSS bucket 名称
     * @param is 文件输入流
     */
    boolean upload(String bucketName, String filePath, InputStream is);

    /**
     * 文件形式上传
     * @param bucketName OSS 创建的 bucket 名称
     * @param filePath 文件路径，包含文件名，不包含 OSS 服务器地址和 OSS bucket 名称
     * @param file 文件对象
     */
    boolean upload(String bucketName, String filePath, File file);

    /**
     * 删除文件
     * @param bucketName OSS bucket 名称
     * @param objectName 文件名称
     */
    boolean delete(String bucketName, String objectName);

}

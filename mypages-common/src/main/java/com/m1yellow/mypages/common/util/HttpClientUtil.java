package com.m1yellow.mypages.common.util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class HttpClientUtil {

    private static final int TIME_OUT = 10 * 1000;
    private static PoolingHttpClientConnectionManager pcm;  // HttpClient 连接池管理类
    private static RequestConfig requestConfig;

    static {
        requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(TIME_OUT)
                .setConnectTimeout(TIME_OUT)
                .setSocketTimeout(TIME_OUT).build();

        pcm = new PoolingHttpClientConnectionManager();
        pcm.setMaxTotal(50);
        pcm.setDefaultMaxPerRoute(10); // 这里可能用不到这个东西。
    }

    public static CloseableHttpClient getHttpClient() {
        return HttpClients.custom()
                .setConnectionManager(pcm)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }
}

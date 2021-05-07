package cn.m1yellow.mypages.common.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class PooledHttpClientAdaptor {

    private static final int DEFAULT_POOL_MAX_TOTAL = 200;
    private static final int DEFAULT_POOL_MAX_PER_ROUTE = 200;

    private static final int DEFAULT_CONNECT_TIMEOUT = 500;
    private static final int DEFAULT_CONNECT_REQUEST_TIMEOUT = 500;
    private static final int DEFAULT_SOCKET_TIMEOUT = 2000;

    private PoolingHttpClientConnectionManager gcm = null;

    private CloseableHttpClient httpClient = null;

    private IdleConnectionMonitorThread idleThread = null;

    // 连接池的最大连接数
    private final int maxTotal;
    // 连接池按route配置的最大连接数
    private final int maxPerRoute;

    // tcp connect的超时时间
    private final int connectTimeout;
    // 从连接池获取连接的超时时间
    private final int connectRequestTimeout;
    // tcp io的读写超时时间
    private final int socketTimeout;

    public PooledHttpClientAdaptor() {
        this(
                PooledHttpClientAdaptor.DEFAULT_POOL_MAX_TOTAL,
                PooledHttpClientAdaptor.DEFAULT_POOL_MAX_PER_ROUTE,
                PooledHttpClientAdaptor.DEFAULT_CONNECT_TIMEOUT,
                PooledHttpClientAdaptor.DEFAULT_CONNECT_REQUEST_TIMEOUT,
                PooledHttpClientAdaptor.DEFAULT_SOCKET_TIMEOUT
        );
    }

    public PooledHttpClientAdaptor(
            int maxTotal,
            int maxPerRoute,
            int connectTimeout,
            int connectRequestTimeout,
            int socketTimeout
    ) {

        this.maxTotal = maxTotal;
        this.maxPerRoute = maxPerRoute;
        this.connectTimeout = connectTimeout;
        this.connectRequestTimeout = connectRequestTimeout;
        this.socketTimeout = socketTimeout;

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();

        this.gcm = new PoolingHttpClientConnectionManager(registry);
        this.gcm.setMaxTotal(this.maxTotal);
        this.gcm.setDefaultMaxPerRoute(this.maxPerRoute);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(this.connectTimeout)                     // 设置连接超时
                .setSocketTimeout(this.socketTimeout)                       // 设置读取超时
                .setConnectionRequestTimeout(this.connectRequestTimeout)    // 设置从连接池获取连接实例的超时
                .build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClient = httpClientBuilder
                .setConnectionManager(this.gcm)
                .setDefaultRequestConfig(requestConfig)
                .build();

        idleThread = new IdleConnectionMonitorThread(this.gcm);
        idleThread.start();

    }

    public String doGet(String url) {
        return this.doGet(url, Collections.EMPTY_MAP, Collections.EMPTY_MAP);
    }

    public String doGet(String url, Map<String, Object> params) {
        return this.doGet(url, Collections.EMPTY_MAP, params);
    }

    public String doGet(String url,
                        Map<String, String> headers,
                        Map<String, Object> params
    ) {

        // *) 构建GET请求头
        String apiUrl = getUrlWithParams(url, params);
        HttpGet httpGet = new HttpGet(apiUrl);

        // *) 设置header信息
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue());
            }
        }

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response == null || response.getStatusLine() == null) {
                return null;
            }

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entityRes = response.getEntity();
                if (entityRes != null) {
                    return EntityUtils.toString(entityRes, "UTF-8");
                }
            }
            return null;
        } catch (IOException e) {
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public String doPost(String apiUrl, Map<String, Object> params) {
        return this.doPost(apiUrl, Collections.EMPTY_MAP, params);
    }

    public String doPost(String apiUrl,
                         Map<String, String> headers,
                         Map<String, Object> params
    ) {

        HttpPost httpPost = new HttpPost(apiUrl);

        // *) 配置请求headers
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // *) 配置请求参数
        if (params != null && params.size() > 0) {
            HttpEntity entityReq = getUrlEncodedFormEntity(params);
            httpPost.setEntity(entityReq);
        }


        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            if (response == null || response.getStatusLine() == null) {
                return null;
            }

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entityRes = response.getEntity();
                if (entityRes != null) {
                    return EntityUtils.toString(entityRes, "UTF-8");
                }
            }
            return null;
        } catch (IOException e) {
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
        }
        return null;

    }

    private HttpEntity getUrlEncodedFormEntity(Map<String, Object> params) {
        List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
                    .getValue().toString());
            pairList.add(pair);
        }
        return new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8"));
    }

    private String getUrlWithParams(String url, Map<String, Object> params) {
        boolean first = true;
        StringBuilder sb = new StringBuilder(url);
        for (String key : params.keySet()) {
            char ch = '&';
            if (first == true) {
                ch = '?';
                first = false;
            }
            String value = params.get(key).toString();
            try {
                String sval = URLEncoder.encode(value, "UTF-8");
                sb.append(ch).append(key).append("=").append(sval);
            } catch (UnsupportedEncodingException e) {
            }
        }
        return sb.toString();
    }


    public void shutdown() {
        idleThread.shutdown();
    }

    // 监控有异常的链接
    private class IdleConnectionMonitorThread extends Thread {

        private final HttpClientConnectionManager connMgr;
        private volatile boolean exitFlag = false;

        public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
            this.connMgr = connMgr;
            setDaemon(true);
        }

        @Override
        public void run() {
            while (!this.exitFlag) {
                synchronized (this) {
                    try {
                        this.wait(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // 关闭失效的连接
                connMgr.closeExpiredConnections();
                // 可选的, 关闭30秒内不活动的连接
                connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
            }
        }

        public void shutdown() {
            this.exitFlag = true;
            synchronized (this) {
                notify();
            }
        }

    }


    /**
     * 单文件下载（httpclient 方式）
     *
     * @param fileUrl  文件下载地址
     * @param fileName 文件名称
     * @param saveDir  保存路径目录
     * @param params   其他参数
     */
    public void singleFileDownload(String fileUrl, String fileName, String saveDir, Map<String, Object> params) {

        HttpGet get = new HttpGet(fileUrl);

        String userAgent = ObjectUtil.getString(params.get("userAgent"));
        String referer = ObjectUtil.getString(params.get("referer"));
        String originalFileMd5 = ObjectUtil.getString(params.get("originalFileMd5"));

        // 设置请求头
        get.setHeader("User-Agent", userAgent);
        get.setHeader("referer", referer);

        /**
         *
         * response.getEntity().getContent() 返回实体的内容流。
         * 每次此方法的调用，可重复的实体都将创建一个InputStream的新实例，因此可以多次使用。
         * 不可重复的实体应返回相同的InputStream实例，因此消耗的次数不得超过一次。
         * HttpEntity 的 isRepeatable 为 false，不可重复，导致内容流使用一次之后就关闭了！
         */

        HttpEntity entity = null;
        try (CloseableHttpResponse response = httpClient.execute(get)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                entity = response.getEntity();
                if (entity != null) {
                    // 重复使用 response entity
                    entity = new BufferedHttpEntity(entity);

                    File filePath = new File(saveDir);
                    File file = new File(saveDir, fileName);

                    if (!filePath.exists()) {
                        filePath.mkdirs();
                        //创建文件
                        //file.createNewFile();
                    }

                    try (
                            OutputStream out = new BufferedOutputStream(new FileOutputStream(file))
                    ) {
                        // 校验 MD5
                        String newFileMd5 = DigestUtils.md5Hex(entity.getContent());
                        if (StringUtils.isNotBlank(newFileMd5) && StringUtils.isNotBlank(originalFileMd5)
                                && newFileMd5.equals(originalFileMd5)) {
                            // 文件 MD5 相同，直接返回，不再继续下载
                            System.out.println(">>>> newFileMd5=originalFileMd5=" + newFileMd5);
                            System.out.println("\n");
                            return;
                        }

                        // 写入本地文件
                        entity.writeTo(out);
                        System.out.println(">>>> 下载完毕：" + file.getAbsolutePath());
                    }
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭实体，HttpEntity 关闭资源
                EntityUtils.consume(entity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 单文件下载（Java net 方式）
     *
     * @param fileUrl
     * @param fileName
     * @param saveDir
     * @param params
     */
    public void singleFileDownloadByNet(String fileUrl, String fileName, String saveDir, Map<String, Object> params) {

        // 构造URL
        URL url = null;
        // 打开连接
        URLConnection con = null;

        try {
            url = new URL(fileUrl);
            con = url.openConnection();
            // 设置请求超时为5s
            con.setConnectTimeout(5 * 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 输出的文件流
        File sf = new File(saveDir);
        if (!sf.exists()) {
            sf.mkdirs();
        }

        try (
                // 输入流
                InputStream is = con.getInputStream();
                OutputStream os = new FileOutputStream(sf.getPath() + "\\" + fileName);
        ) {

            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取网页
     *
     * @param url
     * @param userAgent
     * @return
     */
    public String getHtml(String url, String userAgent) {
        String html = null;
        HttpGet get = new HttpGet(url);
        get.setHeader("User-Agent", userAgent);
        get.setHeader("referer", url);

        HttpEntity entity = null;
        try (CloseableHttpResponse response = httpClient.execute(get)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                entity = response.getEntity();
                if (entity != null) {
                    html = EntityUtils.toString(entity, "UTf-8");
                }
            } else {
                System.out.println(statusCode);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return html;
    }

}

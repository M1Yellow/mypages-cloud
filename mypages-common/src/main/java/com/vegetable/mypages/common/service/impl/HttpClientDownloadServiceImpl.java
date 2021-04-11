package com.vegetable.mypages.common.service.impl;

import com.vegetable.mypages.common.service.FileDownloadService;
import com.vegetable.mypages.common.util.PooledHttpClientAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("httpClientDownloadService")
public class HttpClientDownloadServiceImpl implements FileDownloadService {

    @Autowired
    private PooledHttpClientAdaptor httpClient;

    @Override
    public void singleFileDownload(String fileUrl, String fileName, String saveDir, Map<String, Object> params) {

        httpClient.singleFileDownload(fileUrl, fileName, saveDir, params);
    }
}

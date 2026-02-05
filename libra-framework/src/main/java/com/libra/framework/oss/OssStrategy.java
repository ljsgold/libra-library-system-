package com.libra.framework.oss;

import java.io.InputStream;

public interface OssStrategy {
    /**
     * 上传文件
     * @param inputStream 输入流
     * @param fileName 文件名
     * @return 文件访问URL
     */
    String upload(InputStream inputStream, String fileName);

    /**
     * 删除文件
     * @param url 文件URL
     */
    void delete(String url);
}

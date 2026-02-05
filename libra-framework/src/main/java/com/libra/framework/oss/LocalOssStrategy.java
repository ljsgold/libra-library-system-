package com.libra.framework.oss;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service("local")
public class LocalOssStrategy implements OssStrategy {

    @Value("${oss.local.path:./uploads}")
    private String localPath;

    @Value("${oss.local.domain:http://localhost:8080/files}")
    private String domain;

    @Override
    public String upload(InputStream inputStream, String fileName) {
        String ext = FileUtil.extName(fileName);
        String newName = UUID.randomUUID().toString() + "." + ext;
        File dest = new File(localPath, newName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        FileUtil.writeFromStream(inputStream, dest);
        return domain + "/" + newName;
    }

    @Override
    public void delete(String url) {
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        FileUtil.del(new File(localPath, fileName));
    }
}

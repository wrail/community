package com.wrial.community.provider;
/*
 * @Author  Wrial
 * @Date Created in 17:23 2019/8/3
 * @Description Ucloud授权认证
 */

import cn.ucloud.ufile.UfileClient;
import cn.ucloud.ufile.api.object.ObjectConfig;
import cn.ucloud.ufile.auth.BucketAuthorization;
import cn.ucloud.ufile.auth.ObjectAuthorization;
import cn.ucloud.ufile.auth.UfileObjectLocalAuthorization;
import cn.ucloud.ufile.bean.PutObjectResultBean;
import cn.ucloud.ufile.exception.UfileClientException;
import cn.ucloud.ufile.exception.UfileServerException;
import cn.ucloud.ufile.http.OnProgressListener;
import com.wrial.community.exception.CustomizeErrorCode;
import com.wrial.community.exception.CustomizeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

@Component
@Slf4j
public class UCloudProvider {

    //这个赋值在对象实例化之后了，因此如果不放在try里就初始的是空对象
    @Value("${ucloud.file.public-key}")
    private String public_key;

    @Value("${ucloud.file.private-key}")
    private String private_key;

    @Value("${ucloud.file.bucket-name}")
    private String bucketName;

    @Value("${ucloud.file.expires}")
    private Integer expires;

    @Value("${ucloud.file.region}")
    private String region;

    @Value("${ucloud.file.proxySuffix}")
    private String proxySuffix;


    public String upload(InputStream fileStream, String mimeType, String fileName) {


        String generatedFileName;
        //为了防止重复，给加上UUID
        String[] filePath = fileName.split("\\.");
        if (filePath.length > 1) {
            //就是使用UUID+后缀组成新的fileName
            generatedFileName = UUID.randomUUID().toString() + "." + filePath[filePath.length - 1];
        } else {
            return null;
        }

        try {
            // 对象相关API的授权器
            ObjectAuthorization OBJECT_AUTHORIZER = new UfileObjectLocalAuthorization(public_key, private_key);
            // 对象操作需要ObjectConfig来配置地区和域名后缀
            ObjectConfig config = new ObjectConfig(region, proxySuffix);

            PutObjectResultBean response = UfileClient.object(OBJECT_AUTHORIZER, config)
                    .putObject(fileStream, mimeType)
                    .nameAs(generatedFileName)
                    .toBucket(bucketName)
                    .setOnProgressListener((bytesWritten, contentLength) -> {

                    })
                    .execute();

            if (response != null && response.getRetCode() == 0) {
                //如果上传成功，从存储器上拿到带有过期时间的url实现预览
                String url = UfileClient.object(OBJECT_AUTHORIZER, config)
                        .getDownloadUrlFromPrivateBucket(generatedFileName, bucketName, expires)
                        .createUrl();
                return url;
            } else {
                log.error("upload error,{}", response);
                throw new CustomizeException(CustomizeErrorCode.FILE_UPLOAD_FAIL);
            }

        } catch (UfileClientException e) {
            e.printStackTrace();
            return null;
        } catch (UfileServerException e) {
            e.printStackTrace();
            return null;
        }

    }


}

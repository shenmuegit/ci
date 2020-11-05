package com.ehualu.calabash.s3util;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class S3Util {

    public static void main(String[] args) throws IOException {
        if(null == args){
            return;
        }
//        args = new String[]{"exist","filebucket", "1375183698919456"};
        String accessKeyId = "D2170C84D32FF5E22B14";
        String secretKey = "TCCab5SfNSzIh2pA0y03Oocrsm4AAAFx0y/15Npc";
        String serverUrl = "172.19.41.4";
        String type = args[0];
        String fileBucketName = args[1];
        ClientConfiguration config = new ClientConfiguration();
        //设置连接方式为HTTP，可选参数为HTTP和HTTPS
        config.setProtocol(Protocol.HTTP);
        //设置网络访问超时时间
        config.setConnectionTimeout(50000);
        //获取访问凭证
        AWSCredentials credentials;
        credentials = new BasicAWSCredentials(accessKeyId, secretKey);
        //设置Endpoint
        AwsClientBuilder.EndpointConfiguration endPoint;
        endPoint = new AwsClientBuilder.EndpointConfiguration(serverUrl, "");
        //创建连接
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(config)
                .withEndpointConfiguration(endPoint)
                .withPathStyleAccessEnabled(true)
                .build();

        switch (type){
            case "exist":
                for (int i = 2; i < args.length; i++) {
                    System.out.println("bucketName:" + fileBucketName + ",s3Key:" + args[i] + ":" + (s3Client.doesObjectExist(fileBucketName,args[i])?"存在":"不存在"));
                }
                return;
            case "del":
                for (int i = 2; i < args.length; i++) {
                    s3Client.deleteObject(fileBucketName,args[i]);
                    System.out.println("bucketName:" + fileBucketName + ",s3Key:" + args[i] + ":成功");
                }
                return;
            case "createBucket":
                s3Client.createBucket(fileBucketName);
                System.out.println("bucketName:" + fileBucketName + ":成功");
                return;
            case "get":
                String s3Key = args[2];
                String path = args[3];
                S3Object s3Object = s3Client.getObject(fileBucketName,s3Key);
                File file = new File(path);
                file.createNewFile();
                IOUtils.copy(s3Object.getObjectContent(),new FileOutputStream(file));
                s3Object.close();
                System.out.println("bucketName:" + fileBucketName + ":成功");
                return;
            case "existBucket":
               System.out.println(!s3Client.doesBucketExistV2(fileBucketName)?"否":"是");
        }

    }
}

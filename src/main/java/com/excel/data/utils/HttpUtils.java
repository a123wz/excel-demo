package com.excel.data.utils;


import com.alibaba.excel.util.StringUtils;
import com.excel.data.ExcelDataEnvironment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @ClassName HttpUtils
 * @Description TODO
 * @Author ljq
 * @Date 2024/8/26 15:56
 * @Version 1.0
 */
public class HttpUtils {

    public static void analysisStr(String str,String path) throws Exception{
        ExcelDataEnvironment environment = ExcelDataEnvironment.getInstance();
        if (StringUtils.isNotBlank(str) && str.startsWith("http")){
            String[] imgs = str.split(";");
            path = environment.getAttachmentPath()+File.separator+path;
//            path = path.replaceAll("*","");
            path = path.replaceAll("\\*","");
            String filePath = path;
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            for (String img:imgs){
//                System.out.println(img);
                String fileName = img.substring(img.lastIndexOf("/")+1);
                synchronized (environment.getFileUrlLock()){
                    if (environment.getFileUrlLock().containsKey(img)){
                        System.out.println("url:"+img+"正在下载");
                        return;
                    }
                    environment.getFileUrlLock().put(img,"");
                }
                environment.getFileDownloadExecutor().submit(()->{
                    String filePathName = filePath + File.separator + fileName;
                    try {
                        downloadFile(img.trim(),filePathName );
                    }catch (Exception e){
                        e.printStackTrace();
                        System.out.println("url:\t"+img.trim()+"\t fileName:"+fileName+"\t下载失败");
                        File imgFile = new File(filePathName);
                        System.out.println("下载异常删除文件:"+filePathName+"\t 是否成功:"+imgFile.delete());
                    }
                });
                synchronized (environment.getFileUrlLock()){
                    environment.getFileUrlLock().remove(img);
                }
            }
        }
    }

    public static void downloadFile(String fileUrl, String savePath) throws IOException, InterruptedException {
        File file = new File(savePath);
        if (file.exists()){
//            System.out.println("文件已存在");
            return;
        }
        System.out.println("文件下载" + savePath);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fileUrl))
                .GET() // 默认为GET请求，但显式指定有助于可读性
                .build();

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() == 200) {
            try (InputStream inputStream = response.body();
                 FileOutputStream fileOutputStream = new FileOutputStream(savePath)) {

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
            }
        } else {
            System.out.println("文件下载失败，HTTP响应码为：" + response.statusCode());
            System.out.println("url:\t"+fileUrl+"\t fileName:"+savePath+"\t下载失败");
        }
    }

    public static void main(String[] args) throws Exception {
        String fileUrl = "https://static.dingtalk.com/media/lQDPD3yCO8kx-_3NCfzNBJuwhL881HyDNc4Ga1OZELwVAA_1179_2556.jpg;\n" +
                "https://static.dingtalk.com/media/lQDPD36tBkV1yX3NC9DND8CwQoPIRHpeQ8sGa1OZcgGSAA_4032_3024.jpg";
        String savePath = "1719762730306b902b798643e2734ebcf0a30dfd9dab7.jpg"; // 注意：Windows路径使用双反斜杠
//        analysisStr(fileUrl,);
//        downloadFile(fileUrl, savePath);
        String directoryPath = "D:\\attach\\财务中心-新报销单\\175715\\图片*\\11";
        directoryPath = directoryPath.replaceAll("\\*","");
        Path path = Paths.get(directoryPath);

        try {
            // 使用Files.createDirectories()创建目录，包括所有不存在的父目录
            File imgFile = new File(directoryPath);
            imgFile.delete();
        }catch (Exception e){

        }
    }
}

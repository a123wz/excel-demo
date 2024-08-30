package com.excel.data;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.excel.data.config.ExcelDataConfig;
import com.excel.data.listener.ExcelCompositeHeadDataListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class DataJob {

    private static Map<String, ExcelDataConfig> excelDataConfigMap = new HashMap<>();

    //excel存储目录
    private static String path = DataJob.class.getResource("/").getPath()+File.separator+"excel";

    static {
        excelDataConfigMap.put("demo",new ExcelDataConfig("demo","id",3));
    }

    public static void main(String[] args) {
        //配置excel目录
        System.out.println(excelDataConfigMap.size());
        File file = new File(path);
        File[] files = file.listFiles();
        String basePath = path+File.separator+"sql"+File.separator;
        File baseDir = new File(basePath);
        if(baseDir.exists()){
            for (File f : baseDir.listFiles()) {
                f.delete();
            }
            System.out.println("删除目录"+baseDir.delete());
        }
        baseDir.mkdir();
        for (File f : files) {
            boolean flag = false;
            for (Map.Entry<String, ExcelDataConfig> entry : excelDataConfigMap.entrySet()) {
                String fileName = f.getName();
                fileName = fileName.replaceAll(" ","");
                if (fileName.indexOf(entry.getKey())>=0) {
                    ExcelDataConfig config = entry.getValue();
                    config.setPath(basePath);
                    config.setFileName(entry.getKey());
                    config.setComment(entry.getKey());
                    config.setOriginalFileName(fileName);
                    System.out.println(f.getName()+"-"+f.getAbsolutePath());
                    EasyExcel.read(f.getAbsolutePath(), new ExcelCompositeHeadDataListener(config)).sheet().doRead();
                    flag = true;
                    break;
                }
            }
            if (!flag){
                System.err.println("未匹配到的数据xml"+f.getAbsolutePath());
            }
        }
        System.out.println("所有子表:"+JSON.toJSONString(ExcelDataEnvironment.getInstance().getSubTableDict()));
        System.out.println("所有sql生成完毕");
        ExcelDataEnvironment.getInstance().checkImgDownloadThread();
    }
}

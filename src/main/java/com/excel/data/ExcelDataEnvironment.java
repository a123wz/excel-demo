package com.excel.data;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class ExcelDataEnvironment {

    /**
     * 子表字典
     */
    private Map<String,String> subTableDict = new HashMap<>();

    /**
     * 是否生成子表数据
     */
    private boolean isWriteSubTableData = false;

    /**
     * 文件锁
     */
    private Map<String,String> fileUrlLock = new HashMap<>();

    /**
     * 附件存储地址
     */
    private String attachmentPath = "D:\\attach";

    /**
     * 所有sql存储在一个文件
     */
    private String allSqlFileName = null;

    /**
     * 是否生成删表文件
     */
    private boolean isGenerateDropTableSql = true;

    /**
     * 每批insert数据量
     */
    private final int batchCount= 500;

    private ExcelDataEnvironment() {}

    private static ExcelDataEnvironment environment =new ExcelDataEnvironment();

    /**
     * 下载文件线程池
     */
    private ThreadPoolExecutor fileDownloadExecutor = new ThreadPoolExecutor(20,40,100, TimeUnit.MILLISECONDS,new LinkedBlockingQueue(1000),new ThreadFactory(){
        private AtomicInteger count = new AtomicInteger();
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("data-update-"+count.getAndIncrement());
            return thread;
        }
    },new CallerRunsPolicy());

    public void checkImgDownloadThread(){
        while (fileDownloadExecutor.getActiveCount()>0){
            System.out.println("活动线程数"+fileDownloadExecutor.getActiveCount());
            try {
                Thread.sleep(20000);
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("线程池等待异常:");
            }
        }
        fileDownloadExecutor.shutdown();
    }

    public static ExcelDataEnvironment getInstance() {
//        environment.subTableDict.put("产品^*","product");
//        environment.setAllSqlFileName("excel");
        return environment;
    }
}

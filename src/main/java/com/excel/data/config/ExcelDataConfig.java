package com.excel.data.config;

import com.excel.data.model.HeaderModel;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ExcelDataConfig {

    /**
     * 表名
     */
    private String tableName;

    /**
     * 匹配文件名
     */
    private String fileName;

    /**
     * 原始文件名
     */
    private String originalFileName;

    /**
     * 备注
     */
    private String comment;

    /**
     * 主键
     */
    private String PK;

    /**
     * sql存储目录
     */
    private String path;

    /**
     * 表头行数
     */
    private Integer headerCount=2;

    private HeaderModel pkHeaderModel;

    public ExcelDataConfig(){

    }

    public ExcelDataConfig(String tableName, String PK){
        this();
        this.tableName = tableName;
        this.PK = PK;
    }

    public ExcelDataConfig(String tableName, String PK, Integer headerCount){
        this(tableName, PK);
        this.headerCount = headerCount;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPK() {
        return PK;
    }

    public void setPK(String PK) {
        this.PK = PK;
    }
}

package com.excel.data.base;

import com.alibaba.excel.util.StringUtils;
import com.excel.data.listener.ExcelCompositeHeadDataListener;
import com.excel.data.ExcelDataEnvironment;
import com.excel.data.config.ExcelDataConfig;
import com.excel.data.model.HeaderModel;
import com.excel.data.utils.SqlUtils;
import lombok.Data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class DataResolver {

    private HeaderResolver headerResolver;

    private ExcelDataConfig config;

    private BufferedWriter dataWriter;

    private BufferedWriter selectWriter;

    public DataResolver(ExcelDataConfig config){
        this.config = config;
        try {
            String fileName = config.getFileName();
            boolean isAppend = false;
            if(ExcelDataEnvironment.getInstance().getAllSqlFileName()!=null){
                fileName = ExcelDataEnvironment.getInstance().getAllSqlFileName();
                isAppend = true;
            }
            FileWriter writer = new FileWriter(config.getPath()+fileName+"_data.sql",isAppend);
            this.dataWriter = new BufferedWriter(writer);
            writer = new FileWriter(config.getPath()+fileName+"_select.sql",isAppend);
//            System.out.println("打开文件"+config.getPath()+fileName+"_data.sql");
            this.selectWriter = new BufferedWriter(writer);
//              this.selectWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(config.getPath()+fileName+"_select.sql")), "UTF-8"));
//              this.dataWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(config.getPath()+fileName+"_data.sql")), "UTF-8"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void doAfterAllAnalysed(){
        String sql = headerResolver.getHeaders().stream().filter(e->!e.isSubTable()).map(HeaderModel::getColumn).collect(Collectors.joining(",", "select distinct ", " from " + headerResolver.getTableName()+";"));
        if(!ExcelDataEnvironment.getInstance().isWriteSubTableData()) {
            this.writeSelectSql(sql);
        }
        saveSubTableSql();
        try {
            selectWriter.close();
            dataWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("完成\""+config.getComment()+"\"表数据处理");
    }

    /**
     * 处理子表数据
     */
    private void saveSubTableSql() {
        //生成子表查询sql
        for (Map.Entry<String, List<HeaderModel>> entry : headerResolver.getSubTables().entrySet()) {
            ExcelDataEnvironment.getInstance().getSubTableDict().put(entry.getKey(),"");
            String sql = entry.getValue().stream().map(HeaderModel::getColumn).collect(Collectors.joining(",", "select distinct `" + headerResolver.getPK() + "`, ", " from " + headerResolver.getTableName()));
            String where = entry.getValue().stream().map(e->e.getColumn()+" is not null").collect(Collectors.joining(" or ", " where ", ";"));
            if(ExcelDataEnvironment.getInstance().isWriteSubTableData()) {
                entry.getValue().add(0, config.getPkHeaderModel());
                String subName = ExcelDataEnvironment.getInstance().getSubTableDict().get(entry.getKey());
                String subTableName = config.getTableName() + "_" + (StringUtils.isEmpty(subName) ? entry.getKey() : subName);

                if(ExcelDataEnvironment.getInstance().isGenerateDropTableSql()){
                    writeSelectSql(SqlUtils.getDropTableSql(subTableName));
                }

                String createSql = SqlUtils.getCreateSqlByHeader(entry.getValue(), subTableName, config.getComment() + "_" + entry.getKey());
                this.writeSelectSql(createSql);
                String insertSelect = SqlUtils.getInsertSelectSql(entry.getValue(), subTableName) + sql + where;
                this.writeSelectSql(insertSelect);
            }else{
                this.writeSelectSql(sql + where);
            }
        }
    }

    private void writeSelectSql(String str) {
        try {
            selectWriter.write(str+"\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void afterInitHeader(){
        try {
            if(ExcelDataEnvironment.getInstance().isGenerateDropTableSql()){
                writerDataSql(SqlUtils.getDropTableSql(config.getTableName()));
            }
            String createTableSql = SqlUtils.getCreateSqlByHeader(headerResolver.getHeaders(), headerResolver.getTableName(),config.getComment());
            writerDataSql(createTableSql);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writerDataSql(String sql){
        try {
            dataWriter.write(sql+"\n");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}

package com.excel.data.utils;

import com.alibaba.excel.util.StringUtils;
import com.excel.data.model.HeaderModel;

import java.util.List;
import java.util.stream.Collectors;

public class SqlUtils {

    public static String getCreateSqlByHeader(List<HeaderModel> headers, String tableName){
        return getCreateSqlByHeader(headers,tableName,null);
    }

    public static String getDropTableSql(String tableName){
        return "drop table if exists `"+tableName+"`;";
    }

    public static String getCreateSqlByHeader(List<HeaderModel> headers,String tableName,String comment){
        if(!StringUtils.isEmpty(comment)){
            comment = "COMMENT ='"+comment+"'";
        }else{
            comment = "COMMENT =''";
        }
        return headers.stream().map(HeaderModel::getSql).collect(Collectors.joining(",\n", "create table `" + tableName + "`(\n", "\n)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci "+comment+";"));
    }

    public static String getInsertSelectSql(List<HeaderModel> headers,String tableName){
        return headers.stream().map(HeaderModel::getColumn).collect(Collectors.joining(",","insert into `" + tableName + "` (",") "));
    }
}

package com.excel.data.base;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.excel.data.config.ExcelDataConfig;
import com.excel.data.model.HeaderModel;
import com.excel.data.utils.SqlUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class HeaderResolver {

    private List<Map<Integer, String>> originalHeaders = new ArrayList<>();

    private int headerCount;

    private ExcelDataConfig config;

    private DataResolver dataResolver;

    private String tableName = "table";

    private List<HeaderModel> headers = new ArrayList<>();

    private Map<String, List<HeaderModel>> subTables = new HashMap<>();

    private String insertDataSql;

    private String PK = "数据ID*";

    public HeaderResolver(ExcelDataConfig config, DataResolver dataResolver) {
        this.dataResolver = dataResolver;
        this.config = config;
        this.PK = config.getPK();
        this.headerCount = config.getHeaderCount();
        this.tableName = config.getTableName();
    }

    public boolean isHeader(Integer rows,Map<Integer, String> data){
        if (this.headerCount>=rows){
            this.getOriginalHeaders().add(data);
            if (this.headerCount==rows){
                this.initHeader();
                dataResolver.setHeaderResolver(this);
                dataResolver.afterInitHeader();
            }
            return true;
        }
        return false;
    }

    public void initHeader() {
        int max = originalHeaders.stream().flatMap(e->e.keySet().stream()).max(Integer::compare).get();
        List<String> lastHeaderName = new ArrayList<>();
        for (int i = 0; i < originalHeaders.size(); i++) {
            lastHeaderName.add(i,"");
        }
        for (int i = 0; i <= max; i++) {
            HeaderModel headerModel = new HeaderModel();
            headerModel.setIndex(i);
            headerModel.setSubTable(false);
            String name = "";
            int lastIndex = 0;
            for (int j = 0; j < originalHeaders.size(); j++) {
                String value = originalHeaders.get(j).get(i);
//                lastHeaderName[j] = value==null?lastHeaderName[j]:value;
                lastHeaderName.set(j,value==null?lastHeaderName.get(j):value);
                if(value!=null){
                    lastIndex = j;
                    name = originalHeaders.get(j).get(i);
                }
            }
            headerModel.setName(name);
            if (lastIndex!=0){
                headerModel.setSubTable(true);
                String tableName  = String.join("_", lastHeaderName.subList(0, lastIndex));
                headerModel.setName(tableName+"_"+name);
                if (subTables.get(tableName) == null) {
                    subTables.put(tableName, new ArrayList<>());
                }

                subTables.get(tableName).add(headerModel);
            }
            if (name.equals(this.config.getPK())) {
                this.config.setPkHeaderModel(headerModel);
            }
            headers.add(headerModel);
        }
        insertDataSql = headers.stream().map(HeaderModel::getColumn).collect(Collectors.joining(",", "INSERT INTO `" + tableName + "`(", ")"));
    }
}

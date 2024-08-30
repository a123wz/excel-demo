package com.excel.data.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.excel.data.ExcelDataEnvironment;
import com.excel.data.base.DataResolver;
import com.excel.data.base.HeaderResolver;
import com.excel.data.config.ExcelDataConfig;
import com.excel.data.model.DataModel;
import com.excel.data.model.HeaderModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExcelCompositeHeadDataListener extends AnalysisEventListener<Map<Integer, String>> {
    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private int allCount = 0;

    private static final int BATCH_COUNT = ExcelDataEnvironment.getInstance().getBatchCount();

    private DataResolver dataResolver;

    private HeaderResolver headerResolver;

    private List<DataModel> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    private ExcelDataConfig config;


    public ExcelCompositeHeadDataListener(ExcelDataConfig config) {
        dataResolver = new DataResolver(config);
        headerResolver = new HeaderResolver(config,dataResolver);
        this.config = config;
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        allCount++;
        if (headerResolver.isHeader(allCount,data)) {
            return;
        }

        DataModel dataModel = new DataModel();
        dataModel.setHeaders(headerResolver.getHeaders());
        dataModel.setData(data);
        dataModel.setIndex(allCount);
        dataModel.setExcelDataConfig(this.config);
        cachedDataList.add(dataModel);
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        dataResolver.doAfterAllAnalysed();
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        String sql = cachedDataList.stream().map(DataModel::getInsertSql).collect(Collectors.joining(",\n", headerResolver.getInsertDataSql() + " values ", ";"));
        dataResolver.writerDataSql(sql);
    }
}
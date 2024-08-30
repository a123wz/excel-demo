package com.excel.data.model;

import com.alibaba.excel.util.StringUtils;
import com.excel.data.config.ExcelDataConfig;
import com.excel.data.utils.HttpUtils;
import lombok.Data;

import java.io.File;
import java.util.List;
import java.util.Map;

@Data
public class DataModel {

    private List<HeaderModel> headers;

    private Map<Integer, String> data;

    private ExcelDataConfig excelDataConfig;

    private int index;

    public List<HeaderModel> getHeaders() {
        return headers;
    }

    public void setHeaders(List<HeaderModel> headers) {
        this.headers = headers;
    }

    public Map<Integer, String> getData() {
        return data;
    }

    public void setData(Map<Integer, String> data) {
        this.data = data;
    }

    public String getInsertSql(){
        StringBuilder sb = new StringBuilder("(");
        for(HeaderModel header: headers){
            String value = data.get(header.getIndex());
            try {
                if (StringUtils.isNotBlank(value) && value.startsWith("http")) {
                    HeaderModel pk = excelDataConfig.getPkHeaderModel();
                    String pkValue = data.get(pk.getIndex());
//                    HttpUtils.analysisStr(value,  xbbDataConfig.getFileName()+File.separator+index+"-"+pkValue + File.separator + header.getName());
                    HttpUtils.analysisStr(value,  excelDataConfig.getFileName()+File.separator+pkValue + File.separator + header.getName());
                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("解析图片出错:{}");
                throw new RuntimeException(e);
            }
            String str = StringUtils.isBlank(value)?"null":"'"+value.replaceAll("'","\\\\\'")+"'";
            sb.append(str).append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(")");
        return sb.toString();
    }
}

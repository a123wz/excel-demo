package com.excel.data.model;


public class  HeaderModel {

    private int index;

    private String name;

    private boolean isSubTable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public boolean isSubTable() {
        return isSubTable;
    }

    public void setSubTable(boolean subTable) {
        isSubTable = subTable;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getSql(){
        return "`"+name+"` text null";
    }

    public String getColumn(){
        return "`"+name+"`";
    }
}

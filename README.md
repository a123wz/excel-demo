在启动类DataJob中配置excel存储目录
```java
//excel存储目录
private static String path = "D:\\excel";
```
执行DataJob后会在excel文件夹下生成sql目录,里面会生成"_data.sql"的数据文件,"_select.sql"的查询文件
# 简介

idea插件，自动生成mapper,dao,service,search

* mapper: xml文件
* dao: 数据库操作接口
* service: 服务封装
* search: 查询

## 配置文件如下

```
### config Pojos to generate, split by |
pojos=VerifyType
### baseSearch 基础查询类
baseSearch=BaseSearch
### path directory configuration
mapper.path=service/src/main/resources/com/zhouhua/cuanju/dao
dao.path=service/src/main/java/com/zhouhua/cuanju/dao
service.path=service/src/main/java/com/zhouhua/cuanju/service
search.path=service/src/main/java/com/zhouhua/cuanju/search
createsql.path=doc/sql/generator
```

## 使用方法

通过idea generate面板使用
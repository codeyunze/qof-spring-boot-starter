# MongoDB 元数据接入示例（勿与 MySQL starter 同时引入）

## Maven

```xml
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter-persistence-mongo</artifactId>
  <version>${qof.version}</version>
</dependency>
```

## 配置

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://user:pass@127.0.0.1:27017/qof?authSource=admin

# 对象存储仍按原方式配置，例如 local
qof:
  local:
    enable: true
    default-storage-station: c-station
    multiple:
      c-station:
        filepath: ./files/c
```

## 集合与字段

- 集合名：`sys_files`
- `_id`：业务 fileId（Long，雪花）
- `invalid`：0 有效；删除时写入时间戳做逻辑删除
- 其余字段与 MySQL `sys_files` 对齐（file_name / file_path / file_storage_mode 等）

启动时会自动创建常用索引；无需手写建表 DDL。

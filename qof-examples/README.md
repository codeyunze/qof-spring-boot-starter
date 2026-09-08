# qof-examples

演示工程：只演示 **Local + MySQL 元数据 + 内置 Web** 主路径。

## 依赖（最小集）

```text
qof-spring-boot-starter-web              # HTTP + 传递 starter（含 local）
qof-spring-boot-starter-persistence-mysql
```

**不要**在本模块堆叠 cos / oss / s3；云存储请另建示例或在业务工程按需引入对应 `starter-*`。

## 约束

**禁止被业务工程依赖。** 业务侧请依赖正式 starter 坐标。

## 运行前

1. 按 `qof-spring-boot-starter-persistence-mysql/examples/schema.sql` 建表  
2. 修改 `application.yaml` 中的数据源  
3. `mvn -pl qof-examples spring-boot:run`

## 相关文档

- 根目录 [README.md](../README.md)
- [元数据持久化可插拔设计](../docs/元数据持久化可插拔设计.md)

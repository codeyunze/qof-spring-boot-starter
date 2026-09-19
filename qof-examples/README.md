# qof-examples

演示工程：只演示 **Local + MySQL 元数据 + 内置 Web** 主路径。

## 依赖（最小集）

```text
qof-spring-boot-starter-web
qof-storage-local
qof-spring-boot-starter-persistence-mysql
```

**不要**在本模块堆叠 cos / oss / s3；云存储请在业务工程按需引入对应 `qof-storage-*`。

## 约束

**禁止被业务工程依赖。**

## 运行前

1. 按 `../qof-persistence-mysql` 建表  
2. 修改 `application.yaml` 中的数据源  
3. `mvn -pl qof-examples spring-boot:run`

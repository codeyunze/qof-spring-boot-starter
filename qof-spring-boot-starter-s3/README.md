# qof-spring-boot-starter-s3

S3 兼容存储裁剪 Starter（RustFS / MinIO 等），不附带 Web。

## 作用

- 传递依赖 `qof-storage-s3`
- 与默认 Starter 组合，按需启用 S3 兼容存储

## 依赖示例

```xml
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter</artifactId>
  <version>17.2.0-SNAPSHOT</version>
</dependency>
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter-s3</artifactId>
  <version>17.2.0-SNAPSHOT</version>
</dependency>
```

## 相关模块

- 实现：[`qof-storage-s3`](../qof-storage-s3/README.md)
- mode 字符串以 `ObjectStorageProvider#mode()` 实现为准

## 注意

凭证、endpoint 走配置或环境变量，禁止硬编码进仓库。

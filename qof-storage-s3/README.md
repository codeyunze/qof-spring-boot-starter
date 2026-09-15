# qof-storage-s3

S3 兼容对象存储实现（RustFS / MinIO 等），实现 `ObjectStorageProvider`（mode = `rustfs`），含 Spring Boot 自动配置。

## 何时引入

需要 S3 兼容存储时直接依赖本模块。

```xml
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-storage-s3</artifactId>
</dependency>
```

## 配置要点

```yaml
qof:
  rustfs:
    enable: true
    # endpoint / access-key / secret-key / bucket 等
```

## 依赖

- AWS S3 SDK
- `qof-core`

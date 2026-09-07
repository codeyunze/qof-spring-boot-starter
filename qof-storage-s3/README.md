# qof-storage-s3

S3 兼容对象存储实现（RustFS / MinIO 等），实现 `ObjectStorageProvider`（mode = `rustfs`）。

## 作用

- 基于 AWS S3 SDK（兼容协议）访问自建 / 兼容 S3 的对象存储
- 与 COS/OSS 并列，按需引入，不进入默认 Starter

## 何时引入

推荐：`qof-spring-boot-starter-s3`。

## 配置要点

```yaml
qof:
  rustfs:
    enable: true
    # endpoint / access-key / secret-key / bucket 等
```

调用：`qofClientFactory.buildClient("rustfs")`。

## 依赖

- AWS SDK S3（兼容协议）
- `qof-spi` / `qof-core`
- 禁止依赖 `qof-web` / `qof-examples`

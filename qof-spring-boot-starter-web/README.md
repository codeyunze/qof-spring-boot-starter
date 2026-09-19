# qof-spring-boot-starter-web

内置 HTTP（`FileController` 等）。**不**传递 storage / persistence，宿主需自行引入：

- 至少一个 `qof-storage-*`（local / cos / oss / s3）
- `qof-persistence-mysql` 或 `...-mongo`，或自建 `FileMetadataRepository`

```yaml
qof:
  web:
    enabled: true
```

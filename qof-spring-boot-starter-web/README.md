# qof-spring-boot-starter-web

内置 HTTP（`FileController` 等）。**不**传递 persistence，宿主需自行引入：

- `qof-spring-boot-starter-persistence-mysql`，或  
- 自建 `FileMetadataRepository`（列表另需 `FileMetadataQuery`）

```yaml
qof:
  web:
    enabled: true
```

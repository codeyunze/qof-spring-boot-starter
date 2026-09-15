# qof-storage-local

本地磁盘对象存储实现，实现 `ObjectStorageProvider`（mode = `local`），含 Spring Boot 自动配置。

## 何时引入

直接依赖本模块即可（已传递 `qof-core`）。

## 配置要点

```yaml
qof:
  local:
    enable: true
    filepath: /data/files
```

## 依赖

- `qof-core`
- **不**依赖云 SDK、**不**依赖 MyBatis

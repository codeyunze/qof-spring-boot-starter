# qof-storage-local

本地磁盘对象存储实现，实现 `ObjectStorageProvider`（mode = `local`）。

## 作用

- 将文件读写落到本地文件系统
- 作为默认 Starter 的内置存储，适合开发环境与单机场景

## 何时引入

- 使用 `qof-spring-boot-starter` 时已包含本模块
- 也可单独依赖本模块并配合 `qof-core`

## 配置要点

```yaml
qof:
  local:
    enable: true
    filepath: /data/files
```

## 依赖

- `qof-api` / `qof-core`（实现 SPI 与抽象客户端）
- **不**依赖云 SDK、**不**依赖 MyBatis

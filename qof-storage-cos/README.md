# qof-storage-cos

腾讯云 COS 对象存储实现，实现 `ObjectStorageProvider`（mode = `cos`）。

## 作用

- 封装腾讯云 COS SDK，提供与本地存储一致的上传/下载/删除门面
- 仅在需要 COS 时引入，避免默认 Starter 背负云 SDK

## 何时引入

推荐：`qof-spring-boot-starter-cos`（会传递本模块）。  
或在已有 `qof-spring-boot-starter` 基础上再加本模块 / 对应 Starter。

## 配置要点

```yaml
qof:
  cos:
    enable: true
    # secret-id / secret-key / region / bucket 等按实际配置
```

## 依赖

- 腾讯云 COS SDK
- `qof-spi` / `qof-core`
- 禁止依赖 `qof-web` / `qof-examples`

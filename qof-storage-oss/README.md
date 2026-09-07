# qof-storage-oss

阿里云 OSS 对象存储实现，实现 `ObjectStorageProvider`（mode = `oss`）。

## 作用

- 封装阿里云 OSS SDK，提供统一文件操作门面
- 按需裁剪：不用 OSS 则不必引入本模块及阿里云依赖

## 何时引入

推荐：`qof-spring-boot-starter-oss`。

## 配置要点

```yaml
qof:
  oss:
    enable: true
    # endpoint / access-key / secret-key / bucket 等按实际配置
```

## 依赖

- 阿里云 OSS SDK
- `qof-spi` / `qof-core`
- 禁止依赖 `qof-web` / `qof-examples`

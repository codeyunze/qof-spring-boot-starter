# qof-storage-oss

阿里云 OSS 对象存储实现，实现 `ObjectStorageProvider`（mode = `oss`），含 Spring Boot 自动配置。

## 何时引入

需要 OSS 时直接依赖本模块。

```xml
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-storage-oss</artifactId>
</dependency>
```

## 配置要点

```yaml
qof:
  oss:
    enable: true
    # endpoint / access-key / secret-key / bucket 等按实际配置
    # secret-id 可作为 access-key 的别名
```

## 依赖

- 阿里云 OSS SDK
- `qof-core`

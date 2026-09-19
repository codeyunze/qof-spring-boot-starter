# qof-storage-cos

腾讯云 COS 对象存储实现，实现 `ObjectStorageProvider`（mode = `cos`），含 Spring Boot 自动配置。

## 何时引入

需要 COS 时直接依赖本模块。

```xml
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-storage-cos</artifactId>
</dependency>
```

## 配置要点

```yaml
qof:
  cos:
    enable: true
    # access-key / secret-key / region / bucket 等按实际配置
    # secret-id 可作为 access-key 的别名
```

## 依赖

- 腾讯云 COS SDK
- `qof-core`

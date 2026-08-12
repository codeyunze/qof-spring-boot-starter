# qof-spring-boot-starter

默认 Starter：**一行依赖**即可使用核心能力 + 本地存储。

## 作用

- 聚合：`qof-spring-boot-autoconfigure` + `qof-core` + `qof-storage-local`
- 第三方接入的首选坐标（真正的 Starter，不是演示工程）

## 包含能力

| 能力 | 是否包含 |
|---|---|
| SDK 门面 / 工厂 | ✅ |
| 本地磁盘存储 | ✅ |
| 云存储 COS/OSS/S3 | ❌ 另引对应 starter |
| 元数据落库 | ❌ 另引 persistence |
| 内置 HTTP | ❌ 另引 starter-web |

## 依赖示例

```xml
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter</artifactId>
  <version>17.2.0</version>
</dependency>
```

```yaml
qof:
  local:
    enable: true
    filepath: /data/files
```

## 相关文档

- 根目录 [README.md](../README.md)
- [升级指南-17.2.0](../docs/migration/升级指南-17.2.0.md)

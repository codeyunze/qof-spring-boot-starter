# qof-spring-boot-starter-oss

阿里云 OSS 裁剪 Starter：仅增加 OSS 存储能力，不附带 Web。

## 作用

- 传递依赖 `qof-storage-oss`
- 与默认 Starter 组合，按需启用 OSS

## 依赖示例

```xml
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter</artifactId>
  <version>17.2.0</version>
</dependency>
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter-oss</artifactId>
  <version>17.2.0</version>
</dependency>
```

```yaml
qof:
  oss:
    enable: true
```

## 相关模块

- 实现：[`qof-storage-oss`](../qof-storage-oss/README.md)
- 调用：`qofClientFactory.buildClient("oss")`

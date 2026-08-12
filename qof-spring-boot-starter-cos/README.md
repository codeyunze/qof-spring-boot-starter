# qof-spring-boot-starter-cos

腾讯云 COS 裁剪 Starter：仅增加 COS 存储能力，不附带 Web。

## 作用

- 传递依赖 `qof-storage-cos`
- 与默认 Starter 组合使用，按需启用 COS

## 依赖示例

```xml
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter</artifactId>
  <version>17.2.0</version>
</dependency>
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter-cos</artifactId>
  <version>17.2.0</version>
</dependency>
```

```yaml
qof:
  cos:
    enable: true
```

## 相关模块

- 实现：[`qof-storage-cos`](../qof-storage-cos/README.md)
- 调用：`qofClientFactory.buildClient("cos")`

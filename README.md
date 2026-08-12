# QOF（快联文件桥）

> 当前版本：**17.2.0**（JDK 17 / Spring Boot 3.5.x）  
> 架构说明：[`docs/架构优化方案.md`](docs/架构优化方案.md)  
> 迁移说明：[`docs/migration/升级指南-17.2.0.md`](docs/migration/升级指南-17.2.0.md)

## 模块一览

各模块说明见对应目录下 `README.md`。

| Artifact | 职责 |
|---|---|
| [`qof-api`](qof-api/README.md) | SPI（Provider / Listener / Metadata） |
| [`qof-core`](qof-core/README.md) | 门面、工厂、校验、DTO（**不含**云 SDK / DB） |
| [`qof-storage-local`](qof-storage-local/README.md) | 本地存储 |
| [`qof-storage-cos`](qof-storage-cos/README.md) | 腾讯云 COS |
| [`qof-storage-oss`](qof-storage-oss/README.md) | 阿里云 OSS |
| [`qof-storage-s3`](qof-storage-s3/README.md) | S3 兼容（RustFS / MinIO） |
| [`qof-persistence-mybatis`](qof-persistence-mybatis/README.md) | 元数据持久化（MyBatis-Plus） |
| [`qof-spring-boot-autoconfigure`](qof-spring-boot-autoconfigure/README.md) | Boot 自动配置聚合 |
| [`qof-spring-boot-starter`](qof-spring-boot-starter/README.md) | 默认 Starter = core + **local** |
| [`qof-spring-boot-starter-web`](qof-spring-boot-starter-web/README.md) | HTTP + persistence |
| [`qof-spring-boot-starter-cos`](qof-spring-boot-starter-cos/README.md) / [`oss`](qof-spring-boot-starter-oss/README.md) / [`s3`](qof-spring-boot-starter-s3/README.md) | 按存储裁剪 |
| [`qof-spring-boot-starter-persistence`](qof-spring-boot-starter-persistence/README.md) | 仅持久化 |
| [`qof-web`](qof-web/README.md) | HTTP 实现（建议经 starter-web 引入） |
| [`qof-examples`](qof-examples/README.md) | 演示工程，**禁止**业务依赖 |

## 推荐接入

**仅本地盘 SDK：**

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

**需要 COS + 持久化 + HTTP：**

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
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter-persistence</artifactId>
  <version>17.2.0</version>
</dependency>
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter-web</artifactId>
  <version>17.2.0</version>
</dependency>
```

```yaml
qof:
  persistent-enable: true
  web:
    enabled: true
    expose-advice: true
  cos:
    enable: true
    # ...
```

## SDK 调用

```java
@Resource
private QofClientFactory qofClientFactory;

Long id = qofClientFactory.buildClient("local").upload(in, info);
```

## 自定义存储

实现 `io.github.codeyunze.spi.ObjectStorageProvider` 并注册为 Spring Bean。

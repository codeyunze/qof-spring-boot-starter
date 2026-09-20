# QOF（快联文件桥）

> 当前版本：**17.2.0**（JDK 17 / Spring Boot 3.5.x）  
> 架构说明：[`docs/架构优化方案.md`](docs/架构优化方案.md)  
> 迁移说明：[`docs/migration/升级指南-17.2.0.md`](docs/migration/升级指南-17.2.0.md)

## 模块一览

各模块说明见对应目录下 `README.md`。

| Artifact | 职责 |
|---|---|
| [`qof-core`](qof-core/README.md) | SPI 契约 + 门面、工厂、校验、DTO（**不含**云 SDK / DB） |
| [`qof-storage-local`](qof-storage-local/README.md) | 本地存储（含自动配置） |
| [`qof-storage-cos`](qof-storage-cos/README.md) | 腾讯云 COS（含自动配置） |
| [`qof-storage-oss`](qof-storage-oss/README.md) | 阿里云 OSS（含自动配置） |
| [`qof-storage-s3`](qof-storage-s3/README.md) | S3 兼容（RustFS / MinIO，含自动配置） |
| [`qof-spring-boot-starter-web`](qof-spring-boot-starter-web/README.md) | 内置 HTTP（**不**传递 storage / persistence） |
| [`qof-persistence-mysql`](qof-persistence-mysql/README.md) | MySQL 元数据（实现 + 自动配置） |
| [`qof-persistence-mongo`](qof-persistence-mongo/README.md) | MongoDB 元数据（实现 + 自动配置） |
| [`qof-examples`](qof-examples/README.md) | 演示工程，**禁止**业务依赖 |

## 推荐接入

**仅本地盘 SDK：**

```xml
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-storage-local</artifactId>
  <version>17.3.1</version>
</dependency>
```

```yaml
qof:
  local:
    enable: true
    filepath: /data/files
```

**需要 COS + MySQL 元数据 + HTTP：**

```xml
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-storage-cos</artifactId>
  <version>17.3.1</version>
</dependency>
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter-persistence-mysql</artifactId>
  <version>17.3.1</version>
</dependency>
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter-web</artifactId>
  <version>17.3.1</version>
</dependency>
```

```yaml
qof:
  web:
    enabled: true
    expose-advice: true
  cos:
    enable: true
    # ...
```

**需要 RustFS（S3 兼容）+ MongoDB 元数据 + HTTP：**

MongoDB 与 MySQL 元数据不要同时引入。调用时 `fileStorageMode` 传 `s3`。

```xml
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-storage-s3</artifactId>
  <version>17.3.1</version>
</dependency>
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-persistence-mongo</artifactId>
  <version>17.3.1</version>
</dependency>
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter-web</artifactId>
  <version>17.3.1</version>
</dependency>
```

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://user:pass@127.0.0.1:27017/qof?authSource=admin
qof:
  web:
    enabled: true
    expose-advice: true
  s3:
    enable: true
    default-storage-station: c-station
    multiple:
      c-station:
        endpoint: http://127.0.0.1:9000
        access-key: ${S3_ACCESS_KEY}
        secret-key: ${S3_SECRET_KEY}
        bucket-name: qof
        filepath: /qof-c
```

MinIO 等其它 S3 兼容存储同样走 `qof-storage-s3` / `qof.s3`。集合 `sys_files` 启动时自动建索引，无需手写 DDL。

## SDK 调用

```java
@Resource
private QofClientFactory qofClientFactory;

Long id = qofClientFactory.buildClient("local").upload(in, info);
```

## 自定义存储 / 元数据

- 对象存储：实现 `ObjectStorageProvider` 并注册为 Bean  
- 元数据：实现 `FileMetadataRepository`（可选 `FileMetadataQuery` / `FileLifecycleListener`）  

## 本地开发（IDEA / 命令行均可）

**环境：** JDK **17**、Maven 3.9+（或直接用仓库自带的 `mvnw` / `mvnw.cmd`）。  
`.idea` / `*.iml` 已 gitignore，**请各自用 IDE 重新导入**，不要提交本机 IDE 文件。

### IntelliJ IDEA（推荐开源协作者）

1. **File → Open** 选择仓库根目录的 `pom.xml`（以 Maven 工程打开，不要只当普通文件夹）
2. 信任项目后等待 Maven 导入完成，Project 中应出现全部子模块（`qof-core`、`qof-examples` 等）
3. 设置 Project SDK = **17**
4. 运行 `qof-examples` 中的 `io.github.codeyunze.examples.QofApplication`  
   - 先按 `qof-persistence-mysql` 建表，并改好 `qof-examples` 的数据源配置

若出现「程序包 io.github.codeyunze.xxx 不存在」：多为未正确导入多模块。处理顺序：

1. 右键根 `pom.xml` → **Add as Maven Project** / Maven 工具窗口 **Reload**
2. **File → Invalidate Caches → Invalidate and Restart**
3. 仍异常时删除本地 `.idea` 与各模块 `*.iml` 后，重新 Open 根 `pom.xml`

> 请勿与另一套 IDE（例如同时开 Cursor 与 IDEA）共用同一工作区并互相改写 `.idea`，容易把模块图冲坏。

### 命令行

```bash
./mvnw clean install -DskipTests
./mvnw -pl qof-examples -am spring-boot:run
```

Windows：

```bat
mvnw.cmd clean install -DskipTests
mvnw.cmd -pl qof-examples -am spring-boot:run
```


# qof-spring-boot-starter-persistence

元数据持久化 Starter：仅增加「文件信息落库」能力，**不附带内置 HTTP**。

## 模块作用

本模块是**依赖聚合 Starter**（无业务 Java 源码），引入后传递依赖：

```text
qof-spring-boot-starter-persistence
└── qof-persistence-mybatis
    ├── MyBatis-Plus / MySQL 驱动
    ├── 实体 SysFiles、FilesMapper、FilesService
    └── 自动配置：用落库版 QofExtService 覆盖 core 的 NoopQofExtService
```

| 能力 | 未引入 persistence | 引入本 Starter 且 `qof.persistent-enable=true` |
|---|---|---|
| 对象存储上传/下载（按路径） | 可用 | 可用 |
| 上传后写入 `sys_files` | 否 | 是 |
| 按 `fileId` 查元数据再下载/删除 | 否（Noop 抛错） | 是 |
| 内置 `/file/**` HTTP | 否 | 否（需另引 `starter-web`） |

典型链路：

```text
宿主调用 QofClient.upload/download/delete
        │
        ▼
QofExtService（persistence 自动注入的落库实现）
        │
        ├── afterUpload  → FilesService.save → sys_files
        ├── getByFileId  → 查路径/模式/存储站
        └── beforeDelete → FilesService.deleteByFileId
```

## 何时引入

- 宿主**自建 Controller**，但仍需要组件侧文件元数据表
- 需要落库，又不想引入 `qof-spring-boot-starter-web`

若同时需要内置 HTTP，直接用 `qof-spring-boot-starter-web`（已含 persistence）即可。

## 依赖与配置

```xml
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter</artifactId>
  <version>17.2.0-SNAPSHOT</version>
</dependency>
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter-persistence</artifactId>
  <version>17.2.0-SNAPSHOT</version>
</dependency>
```

```yaml
qof:
  persistent-enable: true
```

还需宿主配置可用数据源（Spring Boot 常规 `spring.datasource.*`）。建表 SQL 见下方案例。

## 案例代码（本模块 `examples/`）

> `examples/` **不参与编译、不打进 jar**，复制到宿主工程使用。

| 文件 | 说明 |
|---|---|
| [`examples/schema.sql`](./examples/schema.sql) | `sys_files` 建表语句 |
| [`examples/application-persistence-example.yaml`](./examples/application-persistence-example.yaml) | 自建 Controller 场景配置 |
| [`examples/HostFileFacadeExample.java`](./examples/HostFileFacadeExample.java) | 宿主门面：上传/下载/分页/删除 |
| [`examples/CustomQofExtServiceExample.java`](./examples/CustomQofExtServiceExample.java) | 继承 `AbstractQofServiceImpl` 做审计扩展 |

## 相关模块

- 实现：[`qof-persistence-mybatis`](../qof-persistence-mybatis/README.md)
- 完整可运行演示：[`qof-examples`](../qof-examples/README.md)

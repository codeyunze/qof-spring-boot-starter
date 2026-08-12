# qof-spring-boot-starter-persistence

元数据持久化 Starter：仅增加落库能力，不附带内置 HTTP。

## 作用

- 传递依赖 `qof-persistence-mybatis`
- 用 DB 实现覆盖 core 默认的 Noop 扩展，使按 `fileId` 下载/删除可用

## 何时引入

- 宿主自建 Controller，但仍需要组件侧文件元数据表
- 需要落库但不想引入 `starter-web`

若同时需要内置 HTTP，可直接用 `qof-spring-boot-starter-web`（已含 persistence）。

## 依赖示例

```xml
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter</artifactId>
  <version>17.2.0</version>
</dependency>
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter-persistence</artifactId>
  <version>17.2.0</version>
</dependency>
```

```yaml
qof:
  persistent-enable: true
```

## 相关模块

- 实现：[`qof-persistence-mybatis`](../qof-persistence-mybatis/README.md)

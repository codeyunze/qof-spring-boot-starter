# qof-persistence-mybatis

可选元数据持久化实现：基于 MyBatis-Plus 将文件信息写入数据库。

## 作用

- 提供落库版 `QofExtService` / 元数据仓储，覆盖 core 中的 `NoopQofExtService`
- 支持按 `fileId` 查询后下载、删除等依赖元数据的能力
- 与存储实现解耦：可只持久化、存储仍用 local/cos/oss/s3

## 何时引入

推荐：`qof-spring-boot-starter-persistence` 或随 `qof-spring-boot-starter-web` 引入。  
未引入时：上传可不入库，但**无法**依赖组件内置「按 fileId 查库再下载」。

## 配置要点

```yaml
qof:
  persistent-enable: true
```

还需宿主提供可用的数据源（本模块引入 MyBatis-Plus 相关依赖，数据源由 Spring Boot 常规方式配置）。

## 注意

- 不要与业务工程的宽 `@MapperScan` 冲突；按本模块自动配置边界装配
- 密钥、库连接串走环境变量 / 配置中心，勿硬编码

# qof-spring-boot-autoconfigure

Spring Boot 自动配置聚合模块：供 Starter 引用，承载与 Boot 集成相关的依赖与配置元数据能力。

## 作用

- 作为标准 Starter 结构中的 `autoconfigure` 角色
- 依赖 `qof-core` 与 `spring-boot-autoconfigure`
- 可选引入 `configuration-processor`，便于生成配置提示元数据

## 何时引入

**不要单独依赖**。由 `qof-spring-boot-starter` 等 Starter 传递引入。

## 说明

各存储 / Web / 持久化模块自身通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册自动配置；本模块负责 Starter 侧的 Boot 集成依赖边界，避免业务工程直接拼装细碎依赖。

## 依赖方向

```text
starter-* → qof-spring-boot-autoconfigure → qof-core → qof-spi
```

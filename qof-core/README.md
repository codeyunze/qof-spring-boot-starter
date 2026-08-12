# qof-core

核心门面与工厂：上传/下载/删除等统一能力。**不含**云厂商 SDK、**不含**数据库持久化。

## 作用

- 提供 `QofClient` / `QofClientFactory` 业务调用入口
- 按 `ObjectStorageProvider.mode()` 显式注册存储实现（不再按类名猜测）
- 默认提供 `NoopQofExtService`：未引入 persistence 时不落库、不按 fileId 查元数据

## 主要内容

- `DefaultQofClientFactory`：收集全部 `ObjectStorageProvider` Bean
- `AbstractQofClient` 与校验、通用工具
- `QofConfiguration`：核心自动配置（无宽 `ComponentScan`）
- 无持久化时的 Noop 扩展实现

## 何时引入

推荐通过 `qof-spring-boot-starter` 引入。仅做 SDK 嵌入、自行装配 Bean 时可直接依赖本模块，但仍需搭配至少一个 `qof-storage-*`。

## 注意

- 只用 local：引入默认 Starter 即可
- 需要 COS/OSS/S3 或落库：额外引入对应 `starter-*`，不要期望本模块内嵌云 SDK

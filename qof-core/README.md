# qof-core

核心门面与工厂：上传/下载/删除等统一能力，并包含 SPI 契约。**不含**云厂商 SDK、**不含**数据库持久化。

## 作用

- 提供 `QofClient` / `QofClientFactory` 业务调用入口
- 按 `ObjectStorageProvider.mode()` 显式注册存储实现
- 提供元数据 / 生命周期等 SPI 接口

## 何时引入

一般不必单独引入：依赖任一 `qof-storage-*` 会传递本模块。  
若只写扩展实现，可直接依赖本模块，但仍需搭配至少一个存储实现与元数据仓储。

## 17.3.2 — 2026-09-21

17.3.0 仅作为 SNAPSHOT 演进，未单独发版；本版本为 17.3.x 首个正式发布。

### Added
- `qof-persistence-mysql`：MySQL 元数据（实现 + 自动配置，替代 `qof-persistence-mybatis` / `qof-spring-boot-starter-persistence`）
- `qof-persistence-mongo`：MongoDB 元数据（集合 `sys_files`，启动自动建索引）
- S3 兼容存储通用化：RustFS 实现收敛为 `qof-storage-s3` / `qof.s3` / `mode=s3`（RustFS / MinIO 等）
- MySQL 侧接入 MyBatis-Plus 分页插件（`mybatis-plus-jsqlparser`）
- Web 层上传体积超限处理（`QofMultipartExceptionHandle`，HTTP 413）
- 元数据时间字段统一 `yyyy-MM-dd HH:mm:ss` 序列化

### Changed
- SPI 契约并入 `qof-core`，包路径调整为 `provider` / `metadata` / `lifecycle`（不再独立 `qof-spi`）
- 存储模块自带自动配置，业务直接依赖 `qof-storage-local` / `cos` / `oss` / `s3`，不再经过聚合 Starter
- `qof-spring-boot-starter-web` 不传递 persistence；本版本起传递 `qof-storage-local`，便于本地盘开箱
- 演示工程默认 **Local + MySQL + Web**；Mongo 依赖与配置以注释示例保留
- 发布插件升级：`maven-gpg-plugin` 3.2.8、`central-publishing-maven-plugin` 0.11.0
- 版本 **17.3.2**

### Removed
- 独立模块 `qof-spi`、`qof-spring-boot-starter`、`qof-spring-boot-starter-cos` / `oss` / `s3` / `persistence`
- 持久化提供者互斥检测（`PersistenceProviderMarker`）
- RustFS 专用类名与 `RUSTFS` 存储模式（统一为 S3）

## 17.2.0 — 2026-08-11

### Added
- `qof-storage-local` / `qof-storage-cos` / `qof-storage-oss` / `qof-storage-s3`
- `qof-persistence-mybatis`
- `qof-spring-boot-starter-web` / `starter-cos` / `starter-oss` / `starter-s3` / `starter-persistence`
- core 内 `NoopQofExtService`（无持久化时的默认扩展）

### Changed
- `qof-core` 移除云 SDK、MySQL、MyBatis、Druid 依赖
- 默认 `qof-spring-boot-starter` 仅聚合 core + local
- 版本 **17.2.0**

## 17.1.0 — 2026-08-11

### Added
- `qof-spi`：`ObjectStorageProvider` / `FileLifecycleListener` / `FileMetadataRepository` SPI
- `qof-spring-boot-autoconfigure`：配置元数据
- `qof-spring-boot-starter`：面向第三方的真正 Starter
- `qof-examples`：演示工程（原 `qof-starter`）
- `qof.web.enabled` / `qof.web.base-path` / `qof.web.expose-advice`
- RustFS 写入 `AutoConfiguration.imports`

### Changed
- 父工程坐标：`qof-spring-boot-starter` → `qof-parent`
- `DefaultQofClientFactory` 按 Provider `mode()` 注册，不再类名猜测
- 核心扫描收窄；Advice 默认不注册且限定 controller 包
- 内置 FileController 默认关闭

### Deprecated
- 业务依赖 `qof-starter`（请改用 `qof-spring-boot-starter` / `qof-examples`）

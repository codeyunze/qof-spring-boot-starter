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
- `qof-api`：`ObjectStorageProvider` / `FileLifecycleListener` / `FileMetadataRepository` SPI
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

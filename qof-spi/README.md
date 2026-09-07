# qof-spi

扩展 SPI 契约层：对象存储 / 元数据 / 生命周期钩子等稳定接口。**零重依赖**（不引入云 SDK / MyBatis / Spring Web）。

## 作用

- 定义第三方与实现模块共同遵守的扩展边界
- 作为实现模块的依赖根基：`storage-*` / `persistence-*` / `core` 均可依赖本模块
- 变更需谨慎：语义变更影响兼容性

## 主要内容

| 类型 | 说明 |
|---|---|
| `ObjectStorageProvider` | 对象存储 SPI：`mode()` + `getClient()` |
| `FileMetadataRepository` | 元数据读写 SPI |
| `FileLifecycleListener` | 文件生命周期钩子 |

## 何时引入

一般**不要单独引入**；随 `qof-core` 或各 Starter 传递依赖即可。仅在编写自定义 SPI 实现、且不想依赖 core 实现细节时直接依赖本模块。

## 依赖方向

```text
其他模块 → qof-spi（本模块不依赖业务实现）
```

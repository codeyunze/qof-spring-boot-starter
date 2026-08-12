# qof-examples

演示 / 样例工程：展示如何组合 Starter 与配置跑通上传下载。

## 作用

- 本地验证 QOF 各 Starter 组合
- 给接入方参考依赖与 `application.yaml` 写法

## 重要约束

**禁止被业务工程依赖。**  
本模块带可运行 `main`，不是可发布的库坐标；业务侧请依赖 `qof-spring-boot-starter` 及按需的 `starter-*`。

## 与历史命名

原 `qof-starter` 易被误当成正式 Starter，已更正为 `qof-examples`。真正的默认 Starter 是：

```text
qof-spring-boot-starter
```

## 相关文档

- 根目录 [README.md](../README.md)
- [架构优化方案](../docs/架构优化方案.md)

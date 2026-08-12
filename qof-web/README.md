# qof-web

可选 HTTP 适配层：内置文件上传/下载等 Controller，默认**不抢占**宿主路由。

## 作用

- 提供 `FileController` 等 HTTP 入口（可配置 base-path）
- 通过 `qof.web.enabled` 显式开关（默认 `false`）
- `expose-advice` 控制是否暴露组件侧异常处理（默认关闭，避免污染宿主）

## 主要内容

- `FileController`：基于 `QofClientFactory` 的文件接口
- `QofWebProperties`：`enabled` / `base-path` / `expose-advice`
- 限定包范围的自动配置，无宽扫描

## 何时引入

推荐经 `qof-spring-boot-starter-web` 引入。宿主已有自建 Controller 时**不必**引入本模块。

## 配置示例

```yaml
qof:
  web:
    enabled: true
    # base-path: /qof/file
    expose-advice: false
```

## 注意

- 鉴权、统一 `Result` 模型由宿主决定，本模块不强制绑定 Sa-Token / Security
- 生产环境建议自行叠加鉴权过滤器后再打开 `enabled`

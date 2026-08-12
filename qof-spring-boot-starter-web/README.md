# qof-spring-boot-starter-web

Web Starter：引入内置 HTTP 能力，并附带元数据持久化实现。

## 作用

- 依赖 `qof-web` + `qof-persistence-mybatis`
- 打开后可使用组件内置文件 Controller（仍需 `qof.web.enabled=true`）

## 何时引入

需要组件自带上传/下载 HTTP 接口，且接受落库元数据时引入。  
若宿主已有 Controller，通常**只引** `qof-spring-boot-starter-persistence` 即可，不必引本 Starter。

## 依赖示例

```xml
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter</artifactId>
  <version>17.2.0</version>
</dependency>
<dependency>
  <groupId>io.github.codeyunze</groupId>
  <artifactId>qof-spring-boot-starter-web</artifactId>
  <version>17.2.0</version>
</dependency>
```

```yaml
qof:
  persistent-enable: true
  web:
    enabled: true
    expose-advice: false
```

## 注意

- 默认不启用 Controller，避免与网关 `/file` 等路由冲突
- 鉴权由宿主叠加

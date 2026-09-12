package io.github.codeyunze.examples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * QOF 演示启动类。
 * <p>
 * 包名使用 {@code io.github.codeyunze.examples}，避免与组件库根包冲突。
 *
 * @author 高晗
 * @since 2024/12/2 23:53
 */
@SpringBootApplication
public class QofApplication {

    public static void main(String[] args) {
        SpringApplication.run(QofApplication.class, args);
    }
}

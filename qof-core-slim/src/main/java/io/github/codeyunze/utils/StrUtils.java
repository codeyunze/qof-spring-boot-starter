package io.github.codeyunze.utils;

import org.springframework.util.StringUtils;

/**
 * 字符串工具
 *
 * @author 高晗
 * @since 2025/2/21 11:43
 */
public class StrUtils {

    /**
     * 字符串首字母转成大写
     *
     * @param str 需要转化的字符
     * @return 首字母转为大写后的字符串
     */
    public static String toUpperCase(String str) {
        if (!StringUtils.hasText(str)) {
            throw new IllegalArgumentException("转换字符串不能为空");
        }
        char firstChar = str.charAt(0);
        if (97 <= firstChar && firstChar <= 122) {
            firstChar ^= 32;
        }
        return firstChar + str.substring(1);
    }
}

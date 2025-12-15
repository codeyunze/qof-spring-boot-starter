package io.github.codeyunze.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件类型检测工具类
 * 通过Magic Number（文件头）检测文件真实类型，防止文件伪装
 *
 * @author 高晗
 * @since 2025/2/25
 */
public class FileTypeDetector {

    private static final Logger log = LoggerFactory.getLogger(FileTypeDetector.class);

    /**
     * 常见文件类型的Magic Number映射
     * Key: MIME类型, Value: 文件头字节数组
     */
    private static final Map<String, byte[][]> MAGIC_NUMBERS = new HashMap<>();

    static {
        // 图片类型
        MAGIC_NUMBERS.put("image/jpeg", new byte[][]{
                {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}
        });
        MAGIC_NUMBERS.put("image/png", new byte[][]{
                {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A}
        });
        MAGIC_NUMBERS.put("image/gif", new byte[][]{
                {0x47, 0x49, 0x46, 0x38, 0x37, 0x61}, // GIF87a
                {0x47, 0x49, 0x46, 0x38, 0x39, 0x61}  // GIF89a
        });
        MAGIC_NUMBERS.put("image/bmp", new byte[][]{
                {0x42, 0x4D}
        });

        // PDF
        MAGIC_NUMBERS.put("application/pdf", new byte[][]{
                {0x25, 0x50, 0x44, 0x46} // %PDF
        });

        // Office文档
        MAGIC_NUMBERS.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", new byte[][]{
                {0x50, 0x4B, 0x03, 0x04, 0x14, 0x00, 0x06, 0x00} // ZIP header (docx is a zip)
        });
        MAGIC_NUMBERS.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[][]{
                {0x50, 0x4B, 0x03, 0x04, 0x14, 0x00, 0x06, 0x00} // ZIP header (xlsx is a zip)
        });
        MAGIC_NUMBERS.put("application/msword", new byte[][]{
                {(byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0, (byte) 0xA1, (byte) 0xB1, 0x1A, (byte) 0xE1} // OLE2 header
        });

        // ZIP压缩文件
        MAGIC_NUMBERS.put("application/zip", new byte[][]{
                {0x50, 0x4B, 0x03, 0x04}
        });

        // 视频类型
        MAGIC_NUMBERS.put("video/mp4", new byte[][]{
                {0x00, 0x00, 0x00, 0x18, 0x66, 0x74, 0x79, 0x70, 0x6D, 0x70, 0x34, 0x32}, // mp42
                {0x00, 0x00, 0x00, 0x20, 0x66, 0x74, 0x79, 0x70, 0x6D, 0x70, 0x34, 0x32}, // mp42
                {0x00, 0x00, 0x00, 0x1C, 0x66, 0x74, 0x79, 0x70} // ftyp
        });
    }

    /**
     * 检测文件真实类型
     *
     * @param inputStream 文件输入流
     * @param declaredType 声明的MIME类型（如ContentType）
     * @return 检测到的MIME类型，如果无法检测则返回null
     */
    public static String detectFileType(InputStream inputStream, String declaredType) {
        if (inputStream == null) {
            return null;
        }

        try {
            // 标记流位置，以便后续重置
            if (!inputStream.markSupported()) {
                log.warn("输入流不支持mark，无法进行Magic Number检测");
                return declaredType; // 如果不支持mark，返回声明的类型
            }

            // 读取文件头（最多读取前32字节）
            inputStream.mark(32);
            byte[] header = new byte[32];
            int bytesRead = inputStream.read(header);
            inputStream.reset();

            if (bytesRead < 2) {
                log.warn("文件头数据不足，无法进行Magic Number检测");
                return declaredType;
            }

            // 检查声明的类型是否匹配
            if (declaredType != null && verifyMagicNumber(header, bytesRead, declaredType)) {
                return declaredType;
            }

            // 如果声明的类型不匹配，尝试检测真实类型
            for (Map.Entry<String, byte[][]> entry : MAGIC_NUMBERS.entrySet()) {
                String mimeType = entry.getKey();
                byte[][] patterns = entry.getValue();
                for (byte[] pattern : patterns) {
                    if (matchesPattern(header, bytesRead, pattern)) {
                        if (declaredType != null && !declaredType.equals(mimeType)) {
                            log.warn("文件类型不匹配，声明类型: {}, 检测类型: {}", declaredType, mimeType);
                        }
                        return mimeType;
                    }
                }
            }

            log.debug("无法通过Magic Number检测文件类型，使用声明的类型: {}", declaredType);
            return declaredType;

        } catch (IOException e) {
            log.error("检测文件类型时发生异常", e);
            return declaredType;
        }
    }

    /**
     * 验证Magic Number是否匹配
     *
     * @param header 文件头字节数组
     * @param bytesRead 实际读取的字节数
     * @param mimeType MIME类型
     * @return 是否匹配
     */
    private static boolean verifyMagicNumber(byte[] header, int bytesRead, String mimeType) {
        byte[][] patterns = MAGIC_NUMBERS.get(mimeType);
        if (patterns == null) {
            return false; // 如果该类型没有Magic Number定义，无法验证
        }

        for (byte[] pattern : patterns) {
            if (matchesPattern(header, bytesRead, pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查文件头是否匹配模式
     *
     * @param header 文件头字节数组
     * @param bytesRead 实际读取的字节数
     * @param pattern 模式字节数组
     * @return 是否匹配
     */
    private static boolean matchesPattern(byte[] header, int bytesRead, byte[] pattern) {
        if (bytesRead < pattern.length) {
            return false;
        }

        for (int i = 0; i < pattern.length; i++) {
            if (header[i] != pattern[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证文件类型是否匹配
     *
     * @param inputStream 文件输入流
     * @param declaredType 声明的MIME类型
     * @return true表示类型匹配或无法验证，false表示类型不匹配
     */
    public static boolean validateFileType(InputStream inputStream, String declaredType) {
        if (declaredType == null || declaredType.trim().isEmpty()) {
            return true; // 如果没有声明类型，无法验证
        }

        String detectedType = detectFileType(inputStream, declaredType);
        if (detectedType == null) {
            return true; // 如果无法检测，允许通过（可能是未知类型）
        }

        return detectedType.equals(declaredType);
    }
}


package io.github.codeyunze.bo;


import java.io.InputStream;

/**
 * 下载文件信息
 *
 * @author 高晗
 * @since 2025/2/18 18:41
 */
public class QofFileDownloadBo extends QofFileInfoBo {

    /**
     * 文件输入流
     * <p>
     * <b>重要：</b>调用者负责关闭此InputStream，建议使用try-with-resources语句：
     * <pre>{@code
     * try (InputStream is = fileDownloadBo.getInputStream()) {
     *     // 使用输入流
     * }
     * }</pre>
     */
    private InputStream inputStream;

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}

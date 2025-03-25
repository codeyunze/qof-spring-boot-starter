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
     * 文件
     */
    private InputStream inputStream;

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}

package io.github.codeyunze.utils;


import java.io.Serializable;

/**
 * 后端返回给前端的数据对象
 *
 * @author yunze
 * @since 2022/11/13 23:23
 * @version 1.0
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 返回状态编码
     * 200: 正常
     * 1: 参数校验不通过
     * 2: 数据不存在
     */
    private int code;

    /**
     * 提示信息
     */
    private String msg;

    /**
     * 返回数据
     */
    private T data;

    public Result() {
    }

    public Result(int code, T data, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

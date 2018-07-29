package com.cqu.wb.result;

/**
 * Created by jingquan on 2018/7/24.
 */
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    /**
     *
     * @param data
     * @param <T>
     * @return
     * @description 成功时调用封装Result
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(data);
    }

    /**
     *
     * @param codeMessage
     * @param <T>
     * @return
     * @description 失败时调用封装Result
     */
    public static <T> Result<T> error(CodeMessage codeMessage) {
        return new Result<T>(codeMessage);
    }

    private Result(T data) {
        this.data = data;
    }
    private Result(CodeMessage codeMessage) {
        if(codeMessage != null) {
            this.code = codeMessage.getCode();
            this.msg = codeMessage.getMsg();
        }
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

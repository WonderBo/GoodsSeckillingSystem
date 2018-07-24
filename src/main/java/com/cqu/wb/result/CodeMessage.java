package com.cqu.wb.result;

/**
 * Created by jingquan on 2018/7/24.
 */
public class CodeMessage {
    private int code;
    private String msg;

    // 通用错误码   5001XX
    public static final CodeMessage SERVER_ERROR = new CodeMessage(500100, "服务端异常");
    // 登录模块错误码   5002XX

    // 商品模块错误码   5003XX

    // 订单模块错误码   5004XX

    // 秒杀模块错误码   5005XX

    private CodeMessage(int code, String msg) {
        this.code = code;
        this.msg = msg;
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
}

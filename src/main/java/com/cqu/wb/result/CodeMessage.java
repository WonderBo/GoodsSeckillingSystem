package com.cqu.wb.result;

/**
 * Created by jingquan on 2018/7/24.
 */
public class CodeMessage {
    private int code;
    private String msg;

    public static final CodeMessage SUCCESS = new CodeMessage(0, "成功");
    // 通用错误码   5001XX
    public static final CodeMessage SERVER_ERROR = new CodeMessage(500100, "服务端异常");
    public static final CodeMessage BIND_ERROR = new CodeMessage(500110, "参数校验异常：%s");
    public static final CodeMessage ILLEGAL_REQUEST = new CodeMessage(500111, "非法请求");
    public static final CodeMessage VERIFY_ERROR = new CodeMessage(500512, "验证码结果错误");
    // 登录模块错误码   5002XX
    public static final CodeMessage SESSION_ERROR = new CodeMessage(500210, "Session不存在或者已经失效");
    public static final CodeMessage PASSWORD_EMPTY = new CodeMessage(500211, "登录密码不能为空");
    public static final CodeMessage MOBILE_EMPTY = new CodeMessage(500212, "手机号不能为空");
    public static final CodeMessage MOBILE_ERROR = new CodeMessage(500213, "手机号格式错误");
    public static final CodeMessage MOBILE_NOT_EXIT = new CodeMessage(500214, "手机号不存在");
    public static final CodeMessage PASSWORD_ERROR = new CodeMessage(500215, "密码错误");
    // 商品模块错误码   5003XX

    // 订单模块错误码   5004XX
    public static final CodeMessage ORDER_NOT_EXIST = new CodeMessage(500410, "订单不存在");
    // 秒杀模块错误码   5005XX
    public static final CodeMessage SECKILL_OVER = new CodeMessage(500500, "商品已经秒杀完毕");
    public static final CodeMessage SECKILL_REPEATE = new CodeMessage(500510, "不能重复秒杀");
    public static final CodeMessage SECKILL_FAIL = new CodeMessage(500511, "秒杀失败");

    private CodeMessage(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     *
     * @param args
     * @return
     * @description 格式化带有格式占位符的消息字符串
     */
    public CodeMessage fillMessageArgs(Object... args) {
        int code = this.code;
        String message = String.format(this.msg, args);

        return new CodeMessage(code, message);
    }

    @Override
    public String toString() {
        return "CodeMsg [code=" + code + ", msg=" + msg + "]";
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

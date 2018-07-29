package com.cqu.wb.exception;

import com.cqu.wb.result.CodeMessage;

/**
 * Created by jingquan on 2018/7/29.
 */

/**
 * @description 全局异常（在Service层进行业务逻辑处理时，出现非期望结果时直接抛出异常交给全局异常处理器进行处理，保证方法返回结果符合情景）
 */
public class GlobalException extends RuntimeException {

    private CodeMessage codeMessage;

    public GlobalException(CodeMessage codeMessage) {
        super(codeMessage.toString());
        this.codeMessage = codeMessage;
    }

    public CodeMessage getCodeMessage() {
        return codeMessage;
    }
    public void setCodeMessage(CodeMessage codeMessage) {
        this.codeMessage = codeMessage;
    }
}

package com.cqu.wb.exception;

/**
 * Created by jingquan on 2018/7/29.
 */

import com.cqu.wb.result.CodeMessage;
import com.cqu.wb.result.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @description 全局异常处理器
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    /**
     *
     * @param request
     * @param e
     * @return
     * @description 处理Exception类型异常
     */
    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request, Exception e) {
        // 打印输出异常信息
        e.printStackTrace();

        if(e instanceof GlobalException) {      // 处理业务逻辑中的全局异常
            GlobalException globalException = (GlobalException) e;
            return Result.error(globalException.getCodeMessage());
        } else if(e instanceof BindException) { // 处理参数校验时发生的异常
            // 为便于处理相应异常，需要将异常强制转型
            BindException bindException = (BindException)e;
            // 获取绑定异常的错误对象（可能存在多个参数校验错误，则用List封装所有错误）
            List<ObjectError> objectErrorList =  bindException.getAllErrors();
            ObjectError objectError = objectErrorList.get(0);
            // 获取校验错误信息，并封装到Result进行返回
            String message = objectError.getDefaultMessage();
            return Result.error(CodeMessage.BIND_ERROR.fillMessageArgs(message));
        } else {                                // 处理其他类型异常
            return Result.error(CodeMessage.SERVER_ERROR);
        }
    }
}

package com.cqu.wb.validator;

import com.cqu.wb.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by jingquan on 2018/7/29.
 */

/**
 * @param IsMobile 注解
 * @param String 注解修饰字段的类型
 * @description 手机号码校验器
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    /**
     *
     * @param s 注解修饰的值
     * @param constraintValidatorContext
     * @return
     * @description 手机号码校验逻辑
     */
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(required) {
            return ValidatorUtil.isMobile(s);
        } else {
            if(StringUtils.isEmpty(s)) {
                return true;
            } else {
                return ValidatorUtil.isMobile(s);
            }
        }
    }

    /**
     *
     * @param constraintAnnotation 对应的注解
     * @description 初始化方法
     */
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }
}

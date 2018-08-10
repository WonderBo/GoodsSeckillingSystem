package com.cqu.wb.config;

import com.cqu.wb.access.AccessLimitInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * Created by jingquan on 2018/7/30.
 */

/**
 * @desription 自定义WebMvcConfigurer拦截器
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private UserArgumentResolver userArgumentResolver;

    @Autowired
    private AccessLimitInterceptor accessLimitInterceptor;

    /**
     *
     * @param argumentResolvers
     * @description 添加参数解析器
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLimitInterceptor);
    }
}

package com.wang.config;

import com.wang.interceptors.LoginInterceotor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceotor loginInterceotor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册拦截器
        //登录和注册接口不拦截
        registry.addInterceptor(loginInterceotor).excludePathPatterns("/user/register","/user/login");
    }
}

package com.example.demo.aspect;

/**
 * 方法拦截器顶层接口
 */
public interface GPMethodInterceptor {

    Object invoke(GPMethodInvocation mi) throws Throwable;
}

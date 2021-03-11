package com.example.demo.aspect;

import lombok.Data;

import java.lang.reflect.Method;

@Data
public class GPAfterThrowingAdvice implements GPMethodInterceptor {

    private String throwingName;

    public GPAfterThrowingAdvice(Method method, Object newInstance) {
    }

    @Override
    public Object invoke(GPMethodInvocation mi) throws Throwable {
        return null;
    }
}

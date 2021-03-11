package com.example.demo.aspect;

import java.lang.reflect.Method;

public class GPMethodBeforeAdvice extends GPAbstractAspectJAdvice implements GPMethodInterceptor, GPAdvice {

    private GPJoinPoint joinPoint;

    public GPMethodBeforeAdvice(Method aspectMethod, Object target) {
        super(aspectMethod, target);
    }

    private void before(Method method, Object[] args, Object target) throws Throwable {
        invokeAdviceMethod(this.joinPoint, null, null);
    }

    @Override
    public Object invoke(GPMethodInvocation mi) throws Throwable {
        this.joinPoint = mi;
        this.before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}

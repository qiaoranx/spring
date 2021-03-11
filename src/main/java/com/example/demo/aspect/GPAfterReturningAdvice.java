package com.example.demo.aspect;

import java.lang.reflect.Method;

public class GPAfterReturningAdvice extends GPAbstractAspectJAdvice implements GPMethodInterceptor, GPAdvice {

    private GPJoinPoint joinPoint;

    public GPAfterReturningAdvice(Method method, Object target) {
        super(method, target);
    }

    @Override
    public Object invoke(GPMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object target) throws Throwable {
        invokeAdviceMethod(joinPoint, retVal, null);
    }
}

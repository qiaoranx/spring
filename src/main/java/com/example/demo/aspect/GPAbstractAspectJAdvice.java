package com.example.demo.aspect;

import java.lang.reflect.Method;

public abstract class GPAbstractAspectJAdvice implements GPAdvice {

    private Method aspectMethod;

    private Object aspectTarget;

    public GPAbstractAspectJAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    protected Object invokeAdviceMethod(GPJoinPoint joinPoint, Object returnValue, Throwable ex) throws Throwable {

        Class<?>[] parameterTypes = this.aspectMethod.getParameterTypes();
        if (null == parameterTypes || parameterTypes.length == 0) {
            return this.aspectMethod.invoke(aspectTarget);
        } else {
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
                if (parameterTypes[i] == GPJoinPoint.class) {
                    args[i] = joinPoint;
                } else if (parameterTypes[i] == Throwable.class) {
                    args[i] = ex;
                } else if (parameterTypes[i] == Object.class) {
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget, args);
        }
    }
}

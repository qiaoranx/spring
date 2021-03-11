package com.example.demo.aspect;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行拦截器链，相当于Spring中ReflectiveInvocation
 */
public class GPMethodInvocation implements GPJoinPoint {

    /**
     * 代理对象
     */
    private Object proxy;

    private Method method;

    private Object target;

    private Class<?> targetClass;

    private Object[] arguments;

    /**
     * 目标方法增强链
     */
    private List<Object> interceptorAndDynamicMethodMatchers;

    private Map<String, Object> userAttributes;

    private int currentInterceptorIndex = -1;

    public GPMethodInvocation(Object proxy, Object target, Method method, Object[] args,
                              Class targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.method = method;
        this.target = target;
        this.targetClass = targetClass;
        this.arguments = args;
        this.interceptorAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    public Object proceed() throws Throwable {
        //拦截器链size=0为空直接执行joinPoint本身
        if (this.currentInterceptorIndex == this.interceptorAndDynamicMethodMatchers.size() - 1) {
            return this.method.invoke(this.target, this.arguments);
        }
        Object interceptorOrInterceptionAdvice =
                this.interceptorAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);

        if (interceptorOrInterceptionAdvice instanceof GPMethodInterceptor) {
            GPMethodInterceptor mi = (GPMethodInterceptor) interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        } else {
            return proceed();
        }
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Object getThis() {
        return this;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        if (value != null) {
            if (this.userAttributes == null) {
                this.userAttributes = new HashMap<>();
            }
            this.userAttributes.put(key, value);
        } else {
            if (this.userAttributes != null) {
                this.userAttributes.remove(key);
            }
        }
    }

    @Override
    public Object getUserAttribute(String key) {

        return (this.userAttributes != null ? this.userAttributes.get(key) : null);
    }
}

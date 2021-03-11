package com.example.demo.aspect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class GPJdkDynamicAopProxy implements GPAopProxy, InvocationHandler {

    private GPAdevisedSupport config;

    public GPJdkDynamicAopProxy(GPAdevisedSupport config) {
        this.config = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.config.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        //参数的含义：目标类的类加载器，目标类的接口，实现InvocationHandler的GPJdkDynamicAopProxy代理类本身
        return Proxy.newProxyInstance(classLoader, this.config.getTargetClass().getInterfaces(), this);
    }

    /**
     * 执行代理的关键入口
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //得到目标方法的增强链
        List<Object> interceptorsAndDynamicMethodMatchers =
                config.getInterceptorsAndDynamicInterceptionAdvice(method, this.config.getTargetClass());

        GPMethodInvocation invocation = new GPMethodInvocation(proxy, this.config.getTarget(), method, args,
                this.config.getTargetClass(), interceptorsAndDynamicMethodMatchers);

        return invocation.proceed();
    }
}

package com.example.demo.aspect;

public interface GPAopProxy {
    /**
     * 获得代理对象
     *
     * @return
     */
    Object getProxy();

    /**
     * 通过自定义类加载器获得代理对象
     */
    Object getProxy(ClassLoader classLoader);

}

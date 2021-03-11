package com.example.demo.core;

/**
 * 封装创建后的对象实例，代理对象或者原生对象都由beanWrapper保存
 */
public class GPBeanWrapper {

    private Object wrappedInstance;

    private Class<?> wrappedClass;

    public GPBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    public Class<?> getWrappedClass() {
        return wrappedInstance.getClass();
    }

}

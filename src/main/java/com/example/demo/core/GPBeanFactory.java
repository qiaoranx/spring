package com.example.demo.core;

public interface GPBeanFactory {

    Object getBean(String beanName) throws Exception;

    Object getBean(Class<?> beanClass) throws Exception;
}

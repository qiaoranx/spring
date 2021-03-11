package com.example.demo.aspect;

import java.lang.reflect.Method;

/**
 * 定义一个切点的抽象，可以理解为某一个业务方法的附加信息，包括业务方法本身、实参列表和方法所属的实例对象
 */
public interface GPJoinPoint {
    /**
     * 业务方法本身
     * @return
     */
    Method getMethod();

    /**
     * 该方法实参列表
     * @return
     */
    Object[] getArguments();

    /**
     * 该方法实例对象
     */
    Object getThis();

    /**
     * 自定义属性
     */
    void setUserAttribute(String key,Object value);

    
    Object getUserAttribute(String key);

}

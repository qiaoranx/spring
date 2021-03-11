package com.example.demo.aspect;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 定义一个织入的切面逻辑，也就是针对目标对象增强的逻辑
 */
@Slf4j
public class LogAspect {

    public void before(GPJoinPoint joinPoint) {

        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(),
                System.currentTimeMillis());
        log.info("Invoker Before Method" + joinPoint.getThis() + Arrays.toString(joinPoint.getArguments()));
    }

    public void after(GPJoinPoint joinPoint) {
        long endTime = System.currentTimeMillis();
        System.out.println("use Time=" + (endTime - (Long)
                joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName())));
    }

    /*
    每一个回调的方法都有一个GPJoinPoint，GPMethodInvocation是GPJoinPoint的实现类，GPMethodInvocation是GPJdkDynamicAopProxy实例化的，
    即每个被代理对象的业务方法都会对应一个GPMethodInvocation实例
     */
}

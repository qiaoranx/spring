package com.example.demo.context;

/**
 * IOC容器顶层设计
 */
public abstract class GPAbstractApplicationContext {
    /**
     * protected只给子类重写
     * @throws Exception
     */
    protected void refresh() throws Exception{};
}

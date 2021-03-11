package com.example.demo.aspect;

import lombok.Data;

/**
 * 定义AOP的配置信息的封装对象，与properties文件中定义的属性一一对应
 */
@Data
public class GPAopConfig {
    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectThrowingName;
}

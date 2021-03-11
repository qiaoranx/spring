package com.example.demo.annotation;


import java.lang.annotation.*;

/**
 * @author yuanqiao
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GPRequestParam {
    String value() default "";
}

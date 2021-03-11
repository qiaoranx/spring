package com.example.demo.annotation;


import java.lang.annotation.*;

/**
 * @author yuanqiao
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GPAutowired {
    String value() default "";
}

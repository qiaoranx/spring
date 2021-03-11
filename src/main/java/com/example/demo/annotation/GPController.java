package com.example.demo.annotation;


import java.lang.annotation.*;

/**
 * @author yuanqiao
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GPController {

    String value() default "";
}

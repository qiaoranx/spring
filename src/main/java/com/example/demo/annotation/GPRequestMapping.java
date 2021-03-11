package com.example.demo.annotation;

import java.lang.annotation.*;

/**
 * @author yuanqiao
 */

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GPRequestMapping {

    String value() default "";
}

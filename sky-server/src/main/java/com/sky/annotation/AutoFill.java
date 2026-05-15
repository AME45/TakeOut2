package com.sky.annotation;


import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)   //指定这个注解只能用在方法上
@Retention(RetentionPolicy.RUNTIME)  //保留时间
public @interface AutoFill {

    OperationType value();
}

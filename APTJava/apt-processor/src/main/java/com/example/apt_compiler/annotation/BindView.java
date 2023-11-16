package com.example.apt_compiler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.CLASS) // 表示这个注解保留到编译期
@Target(ElementType.FIELD) // 表示注解范围为类成员（构造方法、方法、成员变量）
public @interface BindView {
    int value();
}
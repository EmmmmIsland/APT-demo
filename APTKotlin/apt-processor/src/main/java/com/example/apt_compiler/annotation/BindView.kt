package com.example.apt_compiler.annotation

@kotlin.annotation.Retention(AnnotationRetention.BINARY) // 表示这个注解保留到编译期
@Target(AnnotationTarget.FIELD) // 表示注解范围为类成员（构造方法、方法、成员变量）
annotation class BindView(val value: Int)
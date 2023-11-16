package com.example.apt_compiler.launcher

import com.example.apt_compiler.template.IBindHelper

object AutoBind {
    fun inject(target: Any) {
        val className = target.javaClass.canonicalName
        val helperName = "$className$\$Processor"
        try {
            val helper = Class.forName(helperName).getConstructor().newInstance() as IBindHelper
            helper.inject(target)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
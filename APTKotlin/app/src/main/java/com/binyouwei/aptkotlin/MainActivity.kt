package com.binyouwei.aptkotlin

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.apt_compiler.annotation.BindView
import com.example.apt_compiler.launcher.AutoBind

class MainActivity : AppCompatActivity() {

    @BindView(value = R.id.test_textview)
    var text: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AutoBind.inject(this)

        text?.text = "APT 测试"
    }
}
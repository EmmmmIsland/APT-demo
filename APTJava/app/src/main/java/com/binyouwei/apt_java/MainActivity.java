package com.binyouwei.apt_java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.apt_compiler.annotation.BindView;
import com.example.apt_compiler.launcher.AutoBind;

public class MainActivity extends AppCompatActivity {

    @BindView(value = R.id.test_textview)
    public TextView testTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AutoBind.getInstance().inject(this);

        testTextView.setText("APT 测试");

    }
}
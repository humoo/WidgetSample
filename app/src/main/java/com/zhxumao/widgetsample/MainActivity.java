package com.zhxumao.widgetsample;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zhxumao.widgetsample.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ZDampingNestedScrollView.setOnClickListener(v ->
                startActivity(new Intent(activity, ZDampingNestedScrollViewActivity.class))
        );
        binding.ZoomNestScrollView.setOnClickListener(v ->
                startActivity(new Intent(activity, ZoomNestScrollViewActivity.class))
        );

    }
}
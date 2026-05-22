package com.lenkeng.udpdemo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.lenkeng.udpdemo.R;
import com.lenkeng.udpdemo.databinding.ActivityAnimationBinding;
import com.lenkeng.udpdemo.inf.OnPercentChangeListener;

public class AnimationActivity extends AppCompatActivity {
private ActivityAnimationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAnimationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
        addListener();
    }
    private void initData(){
        binding.powerView.setPercent(20);
    }
    private void addListener(){
        binding.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.powerView.setPercent(50);
            }
        });
        binding.btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.powerView.setPercentImmediately(80);
            }
        });
        binding.powerView.setOnPercentChangeListener(new OnPercentChangeListener() {
            @Override
            public void onPercentChanged(int percent, boolean fromUser) {
                LogUtils.d("Battery", "百分比变化: " + percent + ", 来自用户拖拽: " + fromUser);
            }

            @Override
            public void onDragStart() {
                LogUtils.d("Battery", "开始拖拽");
            }

            @Override
            public void onDragEnd(int finalPercent) {
                LogUtils.d("Battery", "结束拖拽，最终电量: " + finalPercent);
            }
        });
    }
}
package com.lenkeng.udpdemo.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.zxing.Result;
import com.king.camera.scan.AnalyzeResult;
import com.king.camera.scan.CameraScan;
import com.king.camera.scan.analyze.Analyzer;
import com.king.zxing.BarcodeCameraScanActivity;
import com.king.zxing.DecodeConfig;
import com.king.zxing.DecodeFormatManager;
import com.king.zxing.analyze.MultiFormatAnalyzer;
import com.lenkeng.udpdemo.R;
import com.lenkeng.udpdemo.databinding.ActivityScannerCodeBinding;
import com.lenkeng.udpdemo.utils.QulickListener;

public class ScannerCodeActivity extends BarcodeCameraScanActivity {
    private Button back_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        addListener();
    }

    @Nullable
    @Override
    public Analyzer<Result> createAnalyzer() {
        //初始化解码配置
        DecodeConfig decodeConfig = new DecodeConfig();
        decodeConfig.setHints(DecodeFormatManager.QR_CODE_HINTS)//如果只有识别二维码的需求，这样设置效率会更高，不设置默认为DecodeFormatManager.DEFAULT_HINTS
                .setFullAreaScan(false)//设置是否全区域识别，默认false
                .setAreaRectRatio(0.8f)//设置识别区域比例，默认0.8，设置的比例最终会在预览区域裁剪基于此比例的一个矩形进行扫码识别
                .setAreaRectVerticalOffset(0)//设置识别区域垂直方向偏移量，默认为0，为0表示居中，可以为负数
                .setAreaRectHorizontalOffset(0);//设置识别区域水平方向偏移量，默认为0，为0表示居中，可以为负数
        return new MultiFormatAnalyzer(decodeConfig);
    }

    @Override
    public void initCameraScan(@NonNull CameraScan<Result> cameraScan) {
        super.initCameraScan(cameraScan);
        cameraScan.setPlayBeep(true);
    }

    private void initView() {//初始化UI
        back_btn = findViewById(R.id.back_btn);
    }

    private void addListener() {
        back_btn.setOnClickListener(new QulickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                finish();
            }
        });
    }

    @Override
    public int getLayoutId() {//重写扫码界面
        return R.layout.activity_scanner_code;
    }

    @Override
    public void onScanResultCallback(@NonNull AnalyzeResult<Result> result) {//扫描回调
        // 停止分析
        getCameraScan().setAnalyzeImage(false);
        // 返回结果
        Intent intent = new Intent();
        intent.putExtra(CameraScan.SCAN_RESULT, result.getResult().getText());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
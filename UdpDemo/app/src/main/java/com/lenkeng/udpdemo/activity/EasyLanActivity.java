package com.lenkeng.udpdemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.ScrollView;

import com.lenkeng.udpdemo.R;
import com.lenkeng.udpdemo.databinding.ActivityEasyLanBinding;
import com.lenkeng.udpdemo.utils.NetworkingUtils;

public class EasyLanActivity extends AppCompatActivity {
  private ActivityEasyLanBinding binding;
    private NetworkingUtils networkingUtils;
    private SpannableStringBuilder logBuilder = new SpannableStringBuilder();

    // 日志类型常量
    private static final int LOG_INFO = 0;      // 普通信息 - 黑色
    private static final int LOG_SEND = 1;      // 发送消息 - 蓝色
    private static final int LOG_RECEIVE = 2;   // 接收消息 - 绿色
    private static final int LOG_ERROR = 3;     // 错误信息 - 红色

    // 当前广播地址和端口
    private String currentBroadcastAddress = "255.255.255.255";
    private int currentPort = 14445;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEasyLanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initUtils();
        setupListeners();
    }
    /**
     * 初始化工具类
     */
    private void initUtils() {
        networkingUtils = NetworkingUtils.getINSTANCE();
        networkingUtils.setOnNetWorkingCallBack(new NetworkingUtils.OnNetWorkingCallBack() {
            @Override
            public void sendMsg(String msg) {
                // 发送状态回调
                addLog(msg, LOG_SEND);
            }

            @Override
            public void receivedMsg(String reMsg) {
                // 接收到消息回调
                addLog("收到: " + reMsg, LOG_RECEIVE);
            }

            @Override
            public void onError(String errorMsg) {
                // 错误回调
                addLog("错误: " + errorMsg, LOG_ERROR);
            }
        });
    }
    /**
     * 设置监听器
     */
    private void setupListeners() {
        // 设置端口
        binding.setPortBtn.setOnClickListener(v -> {
            String portStr = binding.portInput.getText().toString();
            if (!portStr.isEmpty()) {
                try {
                    currentPort = Integer.parseInt(portStr);
                    networkingUtils.setUdpPort(currentPort);
                    addLog("端口已设置为: " + currentPort, LOG_INFO);
                } catch (NumberFormatException e) {
                    addLog("端口号格式错误", LOG_ERROR);
                }
            } else {
                addLog("请输入端口号", LOG_ERROR);
            }
        });

        // 设置广播地址
        binding.setBroadcastBtn.setOnClickListener(v -> {
            String address = binding.broadcastInput.getText().toString();
            if (!address.isEmpty()) {
                currentBroadcastAddress = address;
                addLog("广播地址已设置为: " + currentBroadcastAddress, LOG_INFO);
            } else {
                addLog("请输入广播地址", LOG_ERROR);
            }
        });

        // 设置为接收方
        binding.startReceiverBtn.setOnClickListener(v -> {
            try {
                // 先设置端口
                networkingUtils.setUdpPort(currentPort);
                // 开启接收（设置为可以接收消息）
                networkingUtils.openAndCloseMsg(true);
                // 开启UDP监听
                networkingUtils.openUdp();
                addLog("已设置为接收方，监听端口: " + currentPort, LOG_INFO);
            } catch (Exception e) {
                addLog("启动接收失败: " + e.getMessage(), LOG_ERROR);
            }
        });

        // 设置为发送方
        binding.startSenderBtn.setOnClickListener(v -> {
            try {
                // 设置为发送方时，可以选择是否关闭接收
                // 这里选择关闭接收，只作为发送端
                networkingUtils.openAndCloseMsg(false);
                addLog("已设置为发送方，目标广播地址: " + currentBroadcastAddress + ":" + currentPort, LOG_INFO);
            } catch (Exception e) {
                addLog("设置发送方失败: " + e.getMessage(), LOG_ERROR);
            }
        });

        // 发送消息
        binding.sendMsgBtn.setOnClickListener(v -> {
            String message = binding.messageInput.getText().toString();
            if (message.isEmpty()) {
                addLog("请输入要发送的消息", LOG_ERROR);
                return;
            }

            try {
                networkingUtils.sendMessage(message, currentBroadcastAddress);
                addLog("发送: " + message + " -> " + currentBroadcastAddress + ":" + currentPort, LOG_SEND);
            } catch (Exception e) {
                addLog("发送失败: " + e.getMessage(), LOG_ERROR);
            }
        });

        // 停止UDP
        binding.stopUdpBtn.setOnClickListener(v -> {
            networkingUtils.shutDownUdp();
            addLog("UDP已停止", LOG_INFO);
        });

        // 清除日志
        binding.clearLogBtn.setOnClickListener(v -> {
            clearLog();
        });
    }
    /**
     * 添加日志到界面
     * @param message 日志内容
     * @param type 日志类型
     */
    private void addLog(String message, int type) {
        runOnUiThread(() -> {
            String timestamp = new java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault())
                    .format(new java.util.Date());
            String logEntry = "[" + timestamp + "] " + message + "\n";

            SpannableString spannableLog = new SpannableString(logEntry);

            // 根据类型设置颜色
            int color;
            switch (type) {
                case LOG_SEND:
                    color = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
                    break;
                case LOG_RECEIVE:
                    color = ContextCompat.getColor(this, android.R.color.holo_green_dark);
                    break;
                case LOG_ERROR:
                    color = ContextCompat.getColor(this, android.R.color.holo_red_dark);
                    break;
                default:
                    color = ContextCompat.getColor(this, android.R.color.black);
                    break;
            }

            spannableLog.setSpan(new ForegroundColorSpan(color), 0, logEntry.length(), 0);
            logBuilder.append(spannableLog);

            // 限制日志长度，防止内存溢出
            if (logBuilder.length() > 20000) {
                CharSequence subSequence = logBuilder.subSequence(logBuilder.length() - 15000, logBuilder.length());
                logBuilder = new SpannableStringBuilder(subSequence);
            }

            binding.logTextView.setText(logBuilder);

            // 自动滚动到底部
            binding.logScrollView.post(() -> binding.logScrollView.fullScroll(ScrollView.FOCUS_DOWN));
        });
    }

    /**
     * 清除日志
     */
    private void clearLog() {
        logBuilder.clear();
        binding.logTextView.setText("");
        addLog("日志已清除", LOG_INFO);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放UDP资源
        networkingUtils.shutDownUdp();
    }
}
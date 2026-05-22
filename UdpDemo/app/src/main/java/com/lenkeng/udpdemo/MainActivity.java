package com.lenkeng.udpdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ScrollView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.google.gson.Gson;
import com.lenkeng.udpdemo.databinding.ActivityMainBinding;
import com.lenkeng.udpdemo.db.ADdBOpenHelper;
import com.lenkeng.udpdemo.utils.TestResCheckDemo;
import com.lenkeng.udpdemo.utils.UdpUtils;

import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    //    private StringBuilder logBuffer = new StringBuilder();
    private Gson gson = new Gson();
    private SpannableStringBuilder logBuffer = new SpannableStringBuilder();
    // 定义消息类型常量
    private static final int LOG_TYPE_DEFAULT = 0;  // 默认颜色
    private static final int LOG_TYPE_SUCCESS = 1;  // 绿色
    private static final int LOG_TYPE_ERROR = 2;    // 红色
    private Runnable resRunnable;
    private Handler resHandler = new Handler();//资源检查线程
    private TestResCheckDemo testResCheckDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        testResCheckDemo = TestResCheckDemo.getInstance(this);
        addListener();
    }

    private void addListener() {
        binding.settingSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
//                    //设置为发送方    记录此项操作
//                    String senderMsg = "设置为接收方" + "端口号: [" + binding.inputEdPortUdp.getText().toString() + "] 广播地址:[" + binding.targetAddressEd.getText().toString() + "]";
//                    UdpUtils.getInstance().setSendPort(Integer.parseInt(binding.inputEdPortUdp.getText().toString()));//只设置端口号
//                    logMessage(senderMsg, LOG_TYPE_DEFAULT);//显示启动记录
//                } catch (Exception e) {
//                    //异常
//                    logMessage("启动异常 :" + e.getMessage(), LOG_TYPE_ERROR);
//                }

            }
        });
        binding.settingRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
//                    //设置为接收方  记录此项操作
//                    String recipientMsg = "设置为接收方" + "接收端口号: [" + binding.inputEdPortUdp.getText().toString() + "] 接收广播地址:[" + binding.targetAddressEd.getText().toString() + "]";
//                    UdpUtils.getInstance().setPort(Integer.parseInt(binding.inputEdPortUdp.getText().toString()));//设置端口号并启动监听
//                    UdpUtils.getInstance().startListening();//启动监听
//                    logMessage(recipientMsg, LOG_TYPE_DEFAULT);
//                } catch (Exception e) {
//                    logMessage("设置接收异常:" + e.getMessage(), LOG_TYPE_ERROR);
//                }

            }
        });
        binding.clearLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //清除日志
                clearLog();
            }
        });
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
//                    logMessage("发送端口号:" + UdpUtils.getInstance().getPort(), LOG_TYPE_DEFAULT);
//                    //开启UDP发送端
//                    UdpUtils.getInstance().sendDiscoveryMessage(binding.inputEdMessage.getText().toString(), InetAddress.getByName(binding.targetAddressEd.getText().toString()));
//                } catch (Exception e) {
//                    //发送异常
//                    logMessage("发送异常:" + e.getMessage(), LOG_TYPE_ERROR);
//                }
            }
        });
        binding.restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //清除所有日志
                clearLog();
                //重置所有连接
//                UdpUtils.getInstance().stopListening();
            }
        });
        binding.openCheckRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //生成数据并插入数据
                testResCheckDemo.addFiveTestDataInsertDb();
                //填充数据
//                testResCheckDemo.addListFiveDataWithNewModel();
                //开启资源检查器
                startHandlerTask();
            }
        });
        binding.closeCheckRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //关闭资源检查器
              resHandler.removeCallbacks(resRunnable);
              LogUtils.d("关闭迭代器");
            }
        });
        binding.getGson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.d("编辑的整体字符串模型:"+testResCheckDemo.getResGsonString(gson));
                LogUtils.d("输出（中式星期几）:" + TimeUtils.getChineseWeek(TimeUtils.getNowMills()), "输出美式星期几:" + TimeUtils.getUSWeek(TimeUtils.getNowMills()));
            }
        });
//        UdpUtils.getInstance().setOnMessageReceivedListener(new UdpUtils.OnMessageReceivedListener() {
//            @Override
//            public void sendSta(String sendMsgSta) {
//                //发送状态
//                logMessage(sendMsgSta, LOG_TYPE_SUCCESS);
//            }
//
//            @Override
//            public void onMessageReceived(String message, InetAddress senderAddress, int senderPort) {
//                //开启监听后接收收到的消息
//                String msg = "接收到地址：" + senderAddress.getHostAddress() + " 端口号:" + senderPort + "  接收消息为:[" + message + "]";
//                logMessage(msg, LOG_TYPE_SUCCESS);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                //异常
//                logMessage("异常:" + e.getMessage(), LOG_TYPE_ERROR);
//            }
//        });
//        testResCheckDemo.setCallBack(new TestResCheckDemo.CallBack() {
//            @Override
//            public void onMsg(int type, String messages) {
//                logMessage(messages, type);
//            }
//        });
//        binding.updateData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                testResCheckDemo.clearAllDbData();//清除所有数据
//
//            }
//        });
    }

    private void startHandlerTask() {
        if (resRunnable != null) {//移除已经执行的任务 防止重复触发
            resHandler.removeCallbacks(resRunnable);
        }
        resRunnable = new Runnable() {
            @Override
            public void run() {
//                testResCheckDemo.checkCurrentResource();
                //发送组网申请udp

resHandler.postDelayed(resRunnable,5000);
            }
        };
        resHandler.post(resRunnable);
    }

    // 将logBuffer定义为SpannableStringBuilder类型
    private void logMessage(String messages, int logType) {
        runOnUiThread(() -> {
            // 创建带时间戳的日志消息
            String logEntry = " [" + TimeUtils.getNowString() + "] :" + messages + "\n";

            // 使用SpannableString来设置颜色
            SpannableString spannableLog = new SpannableString(logEntry);

            // 根据消息类型设置颜色
            switch (logType) {
                case LOG_TYPE_ERROR:
                    spannableLog.setSpan(new ForegroundColorSpan(Color.RED), 0, logEntry.length(), 0);
                    break;
                case LOG_TYPE_SUCCESS:
                    spannableLog.setSpan(new ForegroundColorSpan(Color.GREEN), 0, logEntry.length(), 0);
                    break;
                default:
                    // 默认颜色，不设置颜色Span
                    break;
            }

            // 添加到日志缓冲区
            logBuffer.append(spannableLog);

            // 限制日志长度，避免内存溢出
            if (logBuffer.length() > 50000) {
                CharSequence subSequence = logBuffer.subSequence(
                        logBuffer.length() - 40000, logBuffer.length());
                logBuffer = new SpannableStringBuilder(subSequence);
            }

            // 直接设置SpannableStringBuilder到TextView
            binding.logTextView.setText(logBuffer);

            // 滚动到底部
            binding.scView.post(() -> binding.scView.fullScroll(ScrollView.FOCUS_DOWN));
        });
    }

    /**
     * 清除日志显示
     */
    private void clearLog() {
        runOnUiThread(() -> {
            logBuffer.clear(); // 清空StringBuilder内容
            binding.logTextView.setText(""); // 清空TextView显示
            logMessage("日志已清除", LOG_TYPE_DEFAULT); // 记录清除操作
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        UdpUtils.getInstance().stopListening();
    }
}
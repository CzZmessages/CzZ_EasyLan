package com.lenkeng.udpdemo.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.Observer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.king.camera.scan.CameraScan;
import com.lenkeng.udpdemo.R;
import com.lenkeng.udpdemo.bean.AddTerminalBean;
import com.lenkeng.udpdemo.bean.BrodCastBean;
import com.lenkeng.udpdemo.bean.DataProcessBean;
import com.lenkeng.udpdemo.bean.DevicesBean;
import com.lenkeng.udpdemo.bean.InconimgMessage;
import com.lenkeng.udpdemo.databinding.ActivityManagerTerminalBinding;
import com.lenkeng.udpdemo.tcp.LanNetWorkUtils;
import com.lenkeng.udpdemo.tcp.TcpServerForIncomingConnectionsManager;
import com.lenkeng.udpdemo.utils.Constant;
import com.lenkeng.udpdemo.utils.QulickListener;
import com.lenkeng.udpdemo.viewmodel.TcpConnectionViewModel;

import java.util.ArrayList;
import java.util.List;

public class ManagerTerminalActivity extends AppCompatActivity {
    private ActivityManagerTerminalBinding binding;
    private ActivityResultLauncher<Intent> scanLauncher;
    private TcpConnectionViewModel viewModel;
    private LanNetWorkUtils lanNetWorkUtils;
    private TcpServerForIncomingConnectionsManager tcpSever;
    private Gson gson;
    // ========== 异常模拟开关（测试用，上线前改为false）==========
    private static final boolean SIMULATE_EXCEPTION = false;  // 总开关
    // 选择要模拟的异常场景（每次只改这一个数字）
    // 0 = 全部正常  1 = UDP超时  2 = 色块失败  3 = 功能失败  4 = 联网失败
    private static final int EXCEPTION_SCENE = 0;

    // 测试状态机
    private enum TestState {
        IDLE,           // 空闲
        SCANNED,        // 已扫码，等待连接
        CONNECTED,      // 已连接
        TESTING,        // 测试中
        ALL_PASS,       // 全部通过
        FAILED          // 失败
    }

    private TestState currentState = TestState.IDLE;

    // 存储当前设备信息
    private String currentDeviceSn = "";
    private String currentDeviceType = "";

    // 超时处理
    private Handler timeoutHandler = new Handler();
    private Runnable timeoutRunnable;
    private String ip;
    private String id;
    // 测试进度（模拟用）
    private int testStep = 0;
    private final int TOTAL_STEPS = 4;  // UDP测试、色块测试、功能测试、联网测试

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManagerTerminalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
        addListener();
    }

    private void initData() {
        gson = new Gson();

        // 按钮监听
        binding.btnScan.setOnClickListener(quickListener);
        binding.btnFullTest.setOnClickListener(quickListener);
        binding.btnAddHub.setOnClickListener(quickListener);
        binding.btnReportIssue.setOnClickListener(quickListener);
        binding.btnRetry.setOnClickListener(quickListener);
        binding.btnLock.setOnClickListener(quickListener);

        try {
            lanNetWorkUtils = LanNetWorkUtils.getInstance(this);
            tcpSever = TcpServerForIncomingConnectionsManager.getInstance(Constant.TEST_PORT, this);
            tcpSever.startListening();
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewModel = TcpConnectionViewModel.getInstance(getApplicationContext());
    }

    private void addListener() {
        scanLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        String text = CameraScan.parseScanResult(data);
                        if (text != null && !text.isEmpty()) {
                            currentDeviceSn = text;
                            onDeviceScanned(text);
                        }
                    }
                }
        );

        // 监听viewModel回调（设备通过TCP返回的数据）
        viewModel.getNewMessageEvent().observe(this, new Observer<InconimgMessage>() {
            @Override
            public void onChanged(InconimgMessage inconimgMessage) {
                classFlyData(inconimgMessage);
            }
        });

        viewModel.getConnectionClosedEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                // 断开连接回调
                runOnUiThread(() -> {
                    binding.tvConnectStatus.setText("连接状态：已断开");
                    binding.tvConnectStatus.setTextColor(getColor(android.R.color.holo_red_dark));
                    binding.btnFullTest.setEnabled(false);
                    currentState = TestState.IDLE;
                });
            }
        });
    }

    /**
     * 设备扫码后的处理
     */
    private void onDeviceScanned(String sn) {
        id=sn;//赋值id号
        // 更新UI显示设备号
        binding.tvDeviceSn.setText("设备号：" + sn);
        binding.tvConnectStatus.setText("连接状态：正在连接...");
        binding.tvConnectStatus.setTextColor(getColor(android.R.color.holo_orange_dark));
        binding.tvCurrentTest.setText("正在连接设备...");
        currentState = TestState.SCANNED;

        try {
            // 发送UDP广播，请求设备信息
            List<AddTerminalBean> ids = new ArrayList<>();
            ids.add(new AddTerminalBean(sn, "", ""));
            String testMsg = gson.toJson(new BrodCastBean(Constant.TEST_ADMIN_PORT_CONNECT_MSG_UDP, ids));
            lanNetWorkUtils.sendUdpTerminalMsg(testMsg);
            ToastUtils.showLong("已发送连接请求: " + sn);

            // 设置超时（5秒）
            timeoutHandler.removeCallbacksAndMessages(null);
            timeoutRunnable = () -> {
                if (currentState == TestState.SCANNED) {
                    runOnUiThread(() -> {
                        binding.tvConnectStatus.setText("连接状态：连接超时");
                        binding.tvConnectStatus.setTextColor(getColor(android.R.color.holo_red_dark));
                        binding.tvCurrentTest.setText("连接超时，请检查设备");
                        ToastUtils.showLong("设备连接超时");
                        currentState = TestState.IDLE;
                    });
                }
            };
            timeoutHandler.postDelayed(timeoutRunnable, 5000);

        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showLong("发送连接请求失败");
        }
    }

    /**
     * 处理设备通过TCP返回的数据
     */
    private void classFlyData(InconimgMessage inconimgMessage) {
        try {
            if (inconimgMessage.getMessage() == null) {
                return;
            }
            DataProcessBean bean = gson.fromJson(inconimgMessage.getMessage(), DataProcessBean.class);

            switch (bean.getOperation_type()) {
                case Constant.TCP_RESULT_TERMINAL_MESSAGE:
                    // 设备信息回调
                    DevicesBean devicesBean = gson.fromJson(bean.getValue(), DevicesBean.class);
                    runOnUiThread(() -> {
                        onDeviceConnected(devicesBean);
                    });
                    break;

                case Constant.TCP_RESULT_TERMINAL_PING:
                    //赋值ip
                    ip = inconimgMessage.getIp();
                    // 响应设备Ping，保持连接
                    tcpSever.sendMessageTo(inconimgMessage.getIp(),
                            gson.toJson(new DataProcessBean(bean.getDevicesId(), Constant.TCP_PONG, "")));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设备连接成功后的处理
     */
    private void onDeviceConnected(DevicesBean devicesBean) {
        // 取消超时
        timeoutHandler.removeCallbacks(timeoutRunnable);

        // 更新设备信息
        currentDeviceType = devicesBean.getTerminal_type();
        String isNetType = devicesBean.isDevicesNetWorkStat() == 1 ? "已联网" : "未连接互联网";

        binding.tvDeviceType.setText("类型：" + currentDeviceType);
        binding.tvConnectStatus.setText("连接状态：已连接 ✓");
        binding.tvConnectStatus.setTextColor(getColor(android.R.color.holo_green_dark));
        binding.tvCurrentTest.setText("设备已连接，可开始测试");

        currentState = TestState.CONNECTED;

        // 启用全测按钮
        binding.btnFullTest.setEnabled(true);
        binding.btnFullTest.setBackgroundTintList(getColorStateList(android.R.color.holo_green_dark));

        ToastUtils.showLong("设备连接成功: " + currentDeviceType);
    }

    /**
     * 开始全测（一键执行所有测试项）
     */
    private void startFullTest() {
        if (currentState != TestState.CONNECTED && currentState != TestState.FAILED) {
            ToastUtils.showLong("请先扫码连接设备");
            return;
        }

        // 重置测试状态
        currentState = TestState.TESTING;
        testStep = 0;

        // 重置UI
        binding.btnFullTest.setEnabled(false);
        binding.btnAddHub.setEnabled(false);
        binding.btnReportIssue.setVisibility(View.GONE);
        binding.btnRetry.setVisibility(View.GONE);
        binding.tvFinalResult.setVisibility(View.GONE);
        binding.testProgress.setProgress(0);
        binding.tvCurrentTest.setTextColor(getColor(android.R.color.black));

        // 开始第一步测试
        performNextTest();
    }

    /**
     * 执行下一步测试
     */
    private void performNextTest() {
        if (currentState != TestState.TESTING) {
            return;
        }

        testStep++;
        int progress = (testStep * 100) / TOTAL_STEPS;
        binding.testProgress.setProgress(progress);

        switch (testStep) {
            case 1:
                testTCPCommunication();
                break;
            case 2:
                testColorBlockDisplay();
                break;
            case 3:
                testFunctionality();
                break;
            case 4:
                testNetworkConnection();
                break;
            default:
                // 所有测试完成
                onAllTestsPassed();
                break;
        }
    }

    /**
     * 测试1：UDP通讯测试
     */
    private void testTCPCommunication() {
        binding.tvCurrentTest.setText("正在测试 TCP 通讯...");

//        // 异常模拟：UDP超时
//        if (SIMULATE_EXCEPTION && EXCEPTION_SCENE == 1) {
//            ToastUtils.showLong("[模拟] TCP通讯异常");
//            timeoutHandler.postDelayed(() -> {
//                if (currentState == TestState.TESTING && testStep == 1) {
//                    onTestFailed("TCP通讯无响应");
//                }
//            }, 5000);
//            return;
//        }

        // TODO: 替换为实际的UDP测试指令
        ToastUtils.showLong("TCP通讯测试中...");
        if (ip != null && !ip.trim().isEmpty()) {
            if (currentState == TestState.TESTING && testStep == 1) {
                onTestFailed("TCP通讯无响应");
            }
            return;
        }
        tcpSever.sendMessageTo(ip,gson.toJson(new DataProcessBean(id, Constant.TCP_TEST_PP, "")));
//        timeoutHandler.postDelayed(() -> {
//            if (currentState == TestState.TESTING && testStep == 1) {
//                runOnUiThread(() -> {
//                    binding.tvCurrentTest.setText("✓ TCP通讯测试通过");
//                    performNextTest();
//                });
//            }
//        }, 1500);
//
//        timeoutHandler.postDelayed(() -> {
//            if (currentState == TestState.TESTING && testStep == 1) {
//                onTestFailed("TCP通讯超时");
//            }
//        }, 5000);
    }

    /**
     * 测试2：色块素材显示测试
     */
    private void testColorBlockDisplay() {
        binding.tvCurrentTest.setText("正在发送色块素材验证显示...");

        // 异常模拟：色块显示失败
        if (SIMULATE_EXCEPTION && EXCEPTION_SCENE == 2) {
            ToastUtils.showLong("[模拟] 色块显示异常");
            timeoutHandler.postDelayed(() -> {
                if (currentState == TestState.TESTING && testStep == 2) {
                    onTestFailed("色块素材发送失败");
                }
            }, 2000);
            return;
        }

        // TODO: 替换为实际的色块素材发送指令
        ToastUtils.showLong("色块素材发送中...");

        timeoutHandler.postDelayed(() -> {
            if (currentState == TestState.TESTING && testStep == 2) {
                runOnUiThread(() -> {
                    binding.tvCurrentTest.setText("✓ 色块显示测试通过");
                    performNextTest();
                });
            }
        }, 1500);

        timeoutHandler.postDelayed(() -> {
            if (currentState == TestState.TESTING && testStep == 2) {
                onTestFailed("色块显示超时");
            }
        }, 5000);
    }

    /**
     * 测试3：功能测试
     */
    private void testFunctionality() {
        binding.tvCurrentTest.setText("正在测试设备功能...");

        // 异常模拟：功能测试失败
        if (SIMULATE_EXCEPTION && EXCEPTION_SCENE == 3) {
            ToastUtils.showLong("[模拟] 功能测试异常");
            timeoutHandler.postDelayed(() -> {
                if (currentState == TestState.TESTING && testStep == 3) {
                    onTestFailed("功能测试不通过");
                }
            }, 2000);
            return;
        }

        // TODO: 替换为实际的功能测试指令
        ToastUtils.showLong("功能测试中...");

        timeoutHandler.postDelayed(() -> {
            if (currentState == TestState.TESTING && testStep == 3) {
                runOnUiThread(() -> {
                    binding.tvCurrentTest.setText("✓ 功能测试通过");
                    performNextTest();
                });
            }
        }, 1500);

        timeoutHandler.postDelayed(() -> {
            if (currentState == TestState.TESTING && testStep == 3) {
                onTestFailed("功能测试超时");
            }
        }, 5000);
    }

    /**
     * 测试4：联网测试
     */
    private void testNetworkConnection() {
        binding.tvCurrentTest.setText("正在测试网络连接...");

        // 异常模拟：联网失败
        if (SIMULATE_EXCEPTION && EXCEPTION_SCENE == 4) {
            ToastUtils.showLong("[模拟] 联网测试异常");
            timeoutHandler.postDelayed(() -> {
                if (currentState == TestState.TESTING && testStep == 4) {
                    onTestFailed("网络连接失败");
                }
            }, 2000);
            return;
        }

        // TODO: 替换为实际的联网测试指令
        ToastUtils.showLong("联网测试中...");

        timeoutHandler.postDelayed(() -> {
            if (currentState == TestState.TESTING && testStep == 4) {
                runOnUiThread(() -> {
                    binding.tvCurrentTest.setText("✓ 联网测试通过");
                    performNextTest();
                });
            }
        }, 1500);

        timeoutHandler.postDelayed(() -> {
            if (currentState == TestState.TESTING && testStep == 4) {
                onTestFailed("联网测试超时");
            }
        }, 5000);
    }

    /**
     * 所有测试通过
     */
    private void onAllTestsPassed() {
        currentState = TestState.ALL_PASS;
        binding.testProgress.setProgress(100);
        binding.tvCurrentTest.setText("✅ 全部测试通过！");
        binding.tvCurrentTest.setTextColor(getColor(android.R.color.holo_green_dark));

        // 显示结果区域
        binding.tvFinalResult.setVisibility(View.VISIBLE);
        binding.tvFinalResult.setText("✓ 测试通过");
        binding.tvFinalResult.setBackgroundColor(getColor(android.R.color.holo_green_light));
        binding.tvFinalResult.setTextColor(getColor(android.R.color.white));

        // 启用入库按钮
        binding.btnAddHub.setEnabled(true);
        binding.btnFullTest.setEnabled(true);

        ToastUtils.showLong("所有测试通过，可以入库");
    }

    /**
     * 测试失败
     */
    private void onTestFailed(String reason) {
        currentState = TestState.FAILED;

        runOnUiThread(() -> {
            binding.tvCurrentTest.setText("❌ 测试失败：" + reason);
            binding.tvCurrentTest.setTextColor(getColor(android.R.color.holo_red_dark));

            // 显示结果区域
            binding.tvFinalResult.setVisibility(View.VISIBLE);
            binding.tvFinalResult.setText("✗ 测试失败\n" + reason);
            binding.tvFinalResult.setBackgroundColor(getColor(android.R.color.holo_red_light));
            binding.tvFinalResult.setTextColor(getColor(android.R.color.white));

            // 显示重试和上报按钮
            binding.btnReportIssue.setVisibility(View.VISIBLE);
            binding.btnRetry.setVisibility(View.VISIBLE);
            binding.btnFullTest.setEnabled(true);

            // 失败时也显示入库按钮不可用状态
            binding.btnAddHub.setEnabled(false);

            ToastUtils.showLong("测试失败：" + reason);
        });
    }

    /**
     * 入库操作
     */
    private void doStorage() {
        if (currentState != TestState.ALL_PASS) {
            ToastUtils.showLong("请先完成全部测试");
            return;
        }

        binding.btnAddHub.setEnabled(false);
        binding.tvCurrentTest.setText("正在入库...");

        // TODO: 替换为实际的入库接口调用
        timeoutHandler.postDelayed(() -> {
            runOnUiThread(() -> {
                ToastUtils.showLong("设备 " + currentDeviceSn + " 已入库 ✓");
                binding.tvCurrentTest.setText("入库完成，请扫码下一台设备");

                // 重置界面，准备下一台设备
                resetForNextDevice();
            });
        }, 1000);
    }

    /**
     * 上报问题
     */
    private void doReportIssue() {
        // TODO: 替换为实际上报接口
        ToastUtils.showLong("问题已上报，请等待维修");

        // 重置界面
        resetForNextDevice();
    }

    /**
     * 重试测试
     */
    private void doRetry() {
        // 重置到连接成功状态
        currentState = TestState.CONNECTED;
        binding.btnReportIssue.setVisibility(View.GONE);
        binding.btnRetry.setVisibility(View.GONE);
        binding.tvFinalResult.setVisibility(View.GONE);
        binding.testProgress.setProgress(0);
        binding.tvCurrentTest.setText("设备已连接，可开始测试");
        binding.tvCurrentTest.setTextColor(getColor(android.R.color.black));
        binding.btnFullTest.setEnabled(true);

        ToastUtils.showLong("请重新开始测试");
    }

    /**
     * 重置界面，准备测试下一台设备
     */
    private void resetForNextDevice() {
        //断开所有TCP连接
        tcpSever.disconnectAll();
        currentState = TestState.IDLE;
        currentDeviceSn = "";
        currentDeviceType = "";

        binding.tvDeviceSn.setText("设备号：--");
        binding.tvDeviceType.setText("类型：--");
        binding.tvConnectStatus.setText("连接状态：等待扫码");
        binding.tvConnectStatus.setTextColor(getColor(android.R.color.holo_orange_dark));
        binding.tvCurrentTest.setText("请先扫码设备");
        binding.tvCurrentTest.setTextColor(getColor(android.R.color.darker_gray));
        binding.tvFinalResult.setVisibility(View.GONE);
        binding.btnFullTest.setEnabled(false);
        binding.btnAddHub.setEnabled(false);
        binding.btnReportIssue.setVisibility(View.GONE);
        binding.btnRetry.setVisibility(View.GONE);
        binding.testProgress.setProgress(0);
    }

    /**
     * 退出/锁定
     */
    private void doLock() {
        // 简单退出提示（实际可能需要密码验证）
        ToastUtils.showLong("长按可退出程序");
        // TODO: 添加退出或锁屏逻辑
    }

    // 自定义防快速点击监听器
    QulickListener quickListener = new QulickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        protected void onNoDoubleClick(View v) {
            int id = v.getId();

            if (id == binding.btnScan.getId()) {
                //清空所有 并重置所有状态
                startScan(ScannerCodeActivity.class);

            } else if (id == binding.btnFullTest.getId()) {
                startFullTest();

            } else if (id == binding.btnAddHub.getId()) {
                doStorage();

            } else if (id == binding.btnReportIssue.getId()) {
                doReportIssue();

            } else if (id == binding.btnRetry.getId()) {
                doRetry();

            } else if (id == binding.btnLock.getId()) {
                doLock();
            }
        }
    };

    /**
     * 启动扫码界面
     */
    private void startScan(Class<?> cls) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.in, R.anim.on);
        Intent intent = new Intent(this, cls);
        scanLauncher.launch(intent, optionsCompat);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeoutHandler.removeCallbacksAndMessages(null);
    }
}
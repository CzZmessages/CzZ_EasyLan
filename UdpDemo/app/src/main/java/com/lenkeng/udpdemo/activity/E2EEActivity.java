package com.lenkeng.udpdemo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lenkeng.udpdemo.R;
import com.lenkeng.udpdemo.utils.AuthorizationUtils;

import java.security.SecureRandom;

public class E2EEActivity extends AppCompatActivity {
    private static final String TAG = "E2EEDemo";

    // UI组件
    private TextView tvTerminalFingerprint;
    private TextView tvAppStatus;
    private TextView tvEncrypted;
    private TextView tvForward;
    private TextView tvDecryptResult;
    private TextView tvExecuteResult;
    private TextView tvLog;

    // 模拟数据
    private String terminalKey;           // 终端持有的密钥（模拟硬件锁死）
    private String appKey;                // APP持有的密钥（配对后获得）
    private String encryptedCommand;      // 加密后的命令
    private String currentCommand;        // 当前发送的命令明文
    private StringBuilder logBuilder;     // 日志构建器
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e2_eeactivity);
        initViews();
        initListeners();
        resetAll();

        log("========================================");
        log("端到端加密模拟器已启动");
        log("========================================");
    }
    private void initViews() {
        tvTerminalFingerprint = findViewById(R.id.tv_terminal_fingerprint);
        tvAppStatus = findViewById(R.id.tv_app_status);
        tvEncrypted = findViewById(R.id.tv_encrypted);
        tvForward = findViewById(R.id.tv_forward);
        tvDecryptResult = findViewById(R.id.tv_decrypt_result);
        tvExecuteResult = findViewById(R.id.tv_execute_result);
        tvLog = findViewById(R.id.tv_log);
    }

    private void initListeners() {
        // 步骤1: 终端初始化
        findViewById(R.id.btn_step1).setOnClickListener(v -> step1_TerminalInit());

        // 步骤2: APP扫码配对
        findViewById(R.id.btn_step2).setOnClickListener(v -> step2_AppPair());

        // 步骤3: 发送命令
        findViewById(R.id.btn_cmd_on).setOnClickListener(v -> step3_AppSendCommand("开灯"));
        findViewById(R.id.btn_cmd_off).setOnClickListener(v -> step3_AppSendCommand("关灯"));
        findViewById(R.id.btn_cmd_bright).setOnClickListener(v -> step3_AppSendCommand("调亮"));

        // 步骤4: 服务器转发
        findViewById(R.id.btn_step4).setOnClickListener(v -> step4_ServerForward());

        // 步骤5: 终端验签执行
        findViewById(R.id.btn_step5).setOnClickListener(v -> step5_TerminalExecute());

        // 攻击模拟
        findViewById(R.id.btn_attack).setOnClickListener(v -> simulateAttack());

        // 重置
        findViewById(R.id.btn_reset).setOnClickListener(v -> resetAll());
    }

    /**
     * 步骤1: 终端初始化 - 生成唯一密钥
     */
    private void step1_TerminalInit() {
        log("\n[步骤1] 终端初始化开始...");

        // 生成32字节随机密钥（模拟硬件生成的唯一密钥）
        byte[] keyBytes = new byte[32];
        new SecureRandom().nextBytes(keyBytes);
        terminalKey = Base64.encodeToString(keyBytes, Base64.NO_WRAP);

        // 显示密钥指纹（前16位）
        String fingerprint = getKeyFingerprint(terminalKey);
        tvTerminalFingerprint.setText("终端密钥: " + fingerprint + "... (已锁入硬件)");
        tvTerminalFingerprint.setTextColor(getColor(android.R.color.holo_green_dark));

        log("✓ 终端生成了唯一AES密钥");
        log("  密钥指纹: " + fingerprint + "...");
        log("  密钥已锁入硬件安全区，无法读出");
        log("========================================");
    }

    /**
     * 步骤2: APP扫码配对 - 获取终端密钥
     */
    private void step2_AppPair() {
        if (terminalKey == null) {
            log("[步骤2] ❌ 失败: 请先执行步骤1（终端初始化）");
            return;
        }

        log("\n[步骤2] APP扫码配对开始...");
        log("  模拟: 终端屏幕显示二维码（包含加密后的密钥）");
        log("  模拟: APP扫描二维码");

        // 模拟扫码传输：APP获取终端密钥
        appKey = terminalKey;

        tvAppStatus.setText("APP状态: 已配对 ✓ (密钥已存储)");
        tvAppStatus.setTextColor(getColor(android.R.color.holo_green_dark));

        log("✓ APP成功获取终端密钥");
        log("  密钥指纹: " + getKeyFingerprint(appKey) + "...");
        log("  APP将密钥存入本地KeyStore");
        log("========================================");
    }

    /**
     * 步骤3: APP发送命令 - 加密并发送
     */
    private void step3_AppSendCommand(String command) {
        if (appKey == null) {
            log("[步骤3] ❌ 失败: 请先执行步骤2（APP配对）");
            return;
        }

        log("\n[步骤3] APP发送命令: " + command);

        currentCommand = command;

        // 使用你的AuthorizationUtils进行AES加密
        encryptedCommand = AuthorizationUtils.encryptWithSHA256Key(command, appKey);

        if (encryptedCommand == null) {
            log("❌ 加密失败");
            tvEncrypted.setText("加密密文: 加密失败");
            return;
        }

        // 显示密文（截取前50字符）
        String displayEncrypted = encryptedCommand.length() > 50 ?
                encryptedCommand.substring(0, 50) + "..." : encryptedCommand;
        tvEncrypted.setText("加密密文: " + displayEncrypted);
        tvEncrypted.setTextColor(getColor(android.R.color.holo_blue_dark));

        log("✓ 命令已加密");
        log("  明文: " + command);
        log("  密文(前50): " + displayEncrypted);
        log("  加密算法: AES-256/SHA-256");
        log("========================================");
    }

    /**
     * 步骤4: 服务器转发 - 纯转发，不解密
     */
    private void step4_ServerForward() {
        if (encryptedCommand == null) {
            log("[步骤4] ❌ 失败: 请先执行步骤3（发送命令）");
            return;
        }

        log("\n[步骤4] 服务器处理...");

        tvForward.setText("转发状态: 已转发密文到终端 ✓");
        tvForward.setTextColor(getColor(android.R.color.holo_green_dark));

        log("✓ 服务器收到密文");
        log("  服务器不做任何解密操作");
        log("  服务器不知道命令内容");
        log("  服务器将密文原封不动转发给终端");
        log("========================================");
    }

    /**
     * 步骤5: 终端验签执行 - 解密并执行
     */
    private void step5_TerminalExecute() {
        if (terminalKey == null) {
            log("[步骤5] ❌ 失败: 请先执行步骤1（终端初始化）");
            return;
        }

        if (encryptedCommand == null) {
            log("[步骤5] ❌ 失败: 请先执行步骤3和步骤4（发送命令+转发）");
            return;
        }

        log("\n[步骤5] 终端处理...");

        // 终端用本地密钥解密
        String decrypted = AuthorizationUtils.decryptWithSHA256Key(encryptedCommand, terminalKey);

        if (decrypted == null || TextUtils.isEmpty(decrypted)) {
            log("❌ 解密失败！");
            tvDecryptResult.setText("解密结果: 失败");
            tvDecryptResult.setTextColor(getColor(android.R.color.holo_red_dark));
            tvExecuteResult.setText("执行状态: ❌ 拒绝执行（非法消息）");
            tvExecuteResult.setTextColor(getColor(android.R.color.holo_red_dark));
            log("  原因: 密钥不匹配或密文被篡改");
            log("  终端已丢弃此消息");
            return;
        }

        // 解密成功
        tvDecryptResult.setText("解密结果: " + decrypted);
        tvDecryptResult.setTextColor(getColor(android.R.color.holo_green_dark));

        log("✓ 解密成功");
        log("  解密结果: " + decrypted);

        // 执行命令
        executePhysicalCommand(decrypted);
    }

    /**
     * 执行物理命令（模拟）
     */
    private void executePhysicalCommand(String command) {
        log("  正在执行命令...");

        String result;
        switch (command) {
            case "开灯":
                result = "💡 灯光已开启";
                break;
            case "关灯":
                result = "🌙 灯光已关闭";
                break;
            case "调亮":
                result = "✨ 亮度已调高";
                break;
            default:
                result = "⚠️ 未知命令: " + command;
                break;
        }

        tvExecuteResult.setText("执行状态: ✓ " + result);
        tvExecuteResult.setTextColor(getColor(android.R.color.holo_green_dark));

        log("  " + result);
        log("========================================");
    }

    /**
     * 模拟攻击：服务器被黑，攻击者发送伪造命令
     */
    private void simulateAttack() {
        log("\n========================================");
        log("⚠️⚠️⚠️ 攻击模拟开始 ⚠️⚠️⚠️");
        log("场景: 服务器被黑客攻破");
        log("========================================");

        // 攻击者没有正确的终端密钥，尝试用假密钥加密假命令
        String fakeKey = "fake_attacker_key_12345678";
        String fakeCommand = "开门";

        String fakeEncrypted = AuthorizationUtils.encryptWithSHA256Key(fakeCommand, fakeKey);

        if (fakeEncrypted != null) {
            // 替换原有密文
            encryptedCommand = fakeEncrypted;

            String displayFake = fakeEncrypted.length() > 50 ?
                    fakeEncrypted.substring(0, 50) + "..." : fakeEncrypted;

            tvEncrypted.setText("加密密文: [攻击者伪造] " + displayFake);
            tvForward.setText("转发状态: 攻击者已替换密文");
            tvDecryptResult.setText("解密结果: 待验证");
            tvExecuteResult.setText("执行状态: 待验证");

            log("攻击者操作:");
            log("  1. 控制了服务器");
            log("  2. 伪造命令: " + fakeCommand);
            log("  3. 用假密钥加密: " + displayFake);
            log("  4. 将伪造密文发送给终端");
            log("");
            log("现在点击【步骤5: 终端验签执行】查看结果");
            log("预期: 终端解密失败，拒绝执行");
        } else {
            log("❌ 攻击模拟失败: 加密出错");
        }
        log("========================================");
    }

    /**
     * 重置所有状态
     */
    private void resetAll() {
        terminalKey = null;
        appKey = null;
        encryptedCommand = null;
        currentCommand = null;
        logBuilder = new StringBuilder();

        tvTerminalFingerprint.setText("终端密钥: 未生成");
        tvTerminalFingerprint.setTextColor(getColor(android.R.color.darker_gray));
        tvAppStatus.setText("APP状态: 未配对");
        tvAppStatus.setTextColor(getColor(android.R.color.darker_gray));
        tvEncrypted.setText("加密密文: 未发送");
        tvEncrypted.setTextColor(getColor(android.R.color.darker_gray));
        tvForward.setText("转发状态: 未转发");
        tvForward.setTextColor(getColor(android.R.color.darker_gray));
        tvDecryptResult.setText("解密结果: 未执行");
        tvDecryptResult.setTextColor(getColor(android.R.color.darker_gray));
        tvExecuteResult.setText("执行状态: 待执行");
        tvExecuteResult.setTextColor(getColor(android.R.color.darker_gray));

        log("========================================");
        log("已重置所有状态");
        log("========================================");
    }

    /**
     * 添加日志
     */
    private void log(String msg) {
        if (logBuilder == null) {
            logBuilder = new StringBuilder();
        }
        logBuilder.append(msg).append("\n");
        tvLog.setText(logBuilder.toString());

        // 自动滚动到底部
        final ScrollView scrollView = findViewById(R.id.scroll_log);
        if (scrollView != null) {
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        }

        Log.d(TAG, msg);
    }

    /**
     * 获取密钥指纹（前16位）
     */
    private String getKeyFingerprint(String key) {
        if (key == null || key.length() < 16) {
            return "null";
        }
        return key.substring(0, Math.min(16, key.length()));
    }
}
package com.lenkeng.udpdemo.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lenkeng.udpdemo.R;
import com.lenkeng.udpdemo.utils.AES;
import com.lenkeng.udpdemo.utils.KeyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AesActivity extends AppCompatActivity {
    private TextView tvLog;
    private Button btnEncrypt, btnDecrypt, btnVerify;
    private ProgressBar progressBar;

    private File originalFile;      // 原始文件
    private File encryptedFile;     // 加密后的文件
    private File decryptedFile;     // 解密后的文件

    private String secretKey;       // AES密钥

    private StringBuilder logBuilder = new StringBuilder();
    private ExecutorService executorService;  // 线程池
    private Handler mainHandler;              // 主线程Handler

    private boolean isProcessing = false;     // 是否正在处理中

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aes);

        // 初始化线程池（单线程池，保证任务顺序执行）
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        initViews();
        loadSecretKey();
        initTestFile();
        setupListeners();
    }

    private void initViews() {
        tvLog = findViewById(R.id.tv_log);
        btnEncrypt = findViewById(R.id.btn_encrypt);
        btnDecrypt = findViewById(R.id.btn_decrypt);
        btnVerify = findViewById(R.id.btn_verify);

        // 添加进度条（需要在布局文件中添加，或者先注释掉）
        progressBar = findViewById(R.id.progress_bar);
        if (progressBar == null) {
            // 如果布局中没有进度条，创建一个临时变量避免空指针
            // 建议在activity_aes.xml中添加ProgressBar
        }

        // 隐藏文件选择按钮相关的TextView
        TextView tvFilePath = findViewById(R.id.tv_file_path);
        Button btnSelectFile = findViewById(R.id.btn_select_file);
        if (tvFilePath != null) tvFilePath.setVisibility(android.view.View.GONE);
        if (btnSelectFile != null) btnSelectFile.setVisibility(android.view.View.GONE);
    }

    private void loadSecretKey() {
        secretKey = KeyUtils.readKeyFromFile(this);
        if (secretKey == null || secretKey.isEmpty()) {
            appendLog("警告: 无法从assets读取密钥文件(secret.key)，使用默认密钥");
            secretKey = "defaultTestKey1234567890!@#$%";
        } else {
            appendLog("✓ 成功加载密钥: " + maskKey(secretKey));
        }
    }

    private String maskKey(String key) {
        if (key.length() <= 8) return "***";
        return key.substring(0, 2) + "****" + key.substring(key.length() - 2);
    }

    private void initTestFile() {
        String filePath = "/data/data/com.lenkeng.udpdemo/files/testFile.zip";
        originalFile = new File(filePath);

        if (originalFile.exists()) {
            appendLog("✓ 找到测试文件: " + originalFile.getAbsolutePath());
            appendLog("  文件大小: " + originalFile.length() + " bytes (" + formatFileSize(originalFile.length()) + ")");
            btnEncrypt.setEnabled(true);
        } else {
            appendLog("✗ 未找到测试文件: " + filePath);
            appendLog("  请确保文件存在于该路径下");
        }
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
        return String.format("%.2f MB", size / (1024.0 * 1024.0));
    }

    private void setupListeners() {
        btnEncrypt.setOnClickListener(v -> {
            if (!isProcessing) {
                encryptFileAsync();
            } else {
                Toast.makeText(this, "请等待当前操作完成", Toast.LENGTH_SHORT).show();
            }
        });

        btnDecrypt.setOnClickListener(v -> {
            if (!isProcessing) {
                decryptFileAsync();
            } else {
                Toast.makeText(this, "请等待当前操作完成", Toast.LENGTH_SHORT).show();
            }
        });

        btnVerify.setOnClickListener(v -> {
            if (!isProcessing) {
                verifyResultAsync();
            } else {
                Toast.makeText(this, "请等待当前操作完成", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 异步加密文件
     */
    private void encryptFileAsync() {
        if (originalFile == null || !originalFile.exists()) {
            appendLog("✗ 测试文件不存在，无法加密");
            return;
        }

        setButtonsEnabled(false);
        isProcessing = true;
        showProgress(true);

        appendLog("\n========== 开始加密文件 ==========");
        appendLog("源文件: " + originalFile.getAbsolutePath());
        appendLog("源文件大小: " + formatFileSize(originalFile.length()));

        executorService.execute(() -> {
            try {
                File encryptDir = new File(getFilesDir(), "encrypted");
                if (!encryptDir.exists()) {
                    encryptDir.mkdirs();
                }

                String encryptedFileName = "testRes_encrypted.enc";
                long startTime = System.currentTimeMillis();

                File result = AES.encryptFile(originalFile, encryptDir.getAbsolutePath(), encryptedFileName, secretKey);

                long endTime = System.currentTimeMillis();
                final long duration = endTime - startTime;
                final File resultFile = result;

                mainHandler.post(() -> {
                    if (resultFile != null && resultFile.exists()) {
                        encryptedFile = resultFile;
                        appendLog("✓ 加密成功! 耗时: " + duration + " ms");
                        appendLog("  加密文件: " + encryptedFile.getAbsolutePath());
                        appendLog("  加密文件大小: " + formatFileSize(encryptedFile.length()));
                        btnDecrypt.setEnabled(true);
                    } else {
                        appendLog("✗ 加密失败!");
                    }
                    setButtonsEnabled(true);
                    isProcessing = false;
                    showProgress(false);
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    appendLog("✗ 加密异常: " + e.getMessage());
                    e.printStackTrace();
                    setButtonsEnabled(true);
                    isProcessing = false;
                    showProgress(false);
                });
            }
        });
    }

    /**
     * 异步解密文件
     */
    private void decryptFileAsync() {
        if (encryptedFile == null || !encryptedFile.exists()) {
            appendLog("✗ 请先加密文件");
            setButtonsEnabled(true);
            isProcessing = false;
            showProgress(false);
            return;
        }

        setButtonsEnabled(false);
        isProcessing = true;
        showProgress(true);

        appendLog("\n========== 开始解密文件 ==========");
        appendLog("加密文件: " + encryptedFile.getAbsolutePath());
        appendLog("加密文件大小: " + formatFileSize(encryptedFile.length()));

        executorService.execute(() -> {
            try {
                File decryptDir = new File(getFilesDir(), "decrypted");
                if (!decryptDir.exists()) {
                    decryptDir.mkdirs();
                }

                String decryptedFileName = "testRes_decrypted.zip";
                long startTime = System.currentTimeMillis();

                File result = AES.decryptFile(encryptedFile, decryptDir.getAbsolutePath(), decryptedFileName, secretKey);

                long endTime = System.currentTimeMillis();
                final long duration = endTime - startTime;
                final File resultFile = result;

                mainHandler.post(() -> {
                    if (resultFile != null && resultFile.exists()) {
                        decryptedFile = resultFile;
                        appendLog("✓ 解密成功! 耗时: " + duration + " ms");
                        appendLog("  解密文件: " + decryptedFile.getAbsolutePath());
                        appendLog("  解密文件大小: " + formatFileSize(decryptedFile.length()));
                        btnVerify.setEnabled(true);
                    } else {
                        appendLog("✗ 解密失败!");
                    }
                    setButtonsEnabled(true);
                    isProcessing = false;
                    showProgress(false);
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    appendLog("✗ 解密异常: " + e.getMessage());
                    e.printStackTrace();
                    setButtonsEnabled(true);
                    isProcessing = false;
                    showProgress(false);
                });
            }
        });
    }

    /**
     * 异步验证结果
     */
    private void verifyResultAsync() {
        if (originalFile == null || decryptedFile == null) {
            appendLog("✗ 请先完成加解密流程");
            return;
        }

        setButtonsEnabled(false);
        isProcessing = true;
        showProgress(true);

        appendLog("\n========== 验证加解密结果 ==========");

        executorService.execute(() -> {
            try {
                long originalSize = originalFile.length();
                long decryptedSize = decryptedFile.length();

                StringBuilder resultLog = new StringBuilder();
                boolean success = false;

                if (originalSize != decryptedSize) {
                    resultLog.append("✗ 验证失败: 文件大小不一致!\n");
                    resultLog.append("原始大小: ").append(formatFileSize(originalSize)).append("\n");
                    resultLog.append("解密大小: ").append(formatFileSize(decryptedSize));
                } else {
                    resultLog.append("✓ 文件大小一致: ").append(formatFileSize(originalSize)).append("\n\n");

                    resultLog.append("正在计算MD5...\n");
                    String originalMd5 = getFileMD5(originalFile);
                    String decryptedMd5 = getFileMD5(decryptedFile);

                    resultLog.append("原始文件MD5: ").append(originalMd5).append("\n");
                    resultLog.append("解密文件MD5: ").append(decryptedMd5).append("\n\n");

                    if (originalMd5.equals(decryptedMd5)) {
                        success = true;
                        resultLog.append("╔════════════════════════════════════╗\n");
                        resultLog.append("║  ✓✓✓ 验证成功！加解密功能正常！ ✓✓✓  ║\n");
                        resultLog.append("╚════════════════════════════════════╝");
                    } else {
                        resultLog.append("✗ 验证失败: MD5不一致!");
                    }
                }

                final boolean finalSuccess = success;
                final String finalResultLog = resultLog.toString();

                mainHandler.post(() -> {
                    appendLog(finalResultLog);
                    if (finalSuccess) {
                        Toast.makeText(AesActivity.this, "验证成功！加解密功能正常", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AesActivity.this, "验证失败", Toast.LENGTH_LONG).show();
                    }
                    setButtonsEnabled(true);
                    isProcessing = false;
                    showProgress(false);
                });
            } catch (Exception e) {
                mainHandler.post(() -> {
                    appendLog("✗ 验证异常: " + e.getMessage());
                    e.printStackTrace();
                    setButtonsEnabled(true);
                    isProcessing = false;
                    showProgress(false);
                });
            }
        });
    }

    /**
     * 设置按钮启用状态
     */
    private void setButtonsEnabled(boolean enabled) {
        mainHandler.post(() -> {
            btnEncrypt.setEnabled(enabled && originalFile != null);
            btnDecrypt.setEnabled(enabled && encryptedFile != null);
            btnVerify.setEnabled(enabled && decryptedFile != null);
        });
    }

    /**
     * 显示/隐藏进度（如果布局中有ProgressBar）
     */
    private void showProgress(boolean show) {
        mainHandler.post(() -> {
            if (progressBar != null) {
                progressBar.setVisibility(show ? android.view.View.VISIBLE : android.view.View.GONE);
            }
            // 也可以显示一个简单的文字提示
            if (show) {
                appendLog("⏳ 处理中，请稍候...");
            }
        });
    }

    /**
     * 计算文件MD5
     */
    private String getFileMD5(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[8192];
        int len;
        while ((len = fis.read(buffer)) != -1) {
            digest.update(buffer, 0, len);
        }
        fis.close();

        byte[] md5bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : md5bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private void appendLog(String message) {
        runOnUiThread(() -> {
            logBuilder.append(message).append("\n");
            tvLog.setText(logBuilder.toString());
            // 滚动到底部
            tvLog.post(() -> {
                android.view.View parent = (android.view.View) tvLog.getParent();
                if (parent != null) {
                    parent.scrollTo(0, tvLog.getBottom());
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
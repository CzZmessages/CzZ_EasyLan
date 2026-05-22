package com.lenkeng.udpdemo.utils;

import android.content.Context;
import android.util.Base64;
import android.util.Log;


import com.blankj.utilcode.util.ShellUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @ClassName: AuthorizationUtils
 * @Author: chenpengchi
 * @Date: 2026/2/2 0002
 * @Description: 鉴权处理
 * *    ┏┓   ┏┓   <-摸摸脑袋，神兽会保佑你的代码
 * *   ┏┛┻━━━┛┻┓
 * *   ┃       ┃
 * *   ┃   ━   ┃
 * *   ┃ ┳┛ ┗┳ ┃
 * *   ┃       ┃
 * *   ┃   ┻   ┃
 * *   ┃       ┃
 * *   ┗━┓   ┏━┛
 * *     ┃   ┃神兽保佑
 * *     ┃   ┃代码无BUG！
 * *     ┃   ┗━━━┓
 * *     ┃       ┣┓
 * *     ┃       ┏┛
 * *     ┗┓┓┏━┳┓┏┛
 * *      ┃┫┫ ┃┫┫
 * *      ┗┻┛ ┗┻┛
 * * ━━━━━━神兽出没━━━━━━
 */
public class AuthorizationUtils {
    private static final String COMMAND_ORDER = "getprop ro.serialno";
    private static final String TAG = AuthorizationUtils.class.getSimpleName() + " --> ";

    /**
     * 加密算法
     */
    private static final String KEY_ALGORITHM = "AES";

    /**
     * 字符编码
     */
    private static final java.nio.charset.Charset CHARSET_UTF8 = StandardCharsets.UTF_8;

    /**
     * 加解密算法/工作模式/填充方式
     */
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS7Padding";

    /**
     * 设备ID
     */
    private static final String TERMINAL_ID = "25033000099";

    /**
     * 加密版本号，用于向后兼容
     * 1 = MD5（旧版本）     * 2 = SHA-256（新版本）
     */
    private static final int ENCRYPTION_VERSION = 2;

    /**
     * 获取设备本身序列号
     */
    public static String obtainTerminalCode() {
        String result = String.valueOf(ShellUtils.execCmd(COMMAND_ORDER, false));

        if (result != null && result.contains("successMsg:")) {
            // 使用更简单的字符串处理方法
            int startIndex = result.indexOf("successMsg:") + "successMsg:".length();
            String remaining = result.substring(startIndex).trim();

            // 提取序列号（假设序列号是连续的数字字母组合）
            StringBuilder serial = new StringBuilder();
            for (char c : remaining.toCharArray()) {
                if (Character.isLetterOrDigit(c)) {
                    serial.append(c);
                } else if (serial.length() > 0) {
                    // 遇到非字母数字字符且已经提取到部分序列号，停止提取
                    break;
                }
            }

            if (serial.length() > 0) {
                return serial.toString();
            }
        }

        return result;
    }

    /**
     * 生成SHA-256哈希值（64位小写十六进制）
     *
     * @param input 输入字符串
     * @return SHA-256哈希值
     */
    public static String generateSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(CHARSET_UTF8));

            // 转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    /**
     * 固定值 本机序列号+本机ID，使用SHA-256作为密钥
     */
    public static String encryptWithSHA256Key(String originalKey) {
        String data = obtainTerminalCode() + TERMINAL_ID;
        return encryptWithSHA256Key(data, originalKey);
    }

//    /**
//     * 使用SHA-256作为密钥进行AES加密
//     *
//     * @param data        待加密的原始字符串
//     * @param originalKey 原始密钥（从KeyUtils读取的密钥）
//     * @return 返回Base64编码的加密数据
//     */
//    public static String encryptWithSHA256Key(String data, String originalKey) {
//        try {
//            // 1. 对原始密钥进行SHA-256哈希，生成AES密钥
//            String sha256Key = generateSHA256(originalKey);
//
//            // 2. 创建密码器并初始化
//            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
//            SecretKeySpec secretKeySpec = new SecretKeySpec(
//                    sha256Key.getBytes(CHARSET_UTF8),
//                    KEY_ALGORITHM
//            );
//            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
//
//            // 3. 执行加密
//            byte[] encryptByte = cipher.doFinal(data.getBytes(CHARSET_UTF8));
//
//            // 4. 返回Base64编码的加密数据
//            return Base64.encodeToString(encryptByte, Base64.NO_WRAP);
//
//        } catch (Exception e) {
//            handleException(e);
//        }
//        return null;
//    }

//    /**
//     * 使用SHA-256作为密钥进行AES解密
//     *
//     * @param encryptedData Base64编码的加密数据
//     * @param originalKey   原始密钥（从KeyUtils读取的密钥）
//     * @return 解密后的原始字符串
//     */
//    public static String decryptWithSHA256Key(String encryptedData, String originalKey) {
//        try {
//            // 1. 对原始密钥进行SHA-256哈希，生成AES密钥
//            String sha256Key = generateSHA256(originalKey);
//
//            // 2. 解码Base64数据
//            byte[] data = Base64.decode(encryptedData, Base64.NO_WRAP);
//
//            // 3. 创建密码器并初始化
//            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
//            SecretKeySpec secretKeySpec = new SecretKeySpec(
//                    sha256Key.getBytes(CHARSET_UTF8),
//                    KEY_ALGORITHM
//            );
//            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
//
//            // 4. 执行解密
//            byte[] result = cipher.doFinal(data);
//
//            // 5. 返回解密后的字符串
//            return new String(result, CHARSET_UTF8);
//
//        } catch (Exception e) {
//            handleException(e);
//        }
//        return null;
//    }
    /**
     * 使用SHA-256作为密钥进行AES-256加密
     */
    public static String encryptWithSHA256Key(String data, String originalKey) {
        try {
            // 1. 对原始密钥进行SHA-256哈希
            String sha256Hex = generateSHA256(originalKey);

            // 2. 将十六进制字符串转换为字节数组（32字节）
            byte[] keyBytes = hexStringToByteArray(sha256Hex);

            // 3. 创建密码器并初始化
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            // 4. 执行加密
            byte[] encryptByte = cipher.doFinal(data.getBytes(CHARSET_UTF8));

            // 5. 返回Base64编码的加密数据
            return Base64.encodeToString(encryptByte, Base64.NO_WRAP);

        } catch (Exception e) {
            handleException(e);
        }
        return null;
    }
    /**
     * 使用SHA-256作为密钥进行AES-256解密
     */
    public static String decryptWithSHA256Key(String encryptedData, String originalKey) {
        try {
            // 1. 对原始密钥进行SHA-256哈希
            String sha256Hex = generateSHA256(originalKey);

            // 2. 将十六进制字符串转换为字节数组（32字节）
            byte[] keyBytes = hexStringToByteArray(sha256Hex);

            // 3. 解码Base64数据
            byte[] data = Base64.decode(encryptedData, Base64.NO_WRAP);

            // 4. 创建密码器并初始化
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            // 5. 执行解密
            byte[] result = cipher.doFinal(data);

            // 6. 返回解密后的字符串
            return new String(result, CHARSET_UTF8);

        } catch (Exception e) {
            handleException(e);
        }
        return null;
    }
    /**
     * 十六进制字符串转换为字节数组
     */
    private static byte[] hexStringToByteArray(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0) {
            return new byte[0];
        }

        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 字节数组转换为十六进制字符串
     */
    private static String byteArrayToHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 完整的数据处理流程：SHA-256哈希数据 + SHA-256密钥AES加密
     *
     * @param originalData 原始字符串数据
     * @param originalKey  原始密钥（从KeyUtils读取）
     * @return Base64编码的最终加密数据
     */
    public static String processWithSHA256HashAndEncrypt(String originalData, String originalKey) {
        try {
            // 步骤1: 将原始数据转为SHA-256哈希
            String sha256Data = generateSHA256(originalData);

            // 步骤2: 使用SHA-256密钥进行AES加密
            return encryptWithSHA256Key(sha256Data, originalKey);

        } catch (Exception e) {
            handleException(e);
        }
        return null;
    }

// ====================== 向后兼容方法 ======================

    /**
     * 【兼容性】生成MD5哈希值（32位小写）
     * 保留此方法用于向后兼容
     */
    public static String generateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(CHARSET_UTF8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    /**
     * 【兼容性】使用MD5作为密钥进行AES解密
     * 用于解密旧版本数据
     */
    public static String decryptWithMD5Key(String encryptedData, String originalKey) {
        try {
            String md5Key = generateMD5(originalKey);
            byte[] data = Base64.decode(encryptedData, Base64.NO_WRAP);

            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(md5Key.getBytes(CHARSET_UTF8), KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte[] result = cipher.doFinal(data);
            return new String(result, CHARSET_UTF8);

        } catch (Exception e) {
            handleException(e);
        }
        return null;
    }

    /**
     * 智能解密：根据版本号自动选择解密算法
     *
     * @param encryptedData Base64编码的加密数据
     * @param originalKey   原始密钥
     * @param version       加密版本（1=MD5, 2=SHA-256）
     * @return 解密后的原始字符串
     */
    public static String decryptWithVersion(String encryptedData, String originalKey, int version) {
        switch (version) {
            case 1:
                return decryptWithMD5Key(encryptedData, originalKey);
            case 2:
                return decryptWithSHA256Key(encryptedData, originalKey);
            default:
                Log.e(TAG, "未知的加密版本: " + version);
                // 尝试使用SHA-256，如果不成功再尝试MD5
                try {
                    return decryptWithSHA256Key(encryptedData, originalKey);
                } catch (Exception e) {
                    return decryptWithMD5Key(encryptedData, originalKey);
                }
        }
    }

    /**
     * 获取当前加密版本号
     */
    public static int getEncryptionVersion() {
        return ENCRYPTION_VERSION;
    }

    /**
     * 处理异常
     */
    private static void handleException(Exception e) {
        e.printStackTrace();
        Log.e(TAG, TAG + e.getMessage());
    }

//    /**
//     * 测试方法 - 验证SHA-256加密解密
//     */
//    public static void testSHA256Encryption(Context context) {
//        try {
//            // 1. 读取原始密钥
//            String originalKey = KeyUtils.readKeyFromFile(context);
//            if (originalKey == null) {
//                Log.e(TAG, "无法读取密钥文件");
//                return;
//            }
//
//            // 2. 测试数据
//            String testData = "a831f8b107b72dfd" + "25033000021";
//
//            // 3. 测试SHA-256密钥生成
//            String sha256Key = generateSHA256(originalKey);
//            Log.d(TAG, "SHA-256密钥长度: " + sha256Key.length());
//            Log.d(TAG, "SHA-256密钥（前32位）: " + (sha256Key.length() > 32 ? sha256Key.substring(0, 32) + "..." : sha256Key));
//
//            // 4. 使用SHA-256作为密钥加密原始数据
//            String encrypted1 = encryptWithSHA256Key(testData, originalKey);
//            Log.d(TAG, "SHA-256加密结果: " + encrypted1);
//
//            String decrypted1 = decryptWithSHA256Key(encrypted1, originalKey);
//            Log.d(TAG, "SHA-256解密结果: " + decrypted1);
//            Log.d(TAG, "SHA-256解密验证: " + testData.equals(decrypted1));
//
//            // 5. 先SHA-256哈希数据，再用SHA-256密钥加密
//            String encrypted2 = processWithSHA256HashAndEncrypt(testData, originalKey);
//            Log.d(TAG, "SHA-256哈希后加密结果: " + encrypted2);
//
//            // 6. 验证SHA-256生成
//            String sha256Hash = generateSHA256(testData);
//            Log.d(TAG, "SHA-256哈希值: " + sha256Hash);
//            Log.d(TAG, "SHA-256长度: " + sha256Hash.length());
//
//            // 7. 测试向后兼容性
//            String md5Key = generateMD5(originalKey);
//            Log.d(TAG, "MD5密钥长度: " + md5Key.length());
//
//        } catch (Exception e) {
//            handleException(e);
//        }
//    }
    /**
     * 测试方法 - 验证SHA-256加密解密
     */
    public static void testSHA256Encryption(Context context) {
        try {
            // 1. 读取原始密钥
            String originalKey = KeyUtils.readKeyFromFile(context);
            if (originalKey == null) {
                Log.e(TAG, "无法读取密钥文件");
                return;
            }

            // 2. 测试数据
            String testData = "a831f8b107b72dfd" + "25033000021";

            // 3. 测试SHA-256密钥生成
            String sha256Hex = generateSHA256(originalKey);
            byte[] keyBytes = hexStringToByteArray(sha256Hex);

            Log.d(TAG, "SHA-256十六进制长度: " + sha256Hex.length());
            Log.d(TAG, "SHA-256字节数组长度: " + keyBytes.length);
            Log.d(TAG, "SHA-256密钥（前32位）: " + sha256Hex.substring(0, 32));

            // 4. 使用SHA-256作为密钥加密原始数据
            String encrypted1 = encryptWithSHA256Key(testData, originalKey);
            Log.d(TAG, "SHA-256加密结果: " + encrypted1);

            if (encrypted1 != null) {
                String decrypted1 = decryptWithSHA256Key(encrypted1, originalKey);
                Log.d(TAG, "SHA-256解密结果: " + decrypted1);
                Log.d(TAG, "SHA-256解密验证: " + testData.equals(decrypted1));
            }

            // 5. 验证SHA-256生成
            String sha256Hash = generateSHA256(testData);
            Log.d(TAG, "测试数据SHA-256哈希值: " + sha256Hash);
            Log.d(TAG, "测试数据SHA-256长度: " + sha256Hash.length());

        } catch (Exception e) {
            handleException(e);
        }
    }
}

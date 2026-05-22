package com.lenkeng.udpdemo.tcp;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.lenkeng.udpdemo.utils.AES;
import com.lenkeng.udpdemo.utils.KeyUtils;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: IncomingTcpClient
 * @Author: chenpengchi
 * @Date: 2025/5/9 0009
 * @Description: *    ┏┓   ┏┓   <-摸摸脑袋，神兽会保佑你的代码
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

public class IncomingTcpClient {
    private final String ip;
    private final Socket socket;
    private final Context context;
    private BufferedReader reader;
    private PrintWriter writer;
    private boolean isRunning = true;
    private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();
    private Thread sendThread;
private TcpClientListener listener;

    public void setListener(TcpClientListener listener) {
        this.listener = listener;
    }

    public IncomingTcpClient(Socket socket, Context context) throws Exception {
        this.socket = socket;
        this.ip = socket.getInetAddress().getHostAddress();
        this.context = context.getApplicationContext();
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
    }

    public void startListening() {
        new Thread(this::listenForMessages).start();
        startMessageSender();
        if (listener != null) {
            listener.onConnectionEstablished(ip);
        }
    }

    private void listenForMessages() {
        try {
            String line;
            while (isRunning && (line = reader.readLine()) != null) {
                LogUtils.d("[" + ip + "] 收到原文消息: " + line);
                String decrypted = AES.decrypt(line, KeyUtils.readKeyFromFile(context));
//                String decrypted =line;
                LogUtils.d("[" + ip + "] 收到消息: " + decrypted);
                if (listener != null) {
                    listener.onMessageReceived(ip, decrypted);
                }
            }
        } catch (Exception e) {
            LogUtils.e("[" + ip + "] 连接断开", e.getMessage());
            listener.onConnectionClosed(ip);
        } finally {
//            close();
        }
    }

    private void startMessageSender() {
        sendThread = new Thread(() -> {
            while (!sendThread.isInterrupted() && isRunning) {
                try {
                    String msg = messageQueue.poll(1, TimeUnit.SECONDS);
                    if (msg != null && writer != null && !writer.checkError()) {
                        writer.println(AES.encrypt(msg, KeyUtils.readKeyFromFile(context)));
//                        writer.println(msg);
                        LogUtils.d("[" + ip + "] 已发送消息: " + msg);
                    }
                } catch (Exception ignored) {
                    //读写或发送失败

                }
            }
        });
        sendThread.start();
    }

    public void sendMessage(String message) {
        try {
            // 使用 offer 带超时，避免无限阻塞
            boolean success = messageQueue.offer(message, 1, TimeUnit.SECONDS);
            if (!success) {
                LogUtils.e("队列满超时，消息发送失败: " + ip);
                // 触发重试或丢弃策略
//                handleQueueFull(message);
            }
        } catch (Exception e) {
            LogUtils.e("出现异常:"+e.getMessage());
        }
    }
    public boolean getSocketConnectionStatus(){
        return socket != null && socket.isConnected() && !socket.isClosed();
    }


    public void close() {
        isRunning = false;
        try {
            if (!socket.isClosed()) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        } catch (Exception ignored) {
            LogUtils.e("===》"+ignored);
        }
        if (listener != null) {
            listener.onConnectionClosed(ip);
        }
        LogUtils.i("[" + ip + "] 连接已关闭   是否关闭"+socket.isClosed());
    }

    public String getIp() {
        return ip;
    }


    public interface TcpClientListener{
        void onMessageReceived(String ip, String message);//收到消息
        void onConnectionClosed(String ip);//连接关闭
        void onConnectionEstablished(String ip);//建立连接
    }
}

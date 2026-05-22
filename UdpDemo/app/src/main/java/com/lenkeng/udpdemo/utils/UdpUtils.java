package com.lenkeng.udpdemo.utils;

import com.blankj.utilcode.util.LogUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @ClassName: UdpUtils
 * @Author: chenpengchi
 * @Date: 2025/8/14 0014
 * @Description:
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
public class UdpUtils {
    private static volatile UdpUtils instance;
    private DatagramSocket udpSocket;
    private int port = 14445; // 默认端口
    private boolean isListening = false;
    private Thread receiveThread;
    private AtomicBoolean isConnectLan = new AtomicBoolean(true);
    private OnMessageReceivedListener messageListener;

    // 单例模式
    public static UdpUtils getInstance() {
        if (instance == null) {
            synchronized (UdpUtils.class) {
                if (instance == null) {
                    instance = new UdpUtils();
                }
            }
        }
        return instance;
    }

    private UdpUtils() {
        // 私有构造函数
    }

    /**
     * 设置UDP端口号
     * @param port 端口号  启动监听
     */
    public void setPort(int port) {
        this.port = port;
        // 如果已经在监听，需要重启监听
        if (isListening) {
            stopListening();
            startListening();
        }
    }
    public void setSendPort(int port){
        this.port = port;
    }

    /**
     * 获取当前端口号
     * @return 当前端口号
     */
    public int getPort() {
        return port;
    }

    /**
     * 初始化UDP Socket
     * @throws SocketException
     */
    private void initSocket() throws SocketException {
        if (udpSocket != null && !udpSocket.isClosed()) {
            udpSocket.close();
        }
        udpSocket = new DatagramSocket(port);
        udpSocket.setBroadcast(true);
    }

    /**
     * 发送UDP消息
     * @param jsonData 要发送的JSON数据
     * @param targetAddress 目标地址
     * @param targetPort 目标端口
     * @throws IOException
     */
    public void sendMessage(String jsonData, InetAddress targetAddress, int targetPort) throws IOException {
        if (udpSocket == null || udpSocket.isClosed()) {
            initSocket();
        }

        new Thread(() -> {
            try {
                LogUtils.e("发送的数据:" + jsonData);
                byte[] data = jsonData.getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet = new DatagramPacket(data, data.length, targetAddress, targetPort);
                udpSocket.send(packet);
                String sendMsg="UDP数据发送成功，目标地址：" + targetAddress + ":" + targetPort;
                LogUtils.e(sendMsg);
                messageListener.sendSta(sendMsg);
            } catch (IOException e) {
                String sendFail="UDP发送失败：" + e.getMessage();
                LogUtils.e(sendFail);
                messageListener.sendSta(sendFail);
                if (messageListener != null) {
                    messageListener.onError(e);
                }
            }
        }).start();
    }

    /**
     * 发送广播消息（基于你原有的方法）
     * @param jsonData 要发送的JSON数据
     * @param broadcastAddress 广播地址
     * @throws IOException
     */
    public void sendDiscoveryMessage(String jsonData, InetAddress broadcastAddress) throws IOException {
        sendMessage(jsonData, broadcastAddress, port);
    }

    /**
     * 开启UDP监听
     */
    public void startListening() {
        LogUtils.e(""+isListening);
        if (isListening) return;

        isListening = true;

        try {
            if (udpSocket == null || udpSocket.isClosed()) {
                initSocket();
            }
        } catch (SocketException e) {
            LogUtils.e("初始化UDP Socket失败：" + e.getMessage());
            if (messageListener != null) {
                messageListener.onError(e);
            }
            return;
        }

        receiveThread = new Thread(() -> {
            byte[] buffer = new byte[1024];
            while (isListening && udpSocket != null && !udpSocket.isClosed()) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    udpSocket.receive(packet);

                    String receivedData = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    LogUtils.e("收到UDP数据：" + receivedData);
                    //这里设置原子布尔值 可设置不接收任何东西
                    if(isConnectLan.get()){
                        if (messageListener != null) {
                            messageListener.onMessageReceived(receivedData, packet.getAddress(), packet.getPort());
                        }
                    }
                } catch (IOException e) {
                    if (isListening) {
                        LogUtils.e("UDP接收数据异常：" + e.getMessage());
                        messageListener.onError(e);
                        if (messageListener != null) {
                            messageListener.onError(e);
                        }
                    }
                    break;
                }
            }
        });

        receiveThread.start();
        LogUtils.e("UDP监听已启动，端口：" + port);
    }

    /**
     * 停止UDP监听
     */
    public void stopListening() {
        isListening = false;

        if (udpSocket != null && !udpSocket.isClosed()) {
            udpSocket.close();
        }

        if (receiveThread != null && receiveThread.isAlive()) {
            receiveThread.interrupt();
        }

        LogUtils.e("UDP监听已停止");
    }
   /**
    * 设置是否接收信息
    * */
    public void setIsConnectLan(AtomicBoolean isConnectLan) {
        this.isConnectLan = isConnectLan;
    }

    /**
     * 设置消息接收监听器
     * @param listener 监听器
     */
    public void setOnMessageReceivedListener(OnMessageReceivedListener listener) {
        this.messageListener = listener;
    }

    /**
     * 消息接收监听器接口
     */
    public interface OnMessageReceivedListener {
        void sendSta(String sendMsgSta);//发送结果

        void onMessageReceived(String message, InetAddress senderAddress, int senderPort);
        void onError(Exception e);
    }

    /**
     * 释放资源
     */
    public void release() {
        stopListening();
        instance = null;}
}

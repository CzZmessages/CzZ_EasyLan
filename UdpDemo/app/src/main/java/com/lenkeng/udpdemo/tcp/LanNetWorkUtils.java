package com.lenkeng.udpdemo.tcp;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.lenkeng.udpdemo.utils.AES;
import com.lenkeng.udpdemo.utils.KeyUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName: LanNetWorkUtils
 * @Author: chenpengchi
 * @Date: 2026/4/10 0010
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
public class LanNetWorkUtils {
    private static volatile LanNetWorkUtils INSTANCE;
    private Context context;
    private DatagramSocket udpSocket;
    private InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255"); // 广播地址
    public static LanNetWorkUtils getInstance(Context context) throws Exception {//全局单例
        if (INSTANCE == null) {
            INSTANCE = new LanNetWorkUtils(context);
        }
        return INSTANCE;
    }
    private LanNetWorkUtils(Context context)throws Exception{
        udpSocket = new DatagramSocket();
        udpSocket.setBroadcast(true);
        this.context=context;
    }
    //1.发送UDP验证消息
    public void sendUdpTerminalMsg(String msg){
        //加密处理
        String ecyData= AES.encrypt(msg, KeyUtils.readKeyFromFile(context));
        new Thread(() -> {
            try {
                LogUtils.e("发送的数据:" + msg);
                byte[] data = ecyData.getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet = new DatagramPacket(data, data.length, broadcastAddress, 14445);
                udpSocket.send(packet);
                LogUtils.e("是否发送出去了：" + udpSocket.getBroadcast());
            } catch (Exception e) {
                LogUtils.e("发送异常:" + e.getMessage());
            }
        }).start();
    }

}

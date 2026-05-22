package com.lenkeng.udpdemo.utils;

import com.blankj.utilcode.util.LogUtils;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @ClassName: NetworkingUtils
 * @Author: chenpengchi
 * @Date: 2026/3/16 0016
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
public class NetworkingUtils {
    private static volatile NetworkingUtils INSTANCE;
    private UdpUtils udpUtils;
    private OnNetWorkingCallBack onNetWorkingCallBack;

    public static NetworkingUtils getINSTANCE() {
        if (INSTANCE == null) {
            synchronized (NetworkingUtils.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NetworkingUtils();
                }
            }
        }
        return INSTANCE;
    }

    public void setOnNetWorkingCallBack(OnNetWorkingCallBack onNetWorkingCallBack) {
        this.onNetWorkingCallBack = onNetWorkingCallBack;
    }

    //私有化构造方法
    private NetworkingUtils() {
        udpUtils = UdpUtils.getInstance();
        addListener();//监听
    }
    //开启UDP方法
    public void openUdp(){
        udpUtils.startListening();
    }
    //接收或不接收任何讯息
    public void openAndCloseMsg(boolean isRecvice){
         udpUtils.setIsConnectLan(new AtomicBoolean(isRecvice));
    }
    //强制关闭UDP方法
    public void shutDownUdp(){
        udpUtils.stopListening();
    }
    //设置端口号
    public void setUdpPort(int port){
     udpUtils.setPort(port);
    }
    //发送消息
    public void sendMessage(String msg,String ip){
        try {
            //假设第一步加密 测试阶段不加密
            udpUtils.sendDiscoveryMessage(msg,InetAddress.getByName(ip) );
        }catch (Exception e){
            LogUtils.e("发送出现异常:"+e.getMessage());
        }

    }
    //回调方法
    private void addListener(){
       udpUtils.setOnMessageReceivedListener(new UdpUtils.OnMessageReceivedListener() {
           @Override
           public void sendSta(String sendMsgSta) {//发送讯息
               onNetWorkingCallBack.sendMsg(sendMsgSta);
           }

           @Override
           public void onMessageReceived(String message, InetAddress senderAddress, int senderPort) {//接收讯息
               onNetWorkingCallBack.receivedMsg(message);
           }

           @Override
           public void onError(Exception e) {//异常
               onNetWorkingCallBack.onError(e.getMessage());
           }
       });
    }


    public interface OnNetWorkingCallBack{
        void sendMsg(String msg);//发送的消息
        void receivedMsg(String reMsg);//接收到的消息
        void onError(String errorMsg);//异常信息
    }
}

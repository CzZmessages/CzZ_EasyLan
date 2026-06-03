package com.lenkeng.udpdemo.tcp;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.lenkeng.udpdemo.bean.InconimgMessage;
import com.lenkeng.udpdemo.utils.Constant;
import com.lenkeng.udpdemo.viewmodel.TcpConnectionViewModel;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: TcpServerForIncomingConnections
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
public class TcpServerForIncomingConnectionsManager {
    private ServerSocket serverSocket;
    private final int port;
    private final Context context;
    private volatile boolean isRunning = true;
    private final Map<String, IncomingTcpClient> connectionMap = new HashMap<>();
    private final BlockingQueue<InconimgMessage> messageQueue = new LinkedBlockingQueue<>();//消息队列
    private volatile boolean isQueueRunning = true;
    private ExecutorService ioExecutor = Executors.newCachedThreadPool();//IO流读写线程池
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();//消息队列的单一线程池
    private long lastProcessedTime = 0L;
    private static final long MIN_INTERVAL = 100; // 最小间隔
    private OnCallBack onCallBack;
    private Gson gson;

    // 单例实例
    private static volatile TcpServerForIncomingConnectionsManager instance;

    public void setOnCallBack(OnCallBack onCallBack) {
        this.onCallBack = onCallBack;
    }

    private TcpServerForIncomingConnectionsManager(int port, Context context) {
        this.port = port;
        this.context = context.getApplicationContext();
        gson = new Gson();
        startConsumingMessages();
    }

    public static TcpServerForIncomingConnectionsManager getInstance(int port, Context context) {
        LogUtils.e("开始监听端口===》" + port);
        if (instance == null) {
            synchronized (TcpServerForIncomingConnectionsManager.class) {
                if (instance == null) {
                    instance = new TcpServerForIncomingConnectionsManager(port, context);
                }
            }
        }
        return instance;
    }

    private void startConsumingMessages() {//开启消息队列
        executor.scheduleAtFixedRate(() -> {
            if (System.currentTimeMillis() - lastProcessedTime >= MIN_INTERVAL) {
                InconimgMessage msg = messageQueue.poll();
                if (msg != null) {
                    LogUtils.d("取出队列:" + msg.getIp());
                    TcpConnectionViewModel.getInstance(context.getApplicationContext())
                            .onNewMessageReceived(msg.getIp(), msg.getMessage());
                    lastProcessedTime = System.currentTimeMillis();
                }
            }
        }, 0, 100, TimeUnit.MILLISECONDS); // 更高频轮询，但只在满足间隔条件时处理
    }

    public void stopConsumingMessages() {
        isRunning = false;
        executor.shutdownNow();
    }

    public void startListening() {
        ioExecutor.submit(() -> {
            try {
                serverSocket = new ServerSocket(Constant.TEST_PORT);
                LogUtils.i("开始监听端口：" + port);

                while (isRunning) {
                    Socket clientSocket = serverSocket.accept();
                    LogUtils.e("新连接IP=[" + clientSocket.getInetAddress().getHostAddress() + "]" + "新的连接状态:" + clientSocket.isConnected());
                    IncomingTcpClient incomingClient = new IncomingTcpClient(clientSocket, context);
                    incomingClient.setListener(new IncomingTcpClient.TcpClientListener() {
                        @Override
                        public void onMessageReceived(String ip, String message) {
                            LogUtils.e("进入队列:" + ip, message);
                            //这里增加一个队列 逐一放入通知给viewmodel，不然速度太快会漏掉一个
//                            DataProcessBean bean = gson.fromJson(message, DataProcessBean.class);
//                            //这里做分类除刷新操作其余都走viewmodel
//                            if(bean.getOperation_type().trim().equals(Constant.TCP_RESULT_TERMINAL_MESSAGE)){
//                                //这里直接使用回调出去，不然viewmodel会吃不消
//                                onCallBack.onRfreshData(new InconimgMessage(ip, message));
//                            }else {
                            //思考老半天 还是调用队列  nmd....
                            messageQueue.offer(new InconimgMessage(ip, message));
//                            }
//

                        }

                        @Override
                        public void onConnectionClosed(String ip) {
                            LogUtils.e("移除对应连接:"+ip);
                            //连接关闭
                            TcpConnectionViewModel.getInstance(context).onConnectionClosed(ip);
                            //移除对应连接
                            connectionMap.remove(ip);
                        }

                        @Override
                        public void onConnectionEstablished(String ip) {
                            //连接建立
                            TcpConnectionViewModel.getInstance(context).onNewConnection(ip);
                        }
                    });

                    incomingClient.startListening();
                    connectionMap.put(incomingClient.getIp(), incomingClient);//键值对存入对应连接属性以及IP
                }

            } catch (Exception e) {
                LogUtils.e("Socket异常", e.getMessage());
            }
        });
    }

    public void stopListening() {
//        isRunning = false;
        LogUtils.e("关闭了监听");
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            for (IncomingTcpClient client : connectionMap.values()) {
                client.close();
            }
            connectionMap.clear();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    //终止所有连接
    public void disconnectAll() {
        new Thread(() -> {
            try {
                LogUtils.a("=IncomingTcpClient===》" + connectionMap.size());
                // 创建keySet的副本进行遍历
                for (String ip : new ArrayList<>(connectionMap.keySet())) {
                    IncomingTcpClient client = connectionMap.get(ip);
                    if (client != null && client.getSocketConnectionStatus()) {
                        System.out.println("close===》" + client.getIp());
                        client.close();  // close里会触发移除
                    }
                }
            } catch (Exception e) {
                LogUtils.e("关闭异常:" + e.getMessage());
            }
        }).start();
    }

    public void closeOrderTerminalSocket(String ip) {
        new Thread(() -> {
            try {
                IncomingTcpClient tcpClient = connectionMap.get(ip);
                if (tcpClient != null) {
                    if (tcpClient.getSocketConnectionStatus()) {
                        tcpClient.close();
                    }
                }
                connectionMap.remove(ip);
                LogUtils.d("移除对应IP:  [" + ip + "] socket 连接");
            } catch (Exception e) {
                LogUtils.d("关闭异常:" + e.getMessage());
            }
        }).start();

    }


    public void sendMessageTo(String ip, String message) {
        new Thread(() -> {
            LogUtils.e("发送的ip:" + ip, connectionMap.size());
            IncomingTcpClient client = connectionMap.get(ip);
            if (client != null) {
                client.sendMessage(message);
            } else {
                LogUtils.w("[" + ip + "] 当前无连接");
                //应该回调出去
                TcpConnectionViewModel.getInstance(context).onConnectionClosed(ip);
            }
        }).start();

    }

    public interface OnCallBack {
        void onRfreshData(InconimgMessage inconimgMessage);
    }
}

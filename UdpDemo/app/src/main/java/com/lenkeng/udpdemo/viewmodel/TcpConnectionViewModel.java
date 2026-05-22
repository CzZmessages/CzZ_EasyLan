package com.lenkeng.udpdemo.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;


import com.lenkeng.udpdemo.bean.InconimgMessage;
import com.lenkeng.udpdemo.liveData.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: TcpConnectionViewModel
 * @Author: chenpengchi
 * @Date: 2025/5/14 0014
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
public class TcpConnectionViewModel extends AndroidViewModel {
    private static TcpConnectionViewModel instance;
    // 新连接事件
    private MutableLiveData<String> newConnectionEvent = new MutableLiveData<>();
    // 缓存未处理的消息
    private List<InconimgMessage> pendingMessages = new ArrayList<>();

    // 暴露给 UI 的 LiveData
    private MutableLiveData<List<InconimgMessage>> messagesLiveData = new MutableLiveData<>();


    //收到消息的新事件
    private SingleLiveEvent<InconimgMessage> newMessageEvent = new SingleLiveEvent<>();
    //连接关闭事件
    private MutableLiveData<String> connectionClosedEvent = new MutableLiveData<>();

    public TcpConnectionViewModel(@NonNull Application application) {
        super(application);
    }

    public static TcpConnectionViewModel getInstance(Context context) {
        if (instance == null) {
            instance = new ViewModelProvider.AndroidViewModelFactory((Application) context.getApplicationContext())
                    .create(TcpConnectionViewModel.class);
        }
        return instance;
    }

    public LiveData<String> getNewConnectionEvent() {
        return newConnectionEvent;
    }

    public void onNewConnection(String ip) {
        newConnectionEvent.postValue(ip);
    }

    public LiveData<InconimgMessage> getNewMessageEvent() {
        return newMessageEvent;
    }

    public void onNewMessageReceived(String ip, String message) {
        newMessageEvent.postValue(new InconimgMessage(ip, message));
    }

    // 接收新消息，不立即通知 UI
    public void onAddMessagesEvent(String ip, String message) {
        pendingMessages.add(new InconimgMessage(ip, message));
    }
    // 定期调用此方法，批量通知 UI 更新
    public void flushMessages() {
        if (!pendingMessages.isEmpty()) {
            List<InconimgMessage> copy = new ArrayList<>(pendingMessages);
            pendingMessages.clear();
            messagesLiveData.postValue(copy); // 通知 UI 更新
        }
    }
    // 提供给 UI 订阅的数据源
    public LiveData<List<InconimgMessage>> getMessagesLiveData() {
        return messagesLiveData;
    }
    public LiveData<String> getConnectionClosedEvent() {
        return connectionClosedEvent;
    }

    public void onConnectionClosed(String ip) {
        connectionClosedEvent.postValue(ip);
    }
}

package com.lenkeng.udpdemo.bean;

/**
 * @ClassName: InconimgMessage
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
public class InconimgMessage {
    private  String ip;
    private  String message;

    public InconimgMessage(String ip, String message) {
        this.ip = ip;
        this.message = message;
    }

    public String getIp() {
        return ip;
    }

    public String getMessage() {
        return message;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

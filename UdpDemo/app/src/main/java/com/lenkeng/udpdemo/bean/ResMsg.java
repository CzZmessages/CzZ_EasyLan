package com.lenkeng.udpdemo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: ResMsg
 * @Author: chenpengchi
 * @Date: 2025/12/30 0030
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
public class ResMsg implements Serializable {
    private boolean isDefault;//是否属于标准广告
    private List<AdData> resData;//资源数据集合
    private List<SleepData> resSleepData; //睡眠集合数据

    public ResMsg(boolean isDefault, List<AdData> resData, List<SleepData> resSleepData) {
        this.isDefault = isDefault;
        this.resData = resData;
        this.resSleepData = resSleepData;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public List<AdData> getResData() {
        return resData;
    }

    public void setResData(List<AdData> resData) {
        this.resData = resData;
    }

    public List<SleepData> getResSleepData() {
        return resSleepData;
    }

    public void setResSleepData(List<SleepData> resSleepData) {
        this.resSleepData = resSleepData;
    }
}

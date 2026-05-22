package com.lenkeng.udpdemo.bean;

import java.io.Serializable;

/**
 * @ClassName: UrgentData
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
public class UrgentData implements Serializable {
    private String startTime;//开始时间
    private String endTime;//结束时间
    private long totalADPlay;//播放总时长
    private int playTotalCount;//播放次数
    private int type;//0 自定义开始与结束   1播放总时长  2播放总次数

    public UrgentData() {
    }

    public UrgentData(String startTime, String endTime, long totalADPlay, int playTotalCount, int type) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalADPlay = totalADPlay;
        this.playTotalCount = playTotalCount;
        this.type = type;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public long getTotalADPlay() {
        return totalADPlay;
    }

    public void setTotalADPlay(long totalADPlay) {
        this.totalADPlay = totalADPlay;
    }

    public int getPlayTotalCount() {
        return playTotalCount;
    }

    public void setPlayTotalCount(int playTotalCount) {
        this.playTotalCount = playTotalCount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

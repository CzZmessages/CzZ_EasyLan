package com.lenkeng.udpdemo.bean;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @ClassName: ADBean
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
public class ADBean implements Serializable {
    private int adId;//数据库的id
    private String adDemoName;//大数据单元名字  广告名
    private String startPlayBackFrom;//开始AD时间  代表一整个大数据元的开始与结束区间
    private String endPlayBackFrom;//结束AD时间   代表一整个大数据元的开始与结束区间
    private boolean isEveryDay;//是否每天  true 代表每天  false代表自定义    即发即播与每天都是 填入<0,ResMsg>
    private LinkedHashMap<Integer,ResMsg> weekData;// 每周对应资源数据   <1,resmsg>
    private int priority;//优先级 0普通 1紧急
   private UrgentData urgentData;//插播数据
    private String publishTime;//发布时间 2025-12-18 09:47:51

    public ADBean() {
    }

    public ADBean(int adId, String adDemoName, String startPlayBackFrom, String endPlayBackFrom, boolean isEveryDay, LinkedHashMap<Integer, ResMsg> weekData, int priority, UrgentData urgentData, String publishTime) {
        this.adId = adId;
        this.adDemoName = adDemoName;
        this.startPlayBackFrom = startPlayBackFrom;
        this.endPlayBackFrom = endPlayBackFrom;
        this.isEveryDay = isEveryDay;
        this.weekData = weekData;
        this.priority = priority;
        this.urgentData = urgentData;
        this.publishTime = publishTime;
    }

    public ADBean(String adDemoName, String startPlayBackFrom, String endPlayBackFrom, boolean isEveryDay, LinkedHashMap<Integer, ResMsg> weekData, int priority, UrgentData urgentData, String publishTime) {
        this.adDemoName = adDemoName;
        this.startPlayBackFrom = startPlayBackFrom;
        this.endPlayBackFrom = endPlayBackFrom;
        this.isEveryDay = isEveryDay;
        this.weekData = weekData;
        this.priority = priority;
        this.urgentData = urgentData;
        this.publishTime = publishTime;
    }

    public String getAdDemoName() {
        return adDemoName;
    }

    public void setAdDemoName(String adDemoName) {
        this.adDemoName = adDemoName;
    }

    public String getStartPlayBackFrom() {
        return startPlayBackFrom;
    }

    public void setStartPlayBackFrom(String startPlayBackFrom) {
        this.startPlayBackFrom = startPlayBackFrom;
    }

    public String getEndPlayBackFrom() {
        return endPlayBackFrom;
    }

    public void setEndPlayBackFrom(String endPlayBackFrom) {
        this.endPlayBackFrom = endPlayBackFrom;
    }

    public boolean isEveryDay() {
        return isEveryDay;
    }

    public void setEveryDay(boolean everyDay) {
        isEveryDay = everyDay;
    }

    public LinkedHashMap<Integer, ResMsg> getWeekData() {
        return weekData;
    }

    public void setWeekData(LinkedHashMap<Integer, ResMsg> weekData) {
        this.weekData = weekData;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public UrgentData getUrgentData() {
        return urgentData;
    }

    public void setUrgentData(UrgentData urgentData) {
        this.urgentData = urgentData;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public int getAdId() {
        return adId;
    }

    public void setAdId(int adId) {
        this.adId = adId;
    }
}

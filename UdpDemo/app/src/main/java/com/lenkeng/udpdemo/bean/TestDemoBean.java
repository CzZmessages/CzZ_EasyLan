package com.lenkeng.udpdemo.bean;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @ClassName: TestDemoBean
 * @Author: chenpengchi
 * @Date: 2025/11/27 0027
 * @Description: 大数据单元 包含大资源池 以及所属所有的分段播放信息
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
public class TestDemoBean {//模拟大数据单元
    private String demoName;//大数据单元名  也就是广告名
    private List<ResDemoBean> list;//资源信息集
    private String publishDemoTime; //2025-11-25 17:02:29
    private int priority; // 0 默认等级 1优先 2最高优先级 3属于最顶层
    private LinkedHashMap<String,List<ResDemoBean>> resHashMap;//星期几作为key键值，分段时间资源集作为value   就能代表分段周几对应的资源集
     private List<String> sleepTimes;//睡眠集合 [{}]
    public TestDemoBean() {
    }

    public TestDemoBean(String demoName, List<ResDemoBean> list, String publishDemoTime) {
        this.demoName = demoName;
        this.list = list;
        this.publishDemoTime = publishDemoTime;
    }

    public String getDemoName() {
        return demoName;
    }

    public void setDemoName(String demoName) {
        this.demoName = demoName;
    }

    public List<ResDemoBean> getList() {
        return list;
    }

    public void setList(List<ResDemoBean> list) {
        this.list = list;
    }

    public String getPublishDemoTime() {
        return publishDemoTime;
    }

    public void setPublishDemoTime(String publishDemoTime) {
        this.publishDemoTime = publishDemoTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public LinkedHashMap<String, List<ResDemoBean>> getResHashMap() {
        return resHashMap;
    }

    public void setResHashMap(LinkedHashMap<String, List<ResDemoBean>> resHashMap) {
        this.resHashMap = resHashMap;
    }
}

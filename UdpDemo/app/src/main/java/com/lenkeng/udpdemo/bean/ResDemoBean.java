package com.lenkeng.udpdemo.bean;

import java.util.LinkedHashMap;

/**
 * @ClassName: ResDemoBean
 * @Author: chenpengchi
 * @Date: 2025/11/27 0027
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
public class ResDemoBean {//资源集
    private String testData;//测试数据 资源信息集合     resDataA  json串 [{"file_name":"743980.png","play_time":15,"sequence":0,"zoom":false}]
    private String timetamp;//测试时间     例 json串 {"endDayTime":"23:59","startDayTime":"00:00"}  即开始时间与结束时间 也有可能是别的 比如中午一点到下午两点
    private String resPos;//假设有一个唯一标识；比如时间戳
    private boolean isDefault;//是否属于默认广告
    public ResDemoBean() {
    }

    public ResDemoBean(String testData, String timetamp) {
        this.testData = testData;
        this.timetamp = timetamp;
    }

    public String getTestData() {
        return testData;
    }

    public void setTestData(String testData) {
        this.testData = testData;
    }

    public String getTimetamp() {
        return timetamp;
    }

    public void setTimetamp(String timetamp) {
        this.timetamp = timetamp;
    }

    public String getResPos() {
        return resPos;
    }

    public void setResPos(String resPos) {
        this.resPos = resPos;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

}

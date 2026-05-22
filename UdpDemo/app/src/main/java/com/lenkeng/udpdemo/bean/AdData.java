package com.lenkeng.udpdemo.bean;

import java.io.Serializable;

/**
 * @ClassName: AdData
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
public class AdData implements Serializable {//资源轴
    private String resourceAxis;//实例数据 "[{"file_name":"743980.png","play_time":15,"sequence":0,"zoom":false},{}]"
    private String resourceStartAndTime;// 开始与结束时间
    private int position;//下标

    public AdData(String resourceAxis, String resourceStartAndTime, int position) {
        this.resourceAxis = resourceAxis;
        this.resourceStartAndTime = resourceStartAndTime;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getResourceAxis() {
        return resourceAxis;
    }

    public void setResourceAxis(String resourceAxis) {
        this.resourceAxis = resourceAxis;
    }

    public String getResourceStartAndTime() {
        return resourceStartAndTime;
    }

    public void setResourceStartAndTime(String resourceStartAndTime) {
        this.resourceStartAndTime = resourceStartAndTime;
    }

}

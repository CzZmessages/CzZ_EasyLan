package com.lenkeng.udpdemo.inf;

/**
 * @ClassName: OnPercentChangeListener
 * @Author: chenpengchi
 * @Date: 2026/5/9 0009
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
public interface OnPercentChangeListener {
    /**
     * 百分比变化回调
     * @param percent 当前百分比 0-100
     * @param fromUser true=用户拖拽改变, false=代码调用改变
     */
    void onPercentChanged(int percent, boolean fromUser);

    /**
     * 开始拖拽
     */
    void onDragStart();

    /**
     * 结束拖拽
     * @param finalPercent 松手时的最终百分比
     */
    void onDragEnd(int finalPercent);
}

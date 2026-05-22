package com.lenkeng.udpdemo.utils;

import android.os.SystemClock;
import android.view.View;

/**
 * @ClassName: QulickListener
 * @Author: chenpengchi
 * @Date: 2026/3/16 0016
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
public abstract class QulickListener implements View.OnClickListener {
    private long lastClickTime = 0;
    private static final long CLICK_INTERVAL = 1000; // 防止连续点击的时间间隔（毫秒）

    @Override
    public void onClick(View view) {
        if (canPerformClick()) {
            onNoDoubleClick(view);
            lastClickTime = SystemClock.elapsedRealtime();
        }
    }

    protected abstract void onNoDoubleClick(View v);

    private boolean canPerformClick() {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastClickTime > CLICK_INTERVAL) {
            return true;
        }
        return false;
    }
}

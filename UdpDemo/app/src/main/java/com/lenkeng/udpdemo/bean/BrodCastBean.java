package com.lenkeng.udpdemo.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: BrodCastBean
 * @Author: chenpengchi
 * @Date: 2025/5/9 0009
 * @Description:
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
public class BrodCastBean implements Serializable {
    private String type;
    private List<AddTerminalBean> ids;

    public BrodCastBean(String type, List<AddTerminalBean> ids) {
        this.type = type;
        this.ids = ids;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AddTerminalBean> getIds() {
        return ids;
    }

    public void setIds(List<AddTerminalBean> ids) {
        this.ids = ids;
    }
}

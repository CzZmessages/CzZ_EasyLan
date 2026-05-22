package com.lenkeng.udpdemo.bean;

/**
 * @ClassName: DataProcessBean
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
public class DataProcessBean {
    private String devicesId;
    private String operation_type;
    private String task_status;
    private String value;

    public DataProcessBean(String devicesId, String operation_type, String value) {
        this.devicesId = devicesId;
        this.operation_type = operation_type;
        this.value = value;
    }

    public DataProcessBean(String devicesId, String operation_type, String task_status, String value) {
        this.devicesId = devicesId;
        this.operation_type = operation_type;
        this.task_status = task_status;
        this.value = value;
    }

    public String getDevicesId() {
        return devicesId;
    }

    public void setDevicesId(String devicesId) {
        this.devicesId = devicesId;
    }

    public String getOperation_type() {
        return operation_type;
    }

    public void setOperation_type(String operation_type) {
        this.operation_type = operation_type;
    }

    public String getTask_status() {
        return task_status;
    }

    public void setTask_status(String task_status) {
        this.task_status = task_status;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

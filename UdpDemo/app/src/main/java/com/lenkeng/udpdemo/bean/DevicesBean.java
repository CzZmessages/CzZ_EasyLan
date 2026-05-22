package com.lenkeng.udpdemo.bean;

import java.io.Serializable;

public class DevicesBean implements Serializable {

    //终端名字
    private String name;
    //终端ID
    private String devicesId;
    //剩余内存
    private String devicesMemory;
    //终端网络状态
    private int devicesNetWorkStat;   //0离线 1在线 -1过期

    //当前设备IP
    private String devicesIP;
    //当前CPU温度
    private Double cpuTemp;
    //当前屏幕方向
    private boolean isScreen;
    //设备是否故障
    private boolean isDevStates;
    //设备是否睡眠
    private boolean isDevSleep;
    //设备是否正在播放
    private boolean isOnPlayingSta;
    //设备总容量
    private long availableSize;//设备总容量  availableSize
    private long remainingMermory;//设备剩余容量 remainingMermory

    private String file_json;//文件列表
    private String groupName;//分组名称
    private String devicesPassword;//终端密码
    private boolean isWan;//是否互联网
    private boolean isLan;//是否局域网
    private String imageUrl;//预览路径
    private String timeZone;//时区
    private String recordingUrl;//录像预览路径
    private String carmenRecordingUrl;//摄像头路径
    private String maxResolution;//分辨率 //maxResolution
    private String system_version; //系统版本 system_version
    private String terminal_type;//设备类型
    private String upgrade_version;//需要更新的目标版本号
    private boolean pkg_lasted_update_status;//是否有最新的包
    private boolean is_apply;//当前设备是否具备申请条件
    private boolean is_apply_plus;//当前设备是否具备申请条件

    public DevicesBean() {
    }

    public DevicesBean(String name, String devicesId, String devicesMemory, int devicesNetWorkStat, String devicesIP, Double cpuTemp, boolean isScreen, boolean isDevStates, boolean isOnPlayingSta, long availableSize, long remainingMermory) {
        this.name = name;
        this.devicesId = devicesId;
        this.devicesMemory = devicesMemory;
        this.devicesNetWorkStat = devicesNetWorkStat;
        this.devicesIP = devicesIP;
        this.cpuTemp = cpuTemp;
        this.isScreen = isScreen;
        this.isDevStates = isDevStates;
        this.isOnPlayingSta = isOnPlayingSta;
        this.availableSize = availableSize;
        this.remainingMermory = remainingMermory;
    }

    public DevicesBean(String name, String devicesId, String devicesMemory, int devicesNetWorkStat, String devicesIP, Double cpuTemp, boolean isScreen, boolean isDevStates, boolean isOnPlayingSta) {
        this.name = name;
        this.devicesId = devicesId;
        this.devicesMemory = devicesMemory;
        this.devicesNetWorkStat = devicesNetWorkStat;
        this.devicesIP = devicesIP;
        this.cpuTemp = cpuTemp;
        this.isScreen = isScreen;
        this.isDevStates = isDevStates;
        this.isOnPlayingSta = isOnPlayingSta;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDevicesId() {
        return devicesId;
    }

    public void setDevicesId(String devicesId) {
        this.devicesId = devicesId;
    }

    public String getDevicesMemory() {
        return devicesMemory;
    }

    public void setDevicesMemory(String devicesMemory) {
        this.devicesMemory = devicesMemory;
    }

    public int isDevicesNetWorkStat() {
        return devicesNetWorkStat;
    }

    public void setDevicesNetWorkStat(int devicesNetWorkStat) {
        this.devicesNetWorkStat = devicesNetWorkStat;
    }

    public String getUpgrade_version() {
        return upgrade_version;
    }

    public void setUpgrade_version(String upgrade_version) {
        this.upgrade_version = upgrade_version;
    }

    public String getDevicesIP() {
        return devicesIP;
    }

    public void setDevicesIP(String devicesIP) {
        this.devicesIP = devicesIP;
    }

    public Double getCpuTemp() {
        return cpuTemp;
    }

    public void setCpuTemp(Double cpuTemp) {
        this.cpuTemp = cpuTemp;
    }

    public boolean isScreen() {
        return isScreen;
    }

    public void setScreen(boolean screen) {
        isScreen = screen;
    }

    public boolean isDevStates() {
        return isDevStates;
    }

    public void setDevStates(boolean devStates) {
        isDevStates = devStates;
    }

    public String getRecordingUrl() {
        return recordingUrl;
    }

    public void setRecordingUrl(String recordingUrl) {
        this.recordingUrl = recordingUrl;
    }

    public boolean isOnPlayingSta() {
        return isOnPlayingSta;
    }

    public void setOnPlayingSta(boolean onPlayingSta) {
        isOnPlayingSta = onPlayingSta;
    }

    public long getAvailableSize() {
        return availableSize;
    }

    public void setAvailableSize(long availableSize) {
        this.availableSize = availableSize;
    }

    public long getRemainingMermory() {
        return remainingMermory;
    }

    public void setRemainingMermory(long remainingMermory) {
        this.remainingMermory = remainingMermory;
    }

    public String getFile_json() {
        return file_json;
    }

    public void setFile_json(String file_json) {
        this.file_json = file_json;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDevicesPassword() {
        return devicesPassword;
    }

    public void setDevicesPassword(String devicesPassword) {
        this.devicesPassword = devicesPassword;
    }

    public boolean isWan() {
        return isWan;
    }

    public void setWan(boolean wan) {
        isWan = wan;
    }

    public boolean isLan() {
        return isLan;
    }

    public void setLan(boolean lan) {
        isLan = lan;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMaxResolution() {
        return maxResolution;
    }

    public void setMaxResolution(String maxResolution) {
        this.maxResolution = maxResolution;
    }

    public String getSystem_version() {
        return system_version;
    }

    public void setSystem_version(String system_version) {
        this.system_version = system_version;
    }

    public String getTerminal_type() {
        return terminal_type;
    }

    public void setTerminal_type(String terminal_type) {
        this.terminal_type = terminal_type;
    }

    public boolean isPkg_lasted_update_status() {
        return pkg_lasted_update_status;
    }

    public void setPkg_lasted_update_status(boolean pkg_lasted_update_status) {
        this.pkg_lasted_update_status = pkg_lasted_update_status;
    }

    public boolean isIs_apply() {
        return is_apply;
    }

    public void setIs_apply(boolean is_apply) {
        this.is_apply = is_apply;
    }

    public String getCarmenRecordingUrl() {
        return carmenRecordingUrl;
    }

    public void setCarmenRecordingUrl(String carmenRecordingUrl) {
        this.carmenRecordingUrl = carmenRecordingUrl;
    }

    public int getDevicesNetWorkStat() {
        return devicesNetWorkStat;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public boolean isIs_apply_plus() {
        return is_apply_plus;
    }

    public void setIs_apply_plus(boolean is_apply_plus) {
        this.is_apply_plus = is_apply_plus;
    }

    public boolean isDevSleep() {
        return isDevSleep;
    }

    public void setDevSleep(boolean devSleep) {
        isDevSleep = devSleep;
    }

    @Override
    public String toString() {
        return "DevicesBean{" +
                "name='" + name + '\'' +
                ", devicesId='" + devicesId + '\'' +
                ", devicesMemory='" + devicesMemory + '\'' +
                ", devicesNetWorkStat=" + devicesNetWorkStat +
                ", devicesIP='" + devicesIP + '\'' +
                ", cpuTemp=" + cpuTemp +
                ", isScreen=" + isScreen +
                ", isDevStates=" + isDevStates +
                ", isOnPlayingSta=" + isOnPlayingSta +
                ", availableSize=" + availableSize +
                ", remainingMermory=" + remainingMermory +
                ", file_json='" + file_json + '\'' +
                ", groupName='" + groupName + '\'' +
                ", devicesPassword='" + devicesPassword + '\'' +
                ", isWan=" + isWan +
                ", isLan=" + isLan +
                ", maxResolution='" + maxResolution + '\'' +
                ", system_version='" + system_version + '\'' +
                ", terminal_type='" + terminal_type + '\'' +
                ", upgrade_version='" + upgrade_version + '\'' +
                ", pkg_lasted_update_status=" + pkg_lasted_update_status +
                ", is_apply=" + is_apply +
                '}';
    }
}

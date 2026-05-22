package com.lenkeng.udpdemo.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.lenkeng.udpdemo.bean.ADBean;
import com.lenkeng.udpdemo.bean.AdData;
import com.lenkeng.udpdemo.bean.ResDemoBean;
import com.lenkeng.udpdemo.bean.ResMsg;
import com.lenkeng.udpdemo.bean.SleepData;
import com.lenkeng.udpdemo.bean.TestDemoBean;
import com.lenkeng.udpdemo.bean.UrgentData;
import com.lenkeng.udpdemo.db.ResDBUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: TestResCheckDemo
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
public class TestResCheckDemo {//测试用例
    private ScheduledFuture<?> currentTask; // 用于跟踪当前的任务
    private ScheduledExecutorService executor;
    private Context context;
    private String resPos = "";//临时标识位变量
    private static volatile TestResCheckDemo INSTANCE;

    // 存储测试数据单元 - 使用新的ADBean数据模型
    private List<ADBean> dataUnits = new ArrayList<>();//模拟数据库遍历的数据
    private CallBack callBack;
    private ResDBUtils resDBUtils;//数据库工具类

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    private TestResCheckDemo(Context context) {
        this.context = context;
        this.executor = Executors.newSingleThreadScheduledExecutor();//初始化线程池
        this.resDBUtils = new ResDBUtils(context);
    }

    // 4. 获取单例实例的方法
    public static TestResCheckDemo getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new TestResCheckDemo(context);
        }
        return INSTANCE;
    }

    public void addFiveTestDataInsertDb() {
        addListFiveDataWithNewModel();//生成数据
        resDBUtils.insertAdBeanList(dataUnits);//插入数据
    }

    // 生成5条测试数据 - 使用新的数据模型
    public void addListFiveDataWithNewModel() {
        dataUnits.clear();//先清除先前数据

        // 创建5个ADBean测试数据单元
        // ADBean(String adDemoName, String startPlayBackFrom, String endPlayBackFrom,
        //        boolean isEveryDay, LinkedHashMap<Integer,ResMsg> weekData, int priority, UrgentData urgentData)

        // 数据单元1 - 高优先级，每天播放
        ADBean adBean1 = new ADBean();
        adBean1.setAdDemoName("广告单元A-高优先级");
        adBean1.setStartPlayBackFrom("2025-12-25 17:10");
        adBean1.setEndPlayBackFrom("2025-12-29 18:00");
        adBean1.setEveryDay(false);
        adBean1.setPriority(0); //
        adBean1.setPublishTime("2025-11-25 17:02:29");

        // 创建周数据映射 - 每天播放使用特殊key 0
        LinkedHashMap<Integer, ResMsg> weekData1 = new LinkedHashMap<>();
        List<AdData> adDataList1 = new ArrayList<>();
        AdData adData1 = new AdData(
                "{\"file_name\":\"ad1.png\",\"play_time\":10,\"sequence\":0,\"zoom\":false}",
                "{\"startDayTime\":\"09:00\",\"endDayTime\":\"11:09\"}",
                0
        );
        AdData adDataDouble = new AdData(
                "{\"file_name\":\"ad2.png\",\"play_time\":10,\"sequence\":0,\"zoom\":false}",
                "{\"startDayTime\":\"11:10\",\"endDayTime\":\"12:00\"}",
                1
        );
        adDataList1.add(adData1);
        adDataList1.add(adDataDouble);

        List<SleepData> sleepDataList1 = new ArrayList<>();
        SleepData sleepData1 = new SleepData("22:00", "23:00"); // 当天睡眠时间
        sleepDataList1.add(sleepData1);

        ResMsg resMsg1 = new ResMsg(false, adDataList1, sleepDataList1);
        weekData1.put(1, resMsg1); // 使用0作为key表示每天播放
        adBean1.setWeekData(weekData1);
        dataUnits.add(adBean1);

        // 数据单元2 - 中优先级，每天播放
        ADBean adBean2 = new ADBean();
        adBean2.setAdDemoName("广告单元B-中优先级");
        adBean2.setStartPlayBackFrom("2025-12-24 08:00");  // 修改为年月日时分格式
        adBean2.setEndPlayBackFrom("2025-12-24 20:00");    // 修改为年月日时分格式
        adBean2.setEveryDay(false);
        adBean2.setPriority(0);
        adBean2.setPublishTime("2025-11-25 16:30:00");

        LinkedHashMap<Integer, ResMsg> weekData2 = new LinkedHashMap<>();
        List<AdData> adDataList2 = new ArrayList<>();
        AdData adData2 = new AdData(
                "{\"file_name\":\"ad2.png\",\"play_time\":15,\"sequence\":0,\"zoom\":true}",
                "{\"startDayTime\":\"14:00\",\"endDayTime\":\"16:00\"}",
                0
        );
        adDataList2.add(adData2);

        List<SleepData> sleepDataList2 = new ArrayList<>();
        SleepData sleepData2 = new SleepData("17:00", "18:00"); // 当天睡眠时间
        sleepDataList2.add(sleepData2);

        ResMsg resMsg2 = new ResMsg(false, adDataList2, sleepDataList2);
        weekData2.put(1, resMsg2); // 每天播放使用特殊key 0
        adBean2.setWeekData(weekData2);
        dataUnits.add(adBean2);

        // 数据单元3 - 低优先级，默认全天
        ADBean adBean3 = new ADBean();
        adBean3.setAdDemoName("广告单元C-低优先级");
        adBean3.setStartPlayBackFrom("2025-12-23 00:00");  // 修改为年月日时分格式
        adBean3.setEndPlayBackFrom("2025-12-23 23:59");    // 修改为年月日时分格式
        adBean3.setEveryDay(false);
        adBean3.setPriority(0);
        adBean3.setPublishTime("2025-11-25 15:00:00");

        LinkedHashMap<Integer, ResMsg> weekData3 = new LinkedHashMap<>();
        List<AdData> adDataList3 = new ArrayList<>();
        AdData adData3 = new AdData(
                "{\"file_name\":\"ad3.png\",\"play_time\":20,\"sequence\":0,\"zoom\":false}",
                "{\"startDayTime\":\"00:00\",\"endDayTime\":\"23:59\"}",
                0
        );
        adDataList3.add(adData3);

        List<SleepData> sleepDataList3 = new ArrayList<>();
        SleepData sleepData3 = new SleepData("23:00", "23:30"); // 当天睡眠时间
        sleepDataList3.add(sleepData3);

        ResMsg resMsg3 = new ResMsg(true, adDataList3, sleepDataList3); // 默认资源
        weekData3.put(2, resMsg3); // 周二
        adBean3.setWeekData(weekData3);
        dataUnits.add(adBean3);

        // 数据单元4 - 自定义日期，高优先级
        ADBean adBean4 = new ADBean();
        adBean4.setAdDemoName("广告单元D-自定义日期");
        adBean4.setStartPlayBackFrom("2025-12-22 10:00");  // 修改为年月日时分格式
        adBean4.setEndPlayBackFrom("2025-12-22 22:00");    // 修改为年月日时分格式
        adBean4.setEveryDay(false);
        adBean4.setPriority(2);
        adBean4.setPublishTime("2025-11-24 18:00:00");

        LinkedHashMap<Integer, ResMsg> weekData4 = new LinkedHashMap<>();
        List<AdData> adDataList4 = new ArrayList<>();
        AdData adData4 = new AdData(
                "{\"file_name\":\"ad4.png\",\"play_time\":12,\"sequence\":0,\"zoom\":false}",
                "{\"startDayTime\":\"20:00\",\"endDayTime\":\"22:00\"}",
                0
        );
        adDataList4.add(adData4);

        List<SleepData> sleepDataList4 = new ArrayList<>();
        SleepData sleepData4 = new SleepData("22:30", "23:00"); // 当天睡眠时间
        sleepDataList4.add(sleepData4);

        ResMsg resMsg4 = new ResMsg(false, adDataList4, sleepDataList4);
        weekData4.put(4, resMsg4); // 周四
        adBean4.setWeekData(weekData4);
        dataUnits.add(adBean4);

        // 数据单元5 - 最新发布，低优先级
        ADBean adBean5 = new ADBean();
        adBean5.setAdDemoName("广告单元E-最新发布");
        adBean5.setStartPlayBackFrom("2025-12-21 06:00");  // 修改为年月日时分格式
        adBean5.setEndPlayBackFrom("2025-12-21 23:00");    // 修改为年月日时分格式
        adBean5.setEveryDay(true);
        adBean5.setPriority(0);
        adBean5.setPublishTime("2025-11-25 18:00:00");

        LinkedHashMap<Integer, ResMsg> weekData5 = new LinkedHashMap<>();
        List<AdData> adDataList5 = new ArrayList<>();
        AdData adData5 = new AdData(
                "{\"file_name\":\"ad5.png\",\"play_time\":8,\"sequence\":0,\"zoom\":true}",
                "{\"startDayTime\":\"12:00\",\"endDayTime\":\"14:00\"}",
                0
        );
        adDataList5.add(adData5);

        List<SleepData> sleepDataList5 = new ArrayList<>();
        SleepData sleepData5 = new SleepData("14:30", "15:00"); // 当天睡眠时间
        sleepDataList5.add(sleepData5);

        ResMsg resMsg5 = new ResMsg(false, adDataList5, sleepDataList5);
        weekData5.put(0, resMsg5); // 每天播放使用特殊key 0
        adBean5.setWeekData(weekData5);
        dataUnits.add(adBean5);

        Log.d("TestResCheckDemo", "生成5条新模型测试数据完成");
        // 通过回调通知启动信息
        if (callBack != null) {
            callBack.onMsg(0, "生成5条新模型测试数据完成");
        }
    }


    public String getResGsonString(Gson gson) {
        if (dataUnits.size() == 0) {
            return "[]";
        }
        if (dataUnits == null) {
            return "null";
        }
        return gson.toJson(dataUnits);
    }

    // 模拟轮询检查数据元
    public void checkResDemo() {
        // 开启时检查线程池状态，如已关闭则重新创建
        if (executor == null || executor.isShutdown() || executor.isTerminated()) {
            callBack.onMsg(2, "线程池被清空 重新实例线程池");
            executor = Executors.newSingleThreadScheduledExecutor();
        }
        // 如果已有任务在运行，先取消
        if (currentTask != null && !currentTask.isCancelled()) {
            callBack.onMsg(2, "线程池已有 先取消先前线程");
            currentTask.cancel(false);
        }

        // 生成测试数据
//        addListFiveDataWithNewModel();
        //立即检查
//        checkCurrentResource();

//        // 启动定时任务，每5秒检查一次
//        currentTask = executor.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                checkCurrentResource();
//            }
//        }, 0, 5, TimeUnit.SECONDS);
    }

    public void clearAllDbData() {
        resDBUtils.deleteAllAdBeans();
    }

    public void checkCurrentResource() {
        if (callBack != null) {
            callBack.onMsg(0, "=== 开始检查资源 ===");
        }

        String currentTime = getCurrentTimeString();
        int currentDayOfWeek = getCurrentDayOfWeek();
        //查询数据库的中的所有数据
        dataUnits = resDBUtils.queryAllAdBeans();//获取所有数据
        if (callBack != null) {
            callBack.onMsg(0, "当前时间: " + currentTime + ", 当前星期: " + currentDayOfWeek);
        }

        if (dataUnits == null) {
            callBack.onMsg(0, "当前无数据");
            return;
        }
        if (dataUnits.size() == 0) {
            callBack.onMsg(0, "当前无数据");
            return;
        }

        // 先按发布日期倒序排，再按优先级降序排
        List<ADBean> sortedUnits = new ArrayList<>(dataUnits);
        Collections.sort(sortedUnits, new Comparator<ADBean>() {
            @Override
            public int compare(ADBean o1, ADBean o2) {
                //按插入顺序排序  ID越大越前
                return Integer.compare(o2.getAdId(), o1.getAdId());
            }
        });

        // 遍历每个 ADBean 查找合适资源
        for (ADBean unit : sortedUnits) {
            if (callBack != null) {
                callBack.onMsg(0, "检查数据单元: " + unit.getAdDemoName());
            }

            // 检查是否在播放时间段内
            if (!isInPlayTimeRange(unit, currentTime)) {
                if (callBack != null) {
                    callBack.onMsg(0, "不在播放时间段内: " + unit.getAdDemoName());
                }
                continue;
            }

            // 获取当前星期的资源数据
            LinkedHashMap<Integer, ResMsg> weekData = unit.getWeekData();
            ResMsg currentDayResMsg = null;

            if (unit.isEveryDay()) {
                // 如果是每天播放，使用第一个可用的ResMsg
                if (weekData != null && !weekData.isEmpty()) {
                    currentDayResMsg = weekData.values().iterator().next();
                }
            } else {
                // 如果是自定义日期，查找对应星期的数据
                currentDayResMsg = weekData != null ? weekData.get(currentDayOfWeek) : null;
            }

            if (currentDayResMsg != null) {
                // 检查是否处于睡眠时间
                boolean isInSleep = isSleepTime(currentDayResMsg, currentTime);

                if (isInSleep) {
                    if (callBack != null) {
                        callBack.onMsg(0, "当前数据" + unit.getAdDemoName() + "(处于睡眠模式)");
                    }
//                    continue; // 睡眠时间跳过
                } else {
                    callBack.onMsg(0, "当前数据" + unit.getAdDemoName() + "(不处于睡眠模式)");
                }

                // 检查资源时间段匹配
                List<AdData> adDataList = currentDayResMsg.getResData();
                ResMsg defaultResource = null;

                if (adDataList != null && !adDataList.isEmpty()) {
                    boolean found = false;
                    for (AdData adData : adDataList) {
                        if (currentDayResMsg.isDefault()) {
                            defaultResource = currentDayResMsg;
                            continue; // 默认资源稍后再考虑
                        }
                        if (isTimeMatch(adData.getResourceStartAndTime(), currentTime)) {
                            handleFoundResource(adData, unit, isInSleep);
                            return;
                        }
                    }

                    // 若未找到匹配资源，尝试使用默认资源
                    if (!found && defaultResource != null) {
                        // 使用默认资源的第一个AdData
                        if (defaultResource.getResData() != null && !defaultResource.getResData().isEmpty()) {
                            AdData defaultAdData = defaultResource.getResData().get(0);
                            handleFoundResource(defaultAdData, unit, isInSleep);
                            return;
                        }
                    }
                }
            }
        }

        // 未找到匹配资源
        if (callBack != null) {
            callBack.onMsg(1, "未找到匹配的资源");
        }
    }

    private void handleFoundResource(AdData adData, ADBean unit, boolean isInSleep) {
        String sleepStatus = isInSleep ? "(处于睡眠模式)" : "";
        String resultMsg = "找到匹配资源: " + adData.getResourceAxis() +
                " (来自 " + unit.getAdDemoName() + ")" + " 标识位:(" + adData.getResourceStartAndTime() + ") " + sleepStatus;

        if (callBack != null) {
            String orderPos = adData.getResourceStartAndTime();
            if (!orderPos.trim().equals(resPos)) {
                resPos = orderPos;
                callBack.onMsg(1, "资源检索不一致 触发更新 资源标识为:" + resultMsg);
            } else {
                callBack.onMsg(2, "资源检索一致 不触发更新 资源标识为:");
            }
//            if (position != Integer.parseInt(resPos.isEmpty() ? "-1" : resPos)) {
//                callBack.onMsg(1, resultMsg);
//                resPos = String.valueOf(position);
//            } else {
//                callBack.onMsg(2, "资源检索一致 不触发更新 资源标识为:" + position);
//            }
        }
    }

    // 检查是否在播放时间段内
    private boolean isInPlayTimeRange(ADBean adBean, String currentTime) {
        String startTime = adBean.getStartPlayBackFrom();
        String endTime = adBean.getEndPlayBackFrom();

        if (startTime == null || endTime == null) {
            return true; // 如果没有设置时间范围，则认为始终在播放范围内
        }

        try {
            // 解析年月日时分格式的时间
            long currentTimestamp = parseDateTimeToTimestamp(currentTime);
            long startTimestamp = parseDateTimeToTimestamp(startTime);
            long endTimestamp = parseDateTimeToTimestamp(endTime);

            // 检查当前时间是否在开始时间和结束时间之间
            return currentTimestamp >= startTimestamp && currentTimestamp <= endTimestamp;
        } catch (Exception e) {
            Log.e("TestResCheckDemo", "时间解析错误: " + e.getMessage());
            return false; // 解析失败时返回false
        }
    }

    // 将年月日时分格式的时间转换为时间戳
    private long parseDateTimeToTimestamp(String dateTimeStr) throws Exception {
        // 支持 "HH:mm" 和 "yyyy-MM-dd HH:mm" 两种格式
        SimpleDateFormat sdf;
        if (dateTimeStr.length() == 5) { // HH:mm 格式
            // 如果是时分格式，使用今天的日期
            String today = getCurrentDateString();
            dateTimeStr = today + " " + dateTimeStr;
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        }

        Date date = sdf.parse(dateTimeStr);
        return date.getTime();
    }

    // 获取当前日期字符串 (yyyy-MM-dd)
    private String getCurrentDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    // 检查是否处于睡眠时间
    private boolean isSleepTime(ResMsg resMsg, String currentTime) {
        List<SleepData> sleepDataList = resMsg.getResSleepData();
        if (sleepDataList == null || sleepDataList.isEmpty()) {
            return false; // 没有睡眠时间设置
        }

        for (SleepData sleepData : sleepDataList) {
            String sleepStart = sleepData.getSleepStartTime();
            String sleepEnd = sleepData.getSleepEndTime();

            if (sleepStart != null && sleepEnd != null) {
                int currentMinutes = timeToMinutes(currentTime);
                int startMinutes = timeToMinutes(sleepStart);
                int endMinutes = timeToMinutes(sleepEnd);

                if (startMinutes > endMinutes) {
                    // 跨天睡眠时间
                    if (currentMinutes >= startMinutes || currentMinutes <= endMinutes) {
                        return true;
                    }
                } else {
                    // 普通睡眠时间
                    if (currentMinutes >= startMinutes && currentMinutes <= endMinutes) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 获取当前时间字符串 HH:mm
    private String getCurrentTimeString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

    // 获取当前星期几 (1-7 对应周一到周日)
    private int getCurrentDayOfWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("u", Locale.getDefault()); // u表示1-7的星期
        return Integer.parseInt(sdf.format(new Date()));
    }

    // 检查时间是否匹配
    private boolean isTimeMatch(String timeRangeJson, String currentTime) {
        try {
            // 简单解析时间范围JSON
            String startStr = extractTimeString(timeRangeJson, "startDayTime");
            String endStr = extractTimeString(timeRangeJson, "endDayTime");

            if (startStr == null || endStr == null) return false;

            // 转换为分钟数进行比较
            int currentMinutes = timeToMinutes(currentTime);
            int startMinutes = timeToMinutes(startStr);
            int endMinutes = timeToMinutes(endStr);

            // 处理跨天情况（例如23:00到02:00）
            if (startMinutes > endMinutes) {
                // 跨天时间段
                return currentMinutes >= startMinutes || currentMinutes <= endMinutes;
            } else {
                // 普通时间段
                return currentMinutes >= startMinutes && currentMinutes <= endMinutes;
            }
        } catch (Exception e) {
            callBack.onMsg(2, "时间匹配检查出错: " + e.getMessage()); // 异常使用type=2
            return false;
        }
    }

    // 从JSON字符串中提取时间字段（简化实现）
    private String extractTimeString(String json, String fieldName) {
        try {
            String pattern = "\"" + fieldName + "\":\"([^\"]+)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
            callBack.onMsg(2, "提取字段出错: " + e.getMessage()); // 异常使用type=2
        }
        return null;
    }

    // 将时间字符串转换为分钟数
    private int timeToMinutes(String timeStr) {
        String[] parts = timeStr.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    // 停止检查任务
    public void stopCheck() {
        if (currentTask != null && !currentTask.isCancelled()) {
            currentTask.cancel(true);
        }
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    public interface CallBack {
        void onMsg(int type, String messages);
    }
}

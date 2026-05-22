package com.lenkeng.udpdemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lenkeng.udpdemo.bean.ADBean;
import com.lenkeng.udpdemo.bean.ResMsg;
import com.lenkeng.udpdemo.bean.UrgentData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @ClassName: ResDBUtils
 * @Author: chenpengchi
 * @Date: 2025/12/31 0031
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
public class ResDBUtils {
    private static final String TAG = "ResDBUtils";
    private ADdBOpenHelper dbHelper;
    private Gson gson=new Gson();

    public ResDBUtils(Context context) {
        this.dbHelper = ADdBOpenHelper.getInstance(context);
    }
    /**
     * 插入ADBean数据
     * @param adBean 要插入的ADBean对象
     * @return 插入的行ID，失败返回-1
     */
    public long insertAdBean(ADBean adBean) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        // 设置字段值
        values.put(ADdBOpenHelper.COLUMN_AD_DATA_JSON, new Gson().toJson(adBean.getWeekData()));
        values.put(ADdBOpenHelper.COLUMN_AD_DEMO_NAME, adBean.getAdDemoName());
        values.put(ADdBOpenHelper.COLUMN_PUBLISH_TIME, adBean.getPublishTime());
        values.put(ADdBOpenHelper.COLUMN_PRIORITY, adBean.getPriority());
        values.put(ADdBOpenHelper.COLUMN_START_PLAY_TIME, adBean.getStartPlayBackFrom());
        values.put(ADdBOpenHelper.COLUMN_END_PLAY_TIME, adBean.getEndPlayBackFrom());
        values.put(ADdBOpenHelper.COLUMN_IS_EVERY_DAY, adBean.isEveryDay() ? 1 : 0);

        // 处理urgentData
        if (adBean.getUrgentData() != null) {//有数据存入gson后的数据
            values.put(ADdBOpenHelper.COLUMN_URGENT_DATA_JSON, new Gson().toJson(adBean.getUrgentData()));
        } else {//无数据空字符串
            values.put(ADdBOpenHelper.COLUMN_URGENT_DATA_JSON, "");
        }

        return db.insert(ADdBOpenHelper.TABLE_AD_CAMPAIGNS, null, values);
    }

    /**
     * 批量插入ADBean数据
     * @param adBeanList 要插入的ADBean列表
     * @return 成功插入的数量
     */
    public int insertAdBeanList(List<ADBean> adBeanList) {
        if (adBeanList == null || adBeanList.isEmpty()) {
            return 0;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction(); // 开始事务
        int successCount = 0;

        try {
            for (ADBean adBean : adBeanList) {
                long result = insertAdBean(adBean);
                if (result != -1) {
                    successCount++;
                }
            }
            db.setTransactionSuccessful(); // 设置事务成功
        } finally {
            db.endTransaction(); // 结束事务
        }

        return successCount;
    }
    /**
     * 根据ID删除ADBean数据
     * @param campaignId 要删除的记录ID
     * @return 删除的行数
     */
    public int deleteAdBeanById(long campaignId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(ADdBOpenHelper.TABLE_AD_CAMPAIGNS, ADdBOpenHelper.COLUMN_CAMPAIGN_ID + " = ?",
                new String[]{String.valueOf(campaignId)});
    }

    /**
     * 根据广告名称删除ADBean数据
     * @param adDemoName 广告名称
     * @return 删除的行数
     */
    public int deleteAdBeanByName(String adDemoName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(ADdBOpenHelper.TABLE_AD_CAMPAIGNS, ADdBOpenHelper.COLUMN_AD_DEMO_NAME + " = ?",
                new String[]{adDemoName});
    }

    /**
     * 删除所有ADBean数据
     * @return 删除的行数
     */
    public int deleteAllAdBeans() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(ADdBOpenHelper.TABLE_AD_CAMPAIGNS, null, null);
    }

    /**
     * 根据优先级删除ADBean数据
     * @param priority 优先级
     * @return 删除的行数
     */
    public int deleteAdBeanByPriority(int priority) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(ADdBOpenHelper.TABLE_AD_CAMPAIGNS, ADdBOpenHelper.COLUMN_PRIORITY + " = ?",
                new String[]{String.valueOf(priority)});
    }
    /**
     * 根据ID查询ADBean数据
     * @param campaignId 记录ID
     * @return ADBean对象，未找到返回null
     */
    public ADBean queryAdBeanById(long campaignId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ADdBOpenHelper.TABLE_AD_CAMPAIGNS,
                null,
                ADdBOpenHelper.COLUMN_CAMPAIGN_ID + " = ?",
                new String[]{String.valueOf(campaignId)},
                null, null, null);

        ADBean adBean = null;
        if (cursor != null && cursor.moveToFirst()) {
            adBean = parseCursorToADBean(cursor);
            cursor.close();
        }

        return adBean;
    }

    /**
     * 查询所有ADBean数据
     * @return ADBean列表
     */
    public List<ADBean> queryAllAdBeans() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ADdBOpenHelper.TABLE_AD_CAMPAIGNS,
                null, null, null, null, null,
                ADdBOpenHelper.COLUMN_PUBLISH_TIME + " DESC"); // 按发布时间倒序

        List<ADBean> adBeanList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ADBean adBean = parseCursorToADBean(cursor);
                if (adBean != null) {
                    adBeanList.add(adBean);
                }
            }
            cursor.close();
        }
      LogUtils.d("查询的数据量:"+adBeanList.size());
        return adBeanList;
    }

    /**
     * 根据优先级查询ADBean数据
     * @param priority 优先级
     * @return ADBean列表
     */
    public List<ADBean> queryAdBeanByPriority(int priority) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ADdBOpenHelper.TABLE_AD_CAMPAIGNS,
                null,
                ADdBOpenHelper.COLUMN_PRIORITY + " = ?",
                new String[]{String.valueOf(priority)},
                null, null,
                ADdBOpenHelper.COLUMN_PUBLISH_TIME + " DESC");

        List<ADBean> adBeanList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ADBean adBean = parseCursorToADBean(cursor);
                if (adBean != null) {
                    adBeanList.add(adBean);
                }
            }
            cursor.close();
        }

        return adBeanList;
    }

    /**
     * 根据广告名称查询ADBean数据
     * @param adDemoName 广告名称
     * @return ADBean对象，未找到返回null
     */
    public ADBean queryAdBeanByName(String adDemoName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ADdBOpenHelper.TABLE_AD_CAMPAIGNS,
                null,
                ADdBOpenHelper.COLUMN_AD_DEMO_NAME + " = ?",
                new String[]{adDemoName},
                null, null, null);

        ADBean adBean = null;
        if (cursor != null && cursor.moveToFirst()) {
            adBean = parseCursorToADBean(cursor);
            cursor.close();
        }

        return adBean;
    }

    /**
     * 查询激活状态的ADBean数据
     * @return ADBean列表
     */
    public List<ADBean> queryActiveAdBeans() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ADdBOpenHelper.TABLE_AD_CAMPAIGNS,
                null,
                "is_active = ?",
                new String[]{"1"},
                null, null,
                ADdBOpenHelper.COLUMN_PUBLISH_TIME + " DESC");

        List<ADBean> adBeanList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ADBean adBean = parseCursorToADBean(cursor);
                if (adBean != null) {
                    adBeanList.add(adBean);
                }
            }
            cursor.close();
        }

        return adBeanList;
    }

    /**
     * 统计ADBean数据总数
     * @return 数据总数
     */
    public int countAdBeans() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + ADdBOpenHelper.TABLE_AD_CAMPAIGNS, null);

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }

        return count;
    }

    /**
     * 解析Cursor到ADBean对象
     * @param cursor 游标
     * @return ADBean对象
     */
    private ADBean parseCursorToADBean(Cursor cursor) {
        try {
        int id=cursor.getInt(cursor.getColumnIndexOrThrow(ADdBOpenHelper.COLUMN_CAMPAIGN_ID));
        String adName=cursor.getString(cursor.getColumnIndexOrThrow(ADdBOpenHelper.COLUMN_AD_DEMO_NAME));//广告名
        String adDataJson = cursor.getString(cursor.getColumnIndexOrThrow(ADdBOpenHelper.COLUMN_AD_DATA_JSON));//资源集
        String publishTime= cursor.getString(cursor.getColumnIndexOrThrow(ADdBOpenHelper.COLUMN_PUBLISH_TIME));//发布名称
        String priority= cursor.getString(cursor.getColumnIndexOrThrow(ADdBOpenHelper.COLUMN_PRIORITY));//优先级
        String startTime=cursor.getString(cursor.getColumnIndexOrThrow(ADdBOpenHelper.COLUMN_START_PLAY_TIME));//开始时间
        String endTime=cursor.getString(cursor.getColumnIndexOrThrow(ADdBOpenHelper.COLUMN_END_PLAY_TIME));//结束时间
        boolean isEveryDay=cursor.getString(cursor.getColumnIndexOrThrow(ADdBOpenHelper.COLUMN_IS_EVERY_DAY)).equals("1");//是否为每天
        String adUrgentDataJson=cursor.getString(cursor.getColumnIndexOrThrow(ADdBOpenHelper.COLUMN_URGENT_DATA_JSON));//插播数据
        if (adDataJson == null) {
            return null;
        }
        //解析数据恢复aDataJson
        LinkedHashMap<Integer, ResMsg> weekData=gson.fromJson(adDataJson,new TypeToken<LinkedHashMap<String, ResMsg>>() {}.getType());
        UrgentData urgentData;
        //解析插播数据
        if(adUrgentDataJson.trim().isEmpty()){
            //如果为空 代表无数据插播信息
            urgentData=new UrgentData();
        }else {
            //有数据则json解析
           urgentData=gson.fromJson(adUrgentDataJson, UrgentData.class);
        }
       return new ADBean(id,adName,startTime,endTime,isEveryDay,weekData,Integer.parseInt(priority),urgentData,publishTime);
        } catch (Exception e) {
            LogUtils.e(TAG, "解析ADBean数据失败: " + e.getMessage());
            return null;
        }
    }

}

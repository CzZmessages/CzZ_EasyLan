package com.lenkeng.udpdemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;

/**
 * @ClassName: ADdBOpenHelper
 * @Author: chenpengchi
 * @Date: 2025/12/31 0031
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
public class ADdBOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "AdsDatabase_1.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_AD_CAMPAIGNS = "ad_campaigns";  //表名
    public static final String COLUMN_CAMPAIGN_ID = "campaign_id";    //id
    public static final String COLUMN_AD_DATA_JSON = "ad_data_json";      // 完整ADBean JSON
    public static final String COLUMN_AD_DEMO_NAME = "ad_demo_name";      // 广告名称
    public static final String COLUMN_PUBLISH_TIME = "publish_time";      // 发布时间
    public static final String COLUMN_PRIORITY = "priority";              // 优先级
    public static final String COLUMN_START_PLAY_TIME = "start_play_time";// 开始时间
    public static final String COLUMN_END_PLAY_TIME = "end_play_time";    // 结束时间
    public static final String COLUMN_IS_EVERY_DAY = "is_every_day";      // 每天播放
    public static final String COLUMN_URGENT_DATA_JSON = "urgent_data_json"; // 插播数据JSON
    private static volatile ADdBOpenHelper INSTANCE;
    private static final String SQL_CREATE_TABLE="CREATE TABLE " + TABLE_AD_CAMPAIGNS + " (" +
            COLUMN_CAMPAIGN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_AD_DATA_JSON + " TEXT NOT NULL, " +
            COLUMN_AD_DEMO_NAME + " TEXT, " +
            COLUMN_PUBLISH_TIME + " TEXT, " +
            COLUMN_PRIORITY + " INTEGER, " +
            COLUMN_START_PLAY_TIME + " TEXT, " +
            COLUMN_END_PLAY_TIME + " TEXT, " +
            COLUMN_IS_EVERY_DAY + " INTEGER, " +
            COLUMN_URGENT_DATA_JSON + " TEXT, " +
            "is_active INTEGER DEFAULT 1" +
            ")";
    // 获取单例实例的方法
    public static ADdBOpenHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ADdBOpenHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ADdBOpenHelper(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }
    private ADdBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        LogUtils.e("执行实例化" + DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);

        // 创建索引
        sqLiteDatabase.execSQL("CREATE INDEX idx_ad_demo_name ON " + TABLE_AD_CAMPAIGNS + "(" + COLUMN_AD_DEMO_NAME + ")");
        sqLiteDatabase.execSQL("CREATE INDEX idx_publish_time ON " + TABLE_AD_CAMPAIGNS + "(" + COLUMN_PUBLISH_TIME + ")");
        sqLiteDatabase.execSQL("CREATE INDEX idx_priority ON " + TABLE_AD_CAMPAIGNS + "(" + COLUMN_PRIORITY + ")");
        sqLiteDatabase.execSQL("CREATE INDEX idx_is_every_day ON " + TABLE_AD_CAMPAIGNS + "(" + COLUMN_IS_EVERY_DAY + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        LogUtils.d(i,i1);
        // 版本升级处理
        if (i< i1) {
            onCreate(sqLiteDatabase);
            // 可以根据需要添加版本升级逻辑
        }
    }
}

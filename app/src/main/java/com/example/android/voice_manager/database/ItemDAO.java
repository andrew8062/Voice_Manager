package com.example.android.voice_manager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.voice_manager.alarm.AlarmItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Andrew on 5/1/2015.
 */
public class ItemDAO {

    static final String DATABASE_NAME = "DB";
    static final int DATABASE_VERSION = 1;

    public static final String ALARM_TABLE = "alarm";

    public static final String COLUMN_ALARM_ID = "_id";
    public static final String COLUMN_ALARM_TIME = "alarm_time";
    public static final String COLUMN_ALARM_NAME = "alarm_name";
    public static final String COLUMN_ALARM_VIBRATE = "alarm_vibrate";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + ALARM_TABLE + " ( "
            + COLUMN_ALARM_ID + " INTEGER primary key autoincrement, "
            + COLUMN_ALARM_TIME + " INTEGER(10) NOT NULL, "
            + COLUMN_ALARM_VIBRATE + " INTEGER NOT NULL, "
            + COLUMN_ALARM_NAME + " TEXT NOT NULL)";

    private String TAG = "vm:itemDAO";
    private SQLiteDatabase db;
    private Context mContext;

    public ItemDAO(Context context) {
        mContext = context;
        db = Database.getDatabase(context);


    }

    public void close() {
        db.close();
    }

    public AlarmItem insert(AlarmItem item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ALARM_TIME, item.getTime());
        cv.put(COLUMN_ALARM_VIBRATE, item.isVibrate());
        cv.put(COLUMN_ALARM_NAME, item.getName());
        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(ALARM_TABLE, null, cv);

        // 設定編號
        item.setId(id);
        // 回傳結果
        return item;
    }

    public boolean update(AlarmItem item) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(COLUMN_ALARM_TIME, item.getTime());
        cv.put(COLUMN_ALARM_VIBRATE, item.isVibrate() ? 0 : 1);
        cv.put(COLUMN_ALARM_NAME, item.getName());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = COLUMN_ALARM_ID + "=" + item.getId();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(ALARM_TABLE, cv, where, null) > 0;
    }

    // 讀取所有記事資料
    public List<AlarmItem> getAll() {
        return deleteOutOfDateAlarms();
    }
    private List<AlarmItem> deleteOutOfDateAlarms(){
        List<AlarmItem> result = new ArrayList<>();
        Cursor cursor = db.query(
                ALARM_TABLE, null, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }
        cursor.close();
        Calendar calendar = Calendar.getInstance();
        for (int i=result.size()-1; i>=0; i--){
            if (calendar.getTimeInMillis() > result.get(i).getTime()) {
                delete(result.get(i));
                result.remove(i);
            }
        }
        return result;

    }

    // 把Cursor目前的資料包裝為物件
    private AlarmItem getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        AlarmItem result = new AlarmItem();
        result.setId(cursor.getLong(0));
        result.setTime(cursor.getLong(1));
        result.setVibrate(cursor.getInt(2) == 1);
        result.setName(cursor.getString(3));
        // 回傳結果
        return result;
    }

    // 取得資料數量
    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + ALARM_TABLE, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }
    public boolean delete(AlarmItem item){
        // 格式為「欄位名稱＝資料」
        String where = COLUMN_ALARM_ID + "=" + item.getId();
        // 執行修改資料並回傳修改的資料數量是否成功
        return db.delete(ALARM_TABLE, where,null) > 0;
    }
    public AlarmItem getMostCurrent(){
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_ALARM_ID+", min("+COLUMN_ALARM_TIME+"), "+COLUMN_ALARM_VIBRATE+", "+COLUMN_ALARM_NAME+" from "+ALARM_TABLE, null);
        AlarmItem alarmItem = null;
        if (cursor.moveToNext()){
            alarmItem = new AlarmItem(cursor.getInt(0), cursor.getLong(1), cursor.getInt(2) == 1, cursor.getString(3));
        }
        cursor.close();
        return alarmItem;
    }
    public AlarmItem popMostCurrent(){
        Cursor cursor = db.rawQuery("SELECT "+COLUMN_ALARM_ID+", min("+COLUMN_ALARM_TIME+"), "+COLUMN_ALARM_VIBRATE+", "+COLUMN_ALARM_NAME+" from "+ALARM_TABLE, null);
        AlarmItem alarmItem = null;
        if (cursor.moveToNext()){
            alarmItem = new AlarmItem(cursor.getInt(0), cursor.getLong(1), cursor.getInt(2) == 1, cursor.getString(3));
        }
        cursor.close();
        delete(alarmItem);
        return alarmItem;
    }
    public void deleteDatabase() {
        mContext.deleteDatabase(DATABASE_NAME);
    }
}



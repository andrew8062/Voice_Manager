package com.example.android.voice_manager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Andrew on 5/1/2015.
 */
public class Database extends SQLiteOpenHelper {

    private static SQLiteDatabase database;
    private static String TAG = "vm:Database";
    private Context mContext;

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ItemDAO.CREATE_TABLE);
        Log.d(TAG, "create");
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new Database(context, ItemDAO.DATABASE_NAME,
                    null, ItemDAO.DATABASE_VERSION).getWritableDatabase();
        }
        Log.d(TAG, "getDatabase");
        return database;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ItemDAO.ALARM_TABLE);
        onCreate(db);
        Log.d(TAG, "onUpgrade");

    }

    public void close() {
        Log.d(TAG, "close");
        database.close();
    }
}

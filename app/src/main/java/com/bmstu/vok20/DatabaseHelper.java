package com.bmstu.vok20;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bmstu.vok20.VK.VKMessage;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by anthony on 06.11.16.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String LOG_TAG = DatabaseHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "vok.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<VKMessage, Integer> vkMessageDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, VKMessage.class);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Could not create new table for VK message", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        // TODO: update instead of drop-create
        try {
            TableUtils.dropTable(connectionSource, VKMessage.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Could not upgrade the table for VK message", e);
        }
    }

    public Dao<VKMessage, Integer> getVkMessageDao() throws SQLException {
        if (vkMessageDao == null) {
            vkMessageDao = getDao(VKMessage.class);
        }
        return vkMessageDao;
    }
}

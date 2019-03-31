package com.whf.demolist.database.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by @author WangHaoFei on 2017/11/13.
 */

public class DatabaseHelp extends OrmLiteSqliteOpenHelper{
    private static final String DATABASE_NAME = "gank.db";
    private static final int DATABASE_VERSION = 1;

    private Map<String, Dao> daoMap;
    private static DatabaseHelp databaseHelp;

    private DatabaseHelp(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        daoMap = new HashMap<>();
    }

    public static DatabaseHelp getInstance(Context context) {
        if (databaseHelp == null) {
            synchronized (DatabaseHelp.class) {
                if (databaseHelp == null) {
                    databaseHelp = new DatabaseHelp(context);
                }
            }
        }
        return databaseHelp;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, GankEntry.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, GankEntry.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) {
        D dao;
        if (daoMap.containsKey(clazz.getSimpleName())) {
            dao = (D) daoMap.get(clazz.getSimpleName());
        } else {
            dao = createDao(clazz);
        }
        return dao;
    }

    private <D extends Dao<T, ?>, T> D createDao(Class<T> clazz){
        try {
            D dao = super.getDao(clazz);
            daoMap.put(clazz.getSimpleName(), dao);
            return dao;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void initDataBaseHelp(Context context) {
        getInstance(context);
    }
}

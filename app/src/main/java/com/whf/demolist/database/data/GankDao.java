package com.whf.demolist.database.data;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by @author WangHaoFei on 2017/11/13.
 */

public class GankDao {

    private Dao<GankEntry, ?> dao;

    public GankDao(Context context) {
        dao = DatabaseHelp.getInstance(context).getDao(GankEntry.class);
    }

    public void addGankList(List<GankEntry> gankList) {
        try {
            dao.create(gankList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<GankEntry> queryOrderGankList() {
        try {
            return dao.queryBuilder().orderBy("id", true).query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public GankEntry queryGankFromId(int id) {
        try {
            List<GankEntry> gankList = dao.queryBuilder().where().eq("id", id).query();
            if (gankList.size() == 0) {
                return null;
            } else {
                return gankList.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Boolean deleteGank(GankEntry gankEntry) {
        try {
            dao.delete(gankEntry);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Boolean cleanGankList() {
        try {
            dao.delete(queryOrderGankList());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}

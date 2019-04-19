package com.whf.demolist.database.data;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.whf.demolist.net.GankEntry;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by @author WangHaoFei on 2017/11/13.
 */

public class PersonDao {

    private Dao<DbPerson, ?> dao;

    public PersonDao(Context context) {
        dao = DatabaseHelp.getInstance(context).getDao(DbPerson.class);
    }

    public void addPerson(List<DbPerson> personList) {
        try {
            dao.create(personList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<DbPerson> queryOrderPerson() {
        try {
            return dao.queryBuilder().orderBy("birthday", true).query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public DbPerson queryPersonFromId(int id) {
        try {
            List<DbPerson> personList = dao.queryBuilder().where().eq("idCard", id).query();
            if (personList.size() == 0) {
                return null;
            } else {
                return personList.get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deletePerson(DbPerson person) {
        try {
            return dao.delete(person) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cleanDatabase() {
        try {
            dao.executeRaw("TRUNCATE TABLE ?;",DbPerson.Key.TABLE_NAME);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}

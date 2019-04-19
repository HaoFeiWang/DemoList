package com.whf.demolist.database;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.whf.demolist.R;
import com.whf.demolist.database.data.DatabaseHelp;
import com.whf.demolist.database.data.DbPerson;
import com.whf.demolist.net.GankEntry;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

public class DatabaseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Test_Database_" + DatabaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        findViewById(R.id.tv_raw_schema).setOnClickListener(this);
        findViewById(R.id.tv_query).setOnClickListener(this);
        findViewById(R.id.tv_insert).setOnClickListener(this);
        findViewById(R.id.tv_clean).setOnClickListener(this);
        findViewById(R.id.tv_unique_combo).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_raw_schema:
                queryTableInfo();
                break;
            case R.id.tv_query:
                query();
                break;
            case R.id.tv_insert:
                insert();
                break;
            case R.id.tv_clean:
                clean();
                break;
            case R.id.tv_unique_combo:
                insertUniqueCombo();
                break;
        }
    }

    /**
     * 执行原生sql：查询表结构
     */
    private void queryTableInfo() {
        Dao<DbPerson, Object> dao = DatabaseHelp.getInstance(this).getDao(DbPerson.class);
        GenericRawResults<String[]> results = null;
        try {
            results = dao.queryRaw("PRAGMA table_info (" + DbPerson.Key.TABLE_NAME + ");");
            if (results != null) {
                List<String[]> resultsArray = results.getResults();
                for (int i = 0; i < resultsArray.size(); i++) {
                    Log.d(TAG, "table info = " + Arrays.toString(resultsArray.get(i)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG, "query table info error = " + e);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void query() {
        Dao<DbPerson, Object> dao = DatabaseHelp.getInstance(this).getDao(DbPerson.class);
        try {
            List<DbPerson> result = dao.queryBuilder().query();
            for (int i = 0; result != null && i < result.size(); i++) {
                Log.d(TAG, "query into result = " + result.get(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "query entry fail = " + e);
        }
    }

    private void insert() {
        DbPerson person = new DbPerson();
        person.setIdCard("00000001");
        person.setName("Num 1");
        person.setAge(1);
        person.setSex(1);
        person.setFatherIdCard("NULL");
        person.setMotherIdCard("NULL");
        person.setBirthday(System.currentTimeMillis());

        Dao<DbPerson, Object> dao = DatabaseHelp.getInstance(this).getDao(DbPerson.class);
        try {
            int result = dao.create(person);
            Log.d(TAG, "insert into result = " + result);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "insert entry fail = " + e);
        }
    }

    private void clean() {
        Dao<DbPerson, Object> dao = DatabaseHelp.getInstance(this).getDao(DbPerson.class);
        try {
            int result = dao.deleteBuilder().delete();
            Log.d(TAG, "clean table result = " + result);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "clean table fail = " + e);
        }
    }

    private void insertUniqueCombo() {
        long curTime = System.currentTimeMillis();

        DbPerson person1 = new DbPerson();
        person1.setIdCard("00000002");
        person1.setName("Num 2");
        person1.setAge(3);
        person1.setSex(1);
        person1.setFatherIdCard("NULL");
        person1.setMotherIdCard("00000001");
        person1.setBirthday(curTime);

        DbPerson person2 = new DbPerson();
        person2.setIdCard("00000003");
        person2.setName("Num 3");
        person2.setAge(3);
        person2.setSex(1);
        person2.setFatherIdCard("NULL");
        person2.setMotherIdCard("00000001");
        person2.setBirthday(curTime);

        Dao<DbPerson, Object> dao = DatabaseHelp.getInstance(this).getDao(DbPerson.class);
        try {
            int result1 = dao.create(person1);
            Log.d(TAG, "insert into result = " + result1);
            int result2 = dao.create(person2);
            Log.d(TAG, "insert into result = " + result2);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "insert entry fail = " + e);
        }
    }
}

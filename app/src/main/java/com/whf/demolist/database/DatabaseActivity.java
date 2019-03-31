package com.whf.demolist.database;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.whf.demolist.R;
import com.whf.demolist.database.data.DatabaseHelp;
import com.whf.demolist.database.data.GankDao;
import com.whf.demolist.database.data.GankEntry;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DatabaseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Test_Database_" + DatabaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        findViewById(R.id.tv_raw_schema).setOnClickListener(this);
        findViewById(R.id.tv_query).setOnClickListener(this);
        findViewById(R.id.tv_insert).setOnClickListener(this);

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
        }
    }

    /**
     * 执行原生sql：查询表结构
     */
    private void queryTableInfo() {
        Dao<GankEntry, Object> dao = DatabaseHelp.getInstance(this).getDao(GankEntry.class);
        GenericRawResults<String[]> results = null;
        try {
            results = dao.queryRaw("PRAGMA table_info (tb_gank);");
            if (results != null) {
                List<String[]> resultsArray = results.getResults();
                for (int i = 0;i<resultsArray.size();i++){
                    Log.d(TAG,"table info = "+ Arrays.toString(resultsArray.get(i)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG, "query table info error = " + e);
        }finally {
            if (results!=null){
                try {
                    results.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void query() {
        Dao<GankEntry, Object> dao = DatabaseHelp.getInstance(this).getDao(GankEntry.class);
        try {
            List<GankEntry> result = dao.queryBuilder().query();
            Log.d(TAG, "query into result = " + result);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "query entry fail = " + e);
        }
    }

    private void insert() {
        GankEntry gankEntry1 = new GankEntry();
        //gankEntry1.setId(1);
        gankEntry1.setCreateAt("2017-00-01");
        gankEntry1.setDesc("第一个数据");
        gankEntry1.setPublishedAt("2017-00-01T01:01");
        gankEntry1.setType("Android");
        gankEntry1.setWho("赵钱");
        gankEntry1.setUrl("http://www.google.com/url/1");
        gankEntry1.setUsed(true);
        gankEntry1.setSource("Google");

        Dao<GankEntry, Object> dao = DatabaseHelp.getInstance(this).getDao(GankEntry.class);
        try {
            int result = dao.create(gankEntry1);
            Log.d(TAG, "insert into result = " + result);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "insert entry fail = " + e);
        }
    }
}

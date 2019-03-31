package com.whf.demolist.database;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.j256.ormlite.dao.Dao;
import com.whf.demolist.R;
import com.whf.demolist.database.data.DatabaseHelp;
import com.whf.demolist.database.data.GankDao;
import com.whf.demolist.database.data.GankEntry;

import java.util.ArrayList;

public class DatabaseActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        findViewById(R.id.tv_insert).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_insert:
                insert();
                break;
        }
    }

    private void insert() {
        GankEntry gankEntry1 = new GankEntry();
        gankEntry1.setId(1);
        gankEntry1.setCreateAt("2017-00-01");
        gankEntry1.setDesc("第一个数据");
        gankEntry1.setPublishedAt("2017-00-01T01:01");
        ArrayList<String> image1 = new ArrayList<>();
        image1.add("http://www.baidu.com/image/1");
        gankEntry1.setImages(image1);
        gankEntry1.setType("Android");
        gankEntry1.setWho("赵钱");
        gankEntry1.setUrl("http://www.google.com/url/1");
        gankEntry1.setUsed(true);
        gankEntry1.setSource("Google");

        DatabaseHelp.getInstance(this).getDao(GankEntry.class);
    }
}

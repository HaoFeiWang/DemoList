package com.whf.demolist;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.whf.demolist.common.data.GankDao;
import com.whf.demolist.common.data.GankEntry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 数据库操作的单元测试类
 * Created by @author WangHaoFei on 2017/11/13.
 */
@RunWith(AndroidJUnit4.class)
public class DbUnitTest {

    private static final String TAG = "UnitTest";

    private static Context appContext;
    private static GankDao gankDao;
    private static ArrayList<GankEntry> gankList;

    @BeforeClass
    public static void init() {
        appContext = InstrumentationRegistry.getTargetContext();
        gankDao = new GankDao(appContext);
        initDate();
    }

    private static void initDate() {
        gankList = new ArrayList<>();

        GankEntry gankEntry1 = new GankEntry();
        gankEntry1.setId(1);
        gankEntry1.setCreateAt("2017-00-01");
        gankEntry1.setDesc("第一个数据");
        gankEntry1.setPublishedAt("2017-00-01T01:01");
        gankEntry1.setImages("http://www.baidu.com/image/1");
        gankEntry1.setType("Android");
        gankEntry1.setWho("赵钱");
        gankEntry1.setUrl("http://www.google.com/url/1");
        gankEntry1.setUsed(true);
        gankEntry1.setSource("Google");

        GankEntry gankEntry2 = new GankEntry();
        gankEntry2.setId(2);
        gankEntry2.setCreateAt("2017-00-02");
        gankEntry2.setDesc("第二个数据");
        gankEntry2.setPublishedAt("2017-00-02T02:02");
        gankEntry2.setImages("http://www.baidu.com/image/2");
        gankEntry2.setType("IOS");
        gankEntry2.setWho("孙李");
        gankEntry2.setUrl("http://www.google.com/url/2");
        gankEntry2.setUsed(true);
        gankEntry2.setSource("Facebook");

        GankEntry gankEntry3 = new GankEntry();
        gankEntry3.setId(3);
        gankEntry3.setCreateAt("2017-00-03");
        gankEntry3.setDesc("第三个数据");
        gankEntry3.setPublishedAt("2017-00-03T03:03");
        gankEntry3.setImages("http://www.baidu.com/image/3");
        gankEntry3.setType("Windows");
        gankEntry3.setWho("周吴");
        gankEntry3.setUrl("http://www.google.com/url/3");
        gankEntry3.setUsed(true);
        gankEntry3.setSource("Oracle");

        gankList.add(gankEntry1);
        gankList.add(gankEntry2);
        gankList.add(gankEntry3);
    }


    @Test
    public void testDatabase() {
        List<GankEntry> oneList = gankDao.queryOrderGankList();
        Log.i(TAG, "数据查询结果：" + oneList.toString());
        assertEquals(gankList.toString(), oneList.toString());

        gankDao.addGankList(gankList);
        Log.i(TAG, "数据插入成功");

        List<GankEntry> twoList = gankDao.queryOrderGankList();
        Log.i(TAG, "数据查询结果：" + twoList.toString());
        assertEquals(gankList.toString(), twoList.toString());

        GankEntry gankEntry = gankDao.queryGankFromId(1);
        Log.i(TAG, "ID为1的数据为：" + (gankEntry == null ? "null" : gankEntry.toString()));
        assertEquals(gankList.get(0).toString(), gankEntry.toString());

        gankDao.deleteGank(gankEntry);
        Log.i(TAG, "ID为1的数据删除完成");


        List<GankEntry> threeList = gankDao.queryOrderGankList();
        Log.i(TAG, "数据查询结果：" + threeList.toString());
        gankList.remove(0);
        assertEquals(gankList.toString(), threeList.toString());
    }



    @AfterClass
    public static void release() {
        gankDao.cleanGankList();
    }

}

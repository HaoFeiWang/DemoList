package com.whf.demolist;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.whf.demolist.net.GankEntry;
import com.whf.demolist.net.GankResult;
import com.whf.demolist.net.NetManager;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 网络相关单元测试
 * Created by @author WangHaoFei on 2017/11/13.
 */
@RunWith(AndroidJUnit4.class)
public class NetUnitText {
    private static String TAG = "UnitTest";

    private static NetManager netManager;

    @BeforeClass
    public static void init() {
        netManager = NetManager.getInstance();
    }

    @Test
    public void getNetDataList() {
        netManager.getAndroidData(1)
                .map(GankResult::getResults)
                .subscribe(new Observer<List<GankEntry>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<GankEntry> result) {
                        Log.i(TAG, result.toString());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, e.toString());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}

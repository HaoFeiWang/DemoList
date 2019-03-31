package com.whf.demolist.net;


import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by @author WangHaoFei on 2017/11/13.
 */

public class NetManager {

    private static final String BASE_URL = "http://gank.io/api/";
    private Retrofit retrofit;
    private static NetManager netManager;
    private NetService netService;

    private NetManager() {
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        netService = retrofit.create(NetService.class);
    }

    public static NetManager getInstance() {
        if (netManager == null) {
            synchronized (NetManager.class) {
                if (netManager == null) {
                    netManager = new NetManager();
                }
            }
        }
        return netManager;
    }

    public Observable<GankResult> getAndroidData(int page){
        return netService.getAndroidData(page);
    }


}

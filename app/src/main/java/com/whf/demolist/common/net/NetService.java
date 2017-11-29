package com.whf.demolist.common.net;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by @author WangHaoFei on 2017/11/13.
 */

public interface NetService {

    @GET("data/Android/10/{page}")
    Observable<GankResult> getAndroidData(@Path("page") int page);
}

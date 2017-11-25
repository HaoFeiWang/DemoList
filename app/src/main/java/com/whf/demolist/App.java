package com.whf.demolist;

import android.app.Application;

import com.whf.demolist.common.data.DatabaseHelp;

/**
 * Created by @author WangHaoFei on 2017/11/13.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHelp.initDataBaseHelp(getApplicationContext());
    }
}

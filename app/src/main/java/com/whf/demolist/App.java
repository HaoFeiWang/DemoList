package com.whf.demolist;

import android.app.Application;
import android.util.Log;

import com.whf.demolist.common.data.DatabaseHelp;

/**
 * Created by @author WangHaoFei on 2017/11/13.
 */

public class App extends Application {

    public static final String TAG = "WhfDemoList";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "app create");
        DatabaseHelp.initDataBaseHelp(getApplicationContext());
    }
}

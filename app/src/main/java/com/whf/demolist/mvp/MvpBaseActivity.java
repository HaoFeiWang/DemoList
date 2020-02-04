package com.whf.demolist.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by WHF on 2017/11/5.
 */

public abstract class MvpBaseActivity<M extends IModel,V extends Contract.BaseView,P extends MvpBasePresent<M,V>>
        extends AppCompatActivity implements Contract.BaseView {

    protected P present;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (present == null){
            present = createPresent();
        }
        if (present != null){
            present.attachView((V) this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (present != null) {
            present.detachView();
        }
    }

    public abstract P createPresent();
}

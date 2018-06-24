package com.whf.demolist.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by WHF on 2017/11/5.
 */

public abstract class BaseMvpActivity<V extends BaseMvpView,P extends BaseMvpPresent<V>>
        extends AppCompatActivity implements BaseMvpView {

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
        if (present != null) {
            present.detachView();
        }
        super.onDestroy();
    }

    public abstract P createPresent();
}

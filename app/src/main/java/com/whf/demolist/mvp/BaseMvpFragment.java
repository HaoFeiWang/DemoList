package com.whf.demolist.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by WHF on 2017/11/5.
 */

public abstract class BaseMvpFragment<V extends BaseMvpView, P extends BaseMvpPresent<V>>
        extends Fragment implements BaseMvpView {

    protected P present;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (present == null){
            present = createPresent();
        }
        if (present != null) {
            present.attachView((V) this);
        }
    }

    @Override
    public void onDestroy() {
        if (present != null){
            present.detachView();
        }
        super.onDestroy();
    }

    public abstract P createPresent();
}

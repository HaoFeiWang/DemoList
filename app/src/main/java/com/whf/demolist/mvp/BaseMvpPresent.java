package com.whf.demolist.mvp;

/**
 * Created by WHF on 2017/11/5.
 */

public class BaseMvpPresent<V extends BaseMvpView>{

    protected V view;

    protected void attachView(V view){
        this.view = view;
    }

    protected void detachView(){
        this.view = null;
    }
}

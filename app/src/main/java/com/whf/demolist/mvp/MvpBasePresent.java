package com.whf.demolist.mvp;

/**
 * Created by WHF on 2017/11/5.
 */

public abstract class MvpBasePresent<M extends IModel,V extends IView>{

    protected V view;
    protected M model;

    protected MvpBasePresent(){
        this.model = createModel();
    }

    protected void attachView(V view){
        this.view = view;
    }

    protected void detachView(){
        this.view = null;
    }

    protected abstract M createModel();

    protected M getModel() {
        return model;
    }

    protected boolean isViewAttached() {
        return view != null;
    }
}


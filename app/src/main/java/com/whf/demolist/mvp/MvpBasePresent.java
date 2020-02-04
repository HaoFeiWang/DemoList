package com.whf.demolist.mvp;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by WHF on 2017/11/5.
 */

public abstract class MvpBasePresent<M extends IModel, V extends Contract.BaseView> implements Contract.BasePresenter {

    protected V view;
    protected M model;
    private V proxyView;

    protected MvpBasePresent() {
        this.model = createModel();
        this.createViewProxy();
    }

    @SuppressWarnings("unchecked")
    private void createViewProxy() {
        proxyView = (V) Proxy.newProxyInstance(view.getClass().getClassLoader(), view.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                if (view == null) {
                    return null;
                }
                return method.invoke(view, objects);
            }
        });
    }

    private V getView(){
        return proxyView;
    }

    protected void attachView(V view) {
        this.view = view;
    }

    protected void detachView() {
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


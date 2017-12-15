package com.whf.demolist.binder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.whf.demolist.App;
import com.whf.demolist.IRemote;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RemoteService extends Service {

    private int count = 0;
    private Disposable disposable;

    private IRemote.Stub remoteStub = new IRemote.Stub() {
        @Override
        public int getCount() throws RemoteException {
            return count;
        }
    };

    public RemoteService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return remoteStub;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(App.TAG, "remote service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(App.TAG, "remote service onStartCommand");
        countAdd();
        return START_NOT_STICKY;
    }

    private void countAdd() {
        Observable.interval(0, 3000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        RemoteService.this.disposable = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        count++;
                        Log.i(App.TAG, "remote service count = " + count);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public int getCount() {
        return count;
    }
}

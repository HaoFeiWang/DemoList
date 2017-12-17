package com.whf.demolist.binder;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.whf.demolist.App;
import com.whf.demolist.IRemote;
import com.whf.demolist.R;

public class ClientActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnStartService;
    private Button btnStopService;
    private Button btnBindService;
    private Button btnServiceData;

    private Button btnStartRemoteService;
    private Button btnStopRemoteService;
    private Button btnBindRemoteService;
    private Button btnRemoteServiceData;

    private LocalService service;
    private IRemote remoteService;
    private ServiceConnection serviceConnection;
    private ServiceConnection serviceRemoteConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        btnStartService = findViewById(R.id.btn_start_service);
        btnStopService = findViewById(R.id.btn_stop_service);
        btnBindService = findViewById(R.id.btn_bind_service);
        btnServiceData = findViewById(R.id.btn_service_data);

        btnStartRemoteService = findViewById(R.id.btn_start_remote_service);
        btnStopRemoteService = findViewById(R.id.btn_stop_remote_service);
        btnBindRemoteService = findViewById(R.id.btn_bind_remote_service);
        btnRemoteServiceData = findViewById(R.id.btn_service_remote_data);

        btnStartService.setOnClickListener(this);
        btnStopService.setOnClickListener(this);
        btnBindService.setOnClickListener(this);
        btnServiceData.setOnClickListener(this);

        btnStartRemoteService.setOnClickListener(this);
        btnStopRemoteService.setOnClickListener(this);
        btnBindRemoteService.setOnClickListener(this);
        btnRemoteServiceData.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.btn_start_service:
                startService(new Intent(this, LocalService.class));
                break;
            case R.id.btn_stop_service:
                if (serviceConnection != null) {
                    unbindService(serviceConnection);
                }
                stopService(new Intent(this, LocalService.class));
                break;
            case R.id.btn_bind_service:
                bindLocalService();
                break;



            case R.id.btn_start_remote_service:
                startService(new Intent(this, RemoteService.class));
                break;
            case R.id.btn_stop_remote_service:
                if (serviceRemoteConnection != null) {
                    unbindService(serviceRemoteConnection);
                }
                stopService(new Intent(this, RemoteService.class));
                break;
            case R.id.btn_bind_remote_service:
                bindRemoteService();
                break;


            case R.id.btn_service_data:
                Log.i(App.TAG, "client local count = " + service.getCount());
            case R.id.btn_service_remote_data:
                try {
                    int count = remoteService.getCount();
                    Log.i(App.TAG, "client remote count = " + count);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 非同一进程的服务
     */
    private void bindRemoteService() {
        serviceRemoteConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(App.TAG, "bind remote service");
                ClientActivity.this.remoteService = IRemote.Stub.asInterface(service);
                //设置死亡代理
                try {
                    service.linkToDeath(deathRecipient,0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        bindService(new Intent(ClientActivity.this, RemoteService.class), serviceRemoteConnection, BIND_AUTO_CREATE);
    }

    /**
     * 同一进程服务
     */
    private void bindLocalService() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(App.TAG, "bind local service");
                ClientActivity.this.service = ((LocalService.LocalBinder) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        bindService(new Intent(ClientActivity.this, LocalService.class), serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * Binder死亡代理
     */
    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (remoteService==null){
                return;
            }
            remoteService.asBinder().unlinkToDeath(deathRecipient,0);
            remoteService = null;
            //重新绑定远程服务
            bindRemoteService();
        }
    };
}

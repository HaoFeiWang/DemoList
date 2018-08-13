package com.whf.demolist.wifi;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.whf.demolist.R;

/**
 * Created by @author WangHaoFei on 2018/8/13.
 */

public class WifiHolder extends RecyclerView.ViewHolder {

    TextView tvSsid;
    TextView tvRssi;
    TextView tvSecurity;

    public WifiHolder(View itemView) {
        super(itemView);
        tvSsid = itemView.findViewById(R.id.tv_ssid);
        tvRssi = itemView.findViewById(R.id.tv_rssi);
        tvSecurity = itemView.findViewById(R.id.tv_security);
    }
}

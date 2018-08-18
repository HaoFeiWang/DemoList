package com.whf.demolist.bluetooth.ble;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.whf.demolist.R;

/**
 * Created by @author WangHaoFei on 2018/8/18.
 */

public class BluetoothListHolder extends RecyclerView.ViewHolder {

    public TextView tvName;
    public TextView tvAddress;
    public TextView tvRssi;

    public BluetoothListHolder(View itemView) {
        super(itemView);

        tvName = itemView.findViewById(R.id.tv_name);
        tvAddress = itemView.findViewById(R.id.tv_address);
        tvRssi = itemView.findViewById(R.id.tv_rssi);
    }
}

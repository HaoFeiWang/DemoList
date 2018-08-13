package com.whf.demolist.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whf.demolist.R;

import java.util.List;

/**
 * Created by @author WangHaoFei on 2018/8/13.
 */

public class WifiAdapter extends RecyclerView.Adapter<WifiHolder> {

    private static final String TAG = "WIFI_TEST_" + WifiAdapter.class.getSimpleName();

    private LayoutInflater layoutInflater;
    private List<ScanResult> wifiList;
    private OnItemClickListener onItemClickListener;

    public WifiAdapter(Context context, List<ScanResult> wifiList) {
        this.layoutInflater = LayoutInflater.from(context);
        this.wifiList = wifiList;
    }

    @Override
    public WifiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_wifi, parent, false);
        return new WifiHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WifiHolder holder, int position) {
        ScanResult scanResult = wifiList.get(position);
        Log.d(TAG, "ssid = " + scanResult.SSID + " bssid = " + scanResult.BSSID + " level = " + scanResult.level);

        holder.tvSsid.setText(scanResult.SSID);
        holder.tvRssi.setText(String.valueOf(scanResult.level));
        holder.tvSecurity.setText(getSecurity(scanResult));

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener!=null){
                onItemClickListener.onItemClickListener();
            }
        });
    }

    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    private String getSecurity(ScanResult scanResult) {
        String capabilities = scanResult.capabilities;

        if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
            Log.i(TAG, "security is wpa !");
            return "wpa";
        } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
            Log.i(TAG, "security is wep !");
            return "wep";
        } else {
            Log.i(TAG, "no security !");
            return "non";
        }
    }

    public void setWifiList(List<ScanResult> wifiList) {
        this.wifiList = wifiList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    interface OnItemClickListener {
        void onItemClickListener();
    }
}

package com.whf.demolist.bluetooth.basic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whf.demolist.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by @author WangHaoFei on 2018/8/18.
 */

public class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothListHolder> {

    private static final String TAG = Constants.TAG + BluetoothListAdapter.class;

    private LayoutInflater layoutInflater;
    private List<BluetoothInfo> bluetoothInfoList;
    private OnItemClickListener onItemClickListener;

    public BluetoothListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        bluetoothInfoList = new ArrayList<>();
    }

    @Override
    public BluetoothListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_bluetooth, parent, false);
        return new BluetoothListHolder(view);
    }

    @Override
    public void onBindViewHolder(BluetoothListHolder holder, int position) {
        BluetoothInfo bluetoothInfo = bluetoothInfoList.get(position);
        holder.tvName.setText(bluetoothInfo.getName());
        holder.tvAddress.setText(bluetoothInfo.getAddress());
        holder.tvRssi.setText(bluetoothInfo.getRssi());

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(bluetoothInfo.getAddress());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return bluetoothInfoList.size();
    }

    public void addBluetoothData(BluetoothInfo bluetoothInfo) {
        for (int i = 0; i < bluetoothInfoList.size(); i++) {
            if (bluetoothInfoList.get(i).getAddress().equals(bluetoothInfo.getAddress())) {
                Log.d(TAG,"change bluetooth!");
                bluetoothInfoList.set(i, bluetoothInfo);
                notifyItemChanged(i);
                return;
            }
        }

        Log.d(TAG,"add bluetooth!");
        bluetoothInfoList.add(bluetoothInfo);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    interface OnItemClickListener {
        void onItemClick(String address);
    }
}


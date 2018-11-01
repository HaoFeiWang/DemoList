package com.whf.demolist.bluetooth.ad;

import android.os.ParcelUuid;

/**
 * 广播策略分类
 * Created by @author WangHaoFei on 2018/10/31.
 */
@SuppressWarnings("WeakerAccess")
public class AdStrategyClassify {

    public static final ParcelUuid UUID_START = ParcelUuid.fromString("00002363-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid UUID_STOP = ParcelUuid.fromString("00002364-0000-1000-8000-00805F9B34FB");
    public static final ParcelUuid UUID_RESPONSE = ParcelUuid.fromString("00002365-0000-1000-8000-00805F9B34FB");

    public static StrategyAd strategyClassify(ParcelUuid uuid) {
        if (UUID_START.equals(uuid)) {
            return StrategyAdShake.getInstance();
        } else if (UUID_STOP.equals(uuid)) {
            return StrategyAdStatic.getInstance();
        } else if (UUID_RESPONSE.equals(uuid)) {
            return StrategyAdResponse.getInstance();
        }
        return null;
    }
}

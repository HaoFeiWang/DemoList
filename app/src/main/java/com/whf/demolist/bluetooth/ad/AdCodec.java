package com.whf.demolist.bluetooth.ad;

import android.bluetooth.le.AdvertiseData;
import android.os.ParcelUuid;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.ArraySet;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 编解码器
 * Created by @author WangHaoFei on 2018/10/31.
 */

@SuppressWarnings("WeakerAccess")
public class AdCodec {

    private static final String TAG = "BLE_TEST_" + AdCodec.class.getSimpleName();

    public static final int MANUFACTURE_ID = 28;
    public static final byte[] MANUFACTURE_DATA = new byte[]{35};

    public static final int AD_COUNT = 9;
    public static final int AD_HEAD_LENGTH = 2;
    public static final int AD_DATA_LENGTH = 20;

    public static AdvertiseData createAdData(ParcelUuid uuid, byte[] segment) {
        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder()
                //类型和长度占用2个字节，id占用2个字节，data占用1个字节
                .addManufacturerData(MANUFACTURE_ID, MANUFACTURE_DATA)
                .setIncludeTxPowerLevel(false)
                .setIncludeDeviceName(false)
                //类型和长度占用2个字节，uuid占用2
                .addServiceData(uuid, segment);
        return dataBuilder.build();
    }

    public static List<byte[]> encode(String content) {
        byte[] contentBytes = content.getBytes();
        Log.d(TAG, "content " + content + " bytes = " + Arrays.toString(contentBytes));

        int length = contentBytes.length;
        byte lengthByte = Integer.valueOf(length).byteValue();
        if (length > AD_COUNT * AD_DATA_LENGTH) {
            Log.d(TAG, "encode content length too large!");
            return null;
        }

        ArrayList<byte[]> encodeList = new ArrayList<>();
        for (int i = 0, k = 0; i < length; i += AD_DATA_LENGTH, k++) {
            byte[] section = new byte[AD_DATA_LENGTH + AD_HEAD_LENGTH];
            section[0] = lengthByte;
            section[1] = Integer.valueOf(k).byteValue();
            int copyLen = Math.min(length - i, AD_DATA_LENGTH);
            System.arraycopy(contentBytes, i, section, AD_HEAD_LENGTH, copyLen);
            encodeList.add(section);
        }
        return encodeList;
    }

    public static void decode(String address, byte[] content,
                              ArrayMap<String, ParseAdData> achieveAdData) {

        ParseAdData parseAdData = achieveAdData.get(address);
        if (parseAdData != null && !TextUtils.isEmpty(parseAdData.getContent())) {
            return;
        }

        int length = content[0];
        int position = content[1];

        if (parseAdData == null) {
            parseAdData = new ParseAdData();
            achieveAdData.put(address, parseAdData);
        }

        if (parseAdData.getByteArray() == null) {
            parseAdData.setByteArray(new byte[length]);
            parseAdData.setOwnedSegment(new ArraySet<>());
        }

        byte[] adData = parseAdData.getByteArray();
        ArraySet<Integer> ownedSegment = parseAdData.getOwnedSegment();

        if (!ownedSegment.contains(position)) {
            ownedSegment.add(position);
            int destPos = position * AdCodec.AD_DATA_LENGTH;
            int copyLen = Math.min(adData.length - destPos, AdCodec.AD_DATA_LENGTH);
            System.arraycopy(content, AdCodec.AD_HEAD_LENGTH, adData, destPos, copyLen);
        }

        double segmentNum = Math.ceil(length / (float) AdCodec.AD_DATA_LENGTH);
        if (ownedSegment.size() == segmentNum) {
            parseAdData.setContent(new String(parseAdData.getByteArray()));
        }
    }
}

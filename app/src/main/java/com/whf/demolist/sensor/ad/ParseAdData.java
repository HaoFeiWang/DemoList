package com.whf.demolist.sensor.ad;


import android.support.v4.util.ArraySet;

import java.util.Arrays;


/**
 * 广播数据解析
 * Created by @author WangHaoFei on 2018/10/31.
 */
@SuppressWarnings("WeakerAccess")
public class ParseAdData {

    private String content;
    private byte[] byteArray;
    private ArraySet<Integer> ownedSegment;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getByteArray() {
        return byteArray;
    }

    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public ArraySet<Integer> getOwnedSegment() {
        return ownedSegment;
    }

    public void setOwnedSegment(ArraySet<Integer> ownedSegment) {
        this.ownedSegment = ownedSegment;
    }

    @Override
    public String toString() {
        return "ParseAdData{" +
                "content='" + content + '\'' +
                ", byteArray=" + Arrays.toString(byteArray) +
                ", ownedSegment=" + ownedSegment +
                '}';
    }
}

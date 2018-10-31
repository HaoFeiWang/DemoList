package com.whf.demolist.sensor.ad;

/**
 * 摇动设备
 * Created by @author WangHaoFei on 2018/10/31.
 */

public class ShakeDevice {

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ShakeDevice{" +
                "content='" + content + '\'' +
                '}';
    }
}

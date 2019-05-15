package com.whf.demolist.gson;

import com.google.gson.Gson;

import org.junit.Test;

public class TestGson {

    private String jsonStr = "{\"appName\":\"天才秀\",\"desc\":\"我获得了一个银宝箱，快来帮我开箱吧\",\"extInfo\":\"{\\\"boxInfo\\\":{\\\"boxAction\\\":1,\\\"boxId\\\":\\\"4002d9684eb04efcb2a48be1f6bd73f5\\\",\\\"boxType\\\":1,\\\"cur\\\":0,\\\"cutBottom\\\":92,\\\"cutTop\\\":32,\\\"debrisId\\\":203,\\\"max\\\":10,\\\"name\\\":\\\"炫彩喷雾\\\",\\\"paddingBottom\\\":48,\\\"paddingEnd\\\":0,\\\"paddingStart\\\":0,\\\"paddingTop\\\":0,\\\"url\\\":\\\"http://watchgame.okii.com/virtual/self/bg_girlsday_spray_1.png\\\"},\\\"name\\\":\\\"Z6A11\\\",\\\"openId\\\":\\\"D41D9A247D2160647FEC7C40E2725BB11F600E5085E85D32B3428AF44CCE30C187D45675F3C3C4C49F29D6D84F64D7F4\\\",\\\"start\\\":1557459034286}\",\"packageName\":\"com.xtc.virtualself\",\"targetClass\":\"com.xtc.virtualself.module.share.ShareActivity\",\"targetPackage\":\"com.xtc.virtualself\",\"transaction\":\"1557459034333e951c1e0-e4e6-4701-9744-2e40284dad2b\",\"customParamMap\":{},\"localPath\":\"/mnt/sdcard/xtc/ibwatch/weichat/weichat/share/1557459038263.jpg\",\"photoHasDownload\":true,\"photoHasUpload\":true,\"smallPic\":{\"downloadUrl\":\"http://qiniu.video.down.im.okii.com/im_spic_04B8AFEB5B9359038CFB512F354BD03E.webp?e=1558063838&token=8AL1G4RdHBLOGsNafux4Ac5_JeRoNM3fVyJ2ZBEF:QxSc72eq1Kq1ugXQ22TbAfI350I=\",\"key\":\"im_spic_04B8AFEB5B9359038CFB512F354BD03E.webp\",\"urlDeadline\":1558063839400},\"source\":{\"downloadUrl\":\"http://qiniu.video.down.im.okii.com/im_pic_04B8AFEB5B9359038CFB512F354BD03E.webp?e=1558063838&token=8AL1G4RdHBLOGsNafux4Ac5_JeRoNM3fVyJ2ZBEF:ZaLWI0OiNfholU0kdmHQcbRkAGM=\",\"height\":360,\"key\":\"im_pic_04B8AFEB5B9359038CFB512F354BD03E.webp\",\"urlDeadline\":1558063839400,\"width\":320},\"type\":\"1\",\"zone\":\"z0\",\"conversationId\":131524661,\"conversationType\":3,\"createTime\":1557459038428,\"disss\":false,\"isDelete\":false,\"isSendMsg\":true,\"msgId\":\"2cec353eab084785867eecabc6b57a57\",\"msgStatus\":2,\"msgType\":122,\"receiveTime\":0,\"senderAccountType\":1,\"senderImAccountId\":131803475,\"senderName\":\"Z6E7\",\"senderWatchId\":\"5d9422ef572b459296dccfe3c38c674531425191\",\"showTime\":\"11:30\",\"syncKey\":1200}";

    @Test
    public void testGson(){
        ShareAppMsg shareAppMsg = new Gson().fromJson(jsonStr,ShareAppMsg.class);
        System.out.println("share app msg = "+shareAppMsg);
    }
}

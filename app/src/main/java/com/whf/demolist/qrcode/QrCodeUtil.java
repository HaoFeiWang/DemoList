package com.whf.demolist.qrcode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码工具类
 * Created by @author WangHaoFei on 2017/12/1.
 */

public class QrCodeUtil {

    public static Bitmap createQrCode(String content, int widthPix, int heightPix, String filePath) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }

        //配置参数
        Map<EncodeHintType, Object> hints = new HashMap<>(10);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置空白边距的宽度
        hints.put(EncodeHintType.MARGIN, 1);

        try {
            //图像数据转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints);

            // 按照二维码的算法，逐个生成二维码的图片，两个for循环是图片横列扫描的结果
            int[] pixels = new int[widthPix * heightPix];
            for (int y = 0; y < heightPix; y++) {
                for (int x = 0; x < widthPix; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * widthPix + x] = 0xff000000;
                    } else {
                        pixels[y * widthPix + x] = 0xffffffff;
                    }
                }
            }

            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);

//            //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(filePath));
//            return BitmapFactory.decodeFile(filePath);

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}

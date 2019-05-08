package main.cn.edu.sicnu.itop4412_projects01.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 图像工具类
 *
 * Created by Kaier on 2019/5/7.
 */
public class BitmapUtil {
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 设置为ture,只读取图片的大小，不把它加载到内存中去
//        BitmapFactory.decodeFile(filePath, options);

//        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, 480, 800);// 此处，选取了480x800分辨率的照片
//
//        // Decode bitmap with inSampleSize set
//        options.inJustDecodeBounds = false;// 处理完后，同时需要记得设置为false

        return BitmapFactory.decodeFile(filePath, options);
    }

}
